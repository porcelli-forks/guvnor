
package org.guvnor.ala.docker.executor;

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
import org.guvnor.ala.config.ProvisioningConfig;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.config.SourceConfig;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.access.impl.DockerAccessInterfaceImpl;
import org.guvnor.ala.docker.config.ContextAwareDockerProvisioningConfig;
import org.guvnor.ala.docker.config.ContextAwareDockerRuntimeExecConfig;
import org.guvnor.ala.docker.config.DockerBuildConfig;
import org.guvnor.ala.docker.config.DockerProviderConfig;
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
import java.util.List;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.service.DockerRuntimeManager;
import static org.guvnor.ala.pipeline.StageUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TODO: update me
 */
public class DockerExecutorTest {

    private File tempPath;

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
    public void testAPI() throws InterruptedException {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();
        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final DockerAccessInterface dockerAccessInterface = new DockerAccessInterfaceImpl();

        final Stage<Input, SourceConfig> sourceConfig = config( "Git Source", (s) -> new GitConfig() {} );
        final Stage<SourceConfig, ProjectConfig> projectConfig = config( "Maven Project", (s) -> new MavenProjectConfig() {
        } );
        final Stage<ProjectConfig, BuildConfig> buildConfig = config( "Maven Build Config", (s) -> new MavenBuildConfig() {
        } );
        final Stage<BuildConfig, BuildConfig> dockerBuildConfig = config( "Docker Build Config", (s) -> new DockerBuildConfig() {
        } );
        final Stage<BuildConfig, BinaryConfig> buildExec = config( "Maven Build", (s) -> new MavenBuildExecConfig() {
        } );
        final Stage<BinaryConfig, ProviderConfig> providerConfig = config( "Docker Provider Config", (s) -> new DockerProviderConfig() {
        } );

        final Stage<ProviderConfig, ProvisioningConfig> runtimeConfig = config( "Docker Runtime Config", (s) -> new ContextAwareDockerProvisioningConfig() {
        } );

        final Stage<ProvisioningConfig, RuntimeConfig> runtimeExec = config( "Docker Runtime Exec", (s) -> new ContextAwareDockerRuntimeExecConfig() );

        final Pipeline pipe = PipelineFactory
                .startFrom( sourceConfig )
                .andThen( projectConfig )
                .andThen( buildConfig )
                .andThen( dockerBuildConfig )
                .andThen( buildExec )
                .andThen( providerConfig )
                .andThen( runtimeConfig )
                .andThen( runtimeExec ).buildAs( "my pipe" );

        DockerRuntimeExecExecutor dockerRuntimeExecExecutor = new DockerRuntimeExecExecutor( runtimeRegistry, dockerAccessInterface );

        final PipelineExecutor executor = new PipelineExecutor( asList( new GitConfigExecutor( sourceRegistry ),
                new MavenProjectConfigExecutor( sourceRegistry ),
                new MavenBuildConfigExecutor(),
                new MavenBuildExecConfigExecutor( buildRegistry ),
                new DockerBuildConfigExecutor(),
                new DockerProviderConfigExecutor( runtimeRegistry ),
                new DockerProvisioningConfigExecutor(), dockerRuntimeExecExecutor ) );

        
        executor.execute( new Input() {
            {
                put( "repo-name", "drools-workshop" );
                put( "branch", "master" );
                put( "out-dir", tempPath.getAbsolutePath() );
                put( "origin", "https://github.com/salaboy/drools-workshop" );
                put( "project-dir", "drools-webapp-example" );
            }
        }, pipe, (Runtime b) -> System.out.println( b ) );

        List<Runtime> allRuntimes = runtimeRegistry.getAllRuntimes();
       
        assertEquals( 1, allRuntimes.size() );
        
        Runtime runtime = allRuntimes.get( 0 );

        assertTrue( runtime instanceof DockerRuntime );

        DockerRuntime dockerRuntime = ( DockerRuntime ) runtime;

        DockerRuntimeManager runtimeManager = new DockerRuntimeManager(runtimeRegistry, dockerAccessInterface );
        
        runtimeManager.start( dockerRuntime );

        assertEquals( "Running", dockerRuntime.getState().getStatus() );

        runtimeManager.pause( dockerRuntime );

        assertEquals( "Paused", dockerRuntime.getState().getStatus() );
        
        Thread.sleep(3000);
        
        runtimeManager.stop( dockerRuntime );
        
        Thread.sleep(5000);

        dockerRuntimeExecExecutor.destroy( runtime );

        dockerAccessInterface.dispose();
    }

    @Test
    public void testFlexAPI() throws InterruptedException {
        final InMemoryRuntimeRegistry runtimeRegistry = new InMemoryRuntimeRegistry();
        final DockerAccessInterface dockerAccessInterface = new DockerAccessInterfaceImpl();

        final Stage<Input, ProviderConfig> providerConfig = config( "Docker Provider Config", (s) -> new DockerProviderConfig() {
        } );

        final Stage<ProviderConfig, ProvisioningConfig> runtimeConfig = config( "Docker Runtime Config", (s) -> new ContextAwareDockerProvisioningConfig() {
        } );

        final Stage<ProvisioningConfig, RuntimeConfig> runtimeExec = config( "Docker Runtime Exec", (s) -> new ContextAwareDockerRuntimeExecConfig() );

        final Pipeline pipe = PipelineFactory
                .startFrom( providerConfig )
                .andThen( runtimeConfig )
                .andThen( runtimeExec ).buildAs( "my pipe" );

        DockerRuntimeExecExecutor dockerRuntimeExecExecutor = new DockerRuntimeExecExecutor( runtimeRegistry, dockerAccessInterface );
        final PipelineExecutor executor = new PipelineExecutor( asList( new DockerProviderConfigExecutor( runtimeRegistry ),
                new DockerProvisioningConfigExecutor(),
                dockerRuntimeExecExecutor ) );
        executor.execute( new Input() {
            {
                put( "image-name", "kitematic/hello-world-nginx" );
                put( "port-number", "8080" );
                put( "docker-pull", "true" );
            }
        }, pipe, (Runtime b) -> System.out.println( b ) );
        
        List<Runtime> allRuntimes = runtimeRegistry.getAllRuntimes();
       
        assertEquals( 1, allRuntimes.size() );
        
        Runtime runtime = allRuntimes.get( 0 );

        assertTrue( runtime instanceof DockerRuntime );

        DockerRuntime dockerRuntime = ( DockerRuntime ) runtime;
        
        DockerRuntimeManager runtimeManager = new DockerRuntimeManager(runtimeRegistry, dockerAccessInterface );
        
        runtimeManager.start( dockerRuntime );

        assertEquals( "Running", dockerRuntime.getState().getStatus() );
        
        runtimeManager.pause( dockerRuntime );

        assertEquals( "Paused", dockerRuntime.getState().getStatus() );

        runtimeManager.stop( dockerRuntime );
        
        Thread.sleep( 5000 );
        
        dockerRuntimeExecExecutor.destroy( runtime );

        dockerAccessInterface.dispose();
    }

    

}
