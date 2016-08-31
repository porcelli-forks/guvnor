package org.guvnor.ala.wildfly.executor.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.guvnor.ala.build.maven.config.MavenBuildConfig;
import org.guvnor.ala.build.maven.config.MavenBuildExecConfig;
import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.build.maven.executor.MavenBuildConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenProjectConfigExecutor;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.config.SourceConfig;

import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.registry.local.InMemoryBuildRegistry;
import org.guvnor.ala.registry.local.InMemoryRuntimeRegistry;
import org.guvnor.ala.registry.local.InMemorySourceRegistry;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.source.git.config.GitConfig;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;

import static java.util.Arrays.*;
import java.util.function.Consumer;
import org.arquillian.cube.CubeController;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.docker.impl.requirement.RequiresDockerMachine;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import static org.guvnor.ala.pipeline.StageUtil.*;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.access.impl.WildflyAccessInterfaceImpl;
import org.guvnor.ala.wildfly.config.ContextAwareWildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.config.WildflyProviderConfig;
import org.guvnor.ala.wildfly.executor.WildflyProviderConfigExecutor;
import org.guvnor.ala.wildfly.executor.WildflyRuntimeExecExecutor;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.guvnor.ala.wildfly.service.WildflyRuntimeManager;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.runner.RunWith;

/**
 * TODO: update me
 */
@RunWith( ArquillianConditionalRunner.class )
@RequiresDockerMachine( name = "default" )
public class WildflyExecutorTest {

    private static final String CONTAINER = "swarm";
    private File tempPath;
    
    @HostIp
    private String ip;

    @ArquillianResource
    private CubeController cc;

    @Before
    public void setUp() {
        try {
            tempPath = Files.createTempDirectory( "xxx" ).toFile();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly( tempPath );
    }
    
    @Test
    @InSequence( 1 )
    public void shouldBeAbleToCreateAndStartTest() {
        cc.create( CONTAINER );
        cc.start( CONTAINER );
    }

    @Test
    @InSequence( 2 )
    public void testAPI() {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();
        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final WildflyAccessInterface wildflyAccessInterface = new WildflyAccessInterfaceImpl();

        final Stage<Input, SourceConfig> sourceConfig = config( "Git Source", ( s ) -> new GitConfig() {
        } );
        final Stage<SourceConfig, ProjectConfig> projectConfig = config( "Maven Project", ( s ) -> new MavenProjectConfig() {
        } );
        final Stage<ProjectConfig, BuildConfig> buildConfig = config( "Maven Build Config", ( s ) -> new MavenBuildConfig() {
        } );
        
        final Stage<BuildConfig, BinaryConfig> buildExec = config( "Maven Build", ( s ) -> new MavenBuildExecConfig() {
        } );
        final Stage<BinaryConfig, ProviderConfig> providerConfig = config( "Wildfly Provider Config", ( s ) -> new WildflyProviderConfig() {
        } );

       
        final Stage<ProviderConfig, RuntimeConfig> runtimeExec = config( "Wildfly Runtime Exec", ( s ) -> new ContextAwareWildflyRuntimeExecConfig() );

        final Pipeline pipe = PipelineFactory
                .startFrom( sourceConfig )
                .andThen( projectConfig )
                .andThen( buildConfig )
                .andThen( buildExec )
                .andThen( providerConfig )
                .andThen( runtimeExec ).buildAs( "my pipe" );
        WildflyRuntimeExecExecutor wildflyRuntimeExecExecutor = new WildflyRuntimeExecExecutor( runtimeRegistry, wildflyAccessInterface );
        final PipelineExecutor executor = new PipelineExecutor( asList( new GitConfigExecutor( sourceRegistry ),
                                                                        new MavenProjectConfigExecutor( sourceRegistry ),
                                                                        new MavenBuildConfigExecutor(),
                                                                        new MavenBuildExecConfigExecutor( buildRegistry ),
                                                                        new WildflyProviderConfigExecutor( runtimeRegistry ),
                                                                        wildflyRuntimeExecExecutor) );
        
        MyConsumer c = new MyConsumer();
        executor.execute( new Input() {{
            put( "repo-name", "drools-workshop" );
            put( "branch", "master" );
            put( "out-dir", tempPath.getAbsolutePath() );
            put( "origin", "https://github.com/salaboy/drools-workshop" );
            put( "project-dir", "drools-webapp-example" );
            put( "wildfly-user", "admin" );
            put( "wildfly-password", "Admin#70365" );
            put( "host",  ip);
            put( "port",  "8080");
            put( "management-port",  "9990");
            
        }}, pipe, c );
        Runtime runtime = c.getRuntime();
        assertTrue(runtime instanceof WildflyRuntime);
        
         WildflyRuntime wildflyRuntime = ( WildflyRuntime ) runtime;
       
        WildflyRuntimeManager runtimeManager = new WildflyRuntimeManager( runtimeRegistry, wildflyAccessInterface );

        runtimeManager.start( wildflyRuntime );

        assertEquals( "Running", wildflyRuntime.getState().getStatus() );
        runtimeManager.stop( wildflyRuntime );
        assertEquals( "Stopped", wildflyRuntime.getState().getStatus() );

        wildflyRuntimeExecExecutor.destroy( wildflyRuntime );

        wildflyAccessInterface.dispose();
    }
    
    @Test
    @InSequence( 3 )
    public void shouldBeAbleToStopAndDestroyTest() {
        cc.stop( CONTAINER );
        cc.destroy( CONTAINER );
    }
    
    class MyConsumer implements Consumer<Runtime> {

        private Runtime r;

        @Override
        public void accept( Runtime r ) {
            this.r = r;
        }

        public Runtime getRuntime() {
            return this.r;
        }
    }
    
    
   
}
