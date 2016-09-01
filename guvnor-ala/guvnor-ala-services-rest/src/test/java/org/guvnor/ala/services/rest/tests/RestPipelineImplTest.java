
package org.guvnor.ala.services.rest.tests;

import com.spotify.docker.client.DockerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.MavenBuildConfig;
import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.build.maven.config.impl.MavenBuildConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenBuildExecConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.build.maven.executor.MavenBuildConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenBuildExecConfigExecutor;
import org.guvnor.ala.build.maven.executor.MavenProjectConfigExecutor;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.ProjectConfig;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.access.impl.DockerAccessInterfaceImpl;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerProvisioningConfig;
import org.guvnor.ala.docker.config.impl.ContextAwareDockerRuntimeExecConfig;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.executor.DockerProviderConfigExecutor;
import org.guvnor.ala.docker.executor.DockerRuntimeExecExecutor;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerProviderType;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.service.DockerRuntimeManager;
import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.registry.local.InMemoryRuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeManager;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderBuilder;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.rest.RestRuntimeProvisioningServiceImpl;
import org.guvnor.ala.services.rest.factories.ProviderFactory;
import org.guvnor.ala.services.rest.factories.RuntimeFactory;
import org.guvnor.ala.services.rest.factories.RuntimeManagerFactory;
import org.guvnor.ala.wildfly.executor.WildflyProviderConfigExecutor;
import org.guvnor.ala.wildfly.model.WildflyProviderType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import org.uberfire.commons.lifecycle.Disposable;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.docker.config.impl.DockerBuildConfigImpl;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.executor.DockerBuildConfigExecutor;
import org.guvnor.ala.docker.executor.DockerProvisioningConfigExecutor;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.local.InMemoryBuildRegistry;
import org.guvnor.ala.registry.local.InMemoryPipelineRegistry;
import org.guvnor.ala.registry.local.InMemorySourceRegistry;
import org.guvnor.ala.services.api.PipelineService;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.backend.PipelineConfigImpl;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.guvnor.ala.services.rest.RestPipelineServiceImpl;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.junit.After;
import org.junit.Before;

/**
 * TODO: update me
 */
@RunWith( Arquillian.class )
public class RestPipelineImplTest {

    @Inject
    private PipelineService pipelineService;

    @Inject
    private RuntimeProvisioningService runtimeService;

    private File tempPath;

    @Deployment()
    public static Archive createDeployment() throws Exception {
        JavaArchive deployment = ShrinkWrap.create( JavaArchive.class );
        deployment.addClass( PipelineService.class );
        deployment.addClass( RestPipelineServiceImpl.class );
        deployment.addClass( PipelineExecutor.class );
        deployment.addClass( InMemoryPipelineRegistry.class );
        deployment.addClass( InMemoryBuildRegistry.class );
        deployment.addClass( InMemorySourceRegistry.class );
        deployment.addClass( DockerProviderConfigExecutor.class );
        deployment.addClass( WildflyProviderConfigExecutor.class );
        deployment.addClass( RestRuntimeProvisioningServiceImpl.class );
        deployment.addClass( RuntimeRegistry.class );
        deployment.addClass( InMemoryRuntimeRegistry.class );
        deployment.addClass( RuntimeRegistry.class );
        deployment.addClass( ProviderFactory.class );
        deployment.addClass( RuntimeFactory.class );
        deployment.addClass( RuntimeManagerFactory.class );
        deployment.addClass( DockerProviderType.class );
        deployment.addClass( DockerProviderConfig.class );
        deployment.addClass( DockerProvider.class );
        deployment.addClass( WildflyProviderType.class );
        deployment.addClass( ProviderBuilder.class );
        deployment.addClass( ProviderType.class );
        deployment.addClass( FunctionConfigExecutor.class );
        deployment.addClass( ConfigExecutor.class );
        deployment.addClass( ProviderConfig.class );
        deployment.addClass( Provider.class );
        deployment.addClass( DockerRuntimeConfig.class );
        deployment.addClass( DockerRuntime.class );
        deployment.addClass( RuntimeBuilder.class );
        deployment.addClass( DockerRuntimeExecExecutor.class );
        deployment.addClass( RuntimeDestroyer.class );
        deployment.addClass( DockerAccessInterface.class );
        deployment.addClass( DockerAccessInterfaceImpl.class );
        deployment.addClass( Disposable.class );
        deployment.addClass( DockerException.class );
        deployment.addClass( DockerRuntimeManager.class );
        deployment.addClass( RuntimeManager.class );
        deployment.addClass( org.guvnor.ala.config.Config.class );
        deployment.addClass( org.guvnor.ala.config.ProviderConfig.class );
        deployment.addClass( org.guvnor.ala.docker.config.DockerProviderConfig.class );
        deployment.addClass( org.guvnor.ala.docker.model.DockerProvider.class );
        deployment.addClass( org.guvnor.ala.pipeline.FunctionConfigExecutor.class );
        deployment.addClass( BiFunctionConfigExecutor.class );
        deployment.addClass( org.guvnor.ala.registry.RuntimeRegistry.class );
        deployment.addClass( org.guvnor.ala.runtime.providers.ProviderBuilder.class );
        deployment.addClass( org.guvnor.ala.runtime.providers.ProviderDestroyer.class );
        deployment.addClass( org.guvnor.ala.runtime.providers.ProviderId.class );

        deployment.addClass( GitConfigExecutor.class );
        deployment.addClass( MavenProjectConfig.class );
        deployment.addClass( MavenProjectConfigExecutor.class );
        deployment.addClass( Project.class );
        deployment.addClass( Source.class );
        deployment.addClass( ProjectConfig.class );
        deployment.addClass( BuildConfig.class );
        deployment.addClass( MavenBuildConfig.class );
        deployment.addClass( MavenBuildConfigExecutor.class );
        deployment.addClass( MavenBuildExecConfigExecutor.class );
        deployment.addClass( DockerBuildConfigExecutor.class );
        deployment.addClass( DockerProviderConfigExecutor.class );
        deployment.addClass( DockerProvisioningConfigExecutor.class );

        deployment.addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
        return deployment;
    }

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
    public void testAPI() {

        ProviderTypeList allProviderTypes = runtimeService.getAllProviderTypes();

        assertEquals( 2, allProviderTypes.getItems().size() );
        DockerProviderConfig dockerProviderConfig = new DockerProviderConfig() {
        };
        runtimeService.registerProvider( dockerProviderConfig );

        ProviderList allProviders = runtimeService.getAllProviders();

        assertEquals( 1, allProviders.getItems().size() );

        Provider p = allProviders.getItems().get( 0 );
        assertTrue( p instanceof DockerProvider );

        PipelineConfigsList allPipelineConfigs = pipelineService.getAllPipelineConfigs();

        assertNotNull( allPipelineConfigs );
        assertEquals( 0, allPipelineConfigs.getItems().size() );

        List<Config> configs = new ArrayList<>();
        configs.add( new GitConfigImpl() );
        configs.add( new MavenProjectConfigImpl() );
        configs.add( new MavenBuildConfigImpl() );
        configs.add( new DockerBuildConfigImpl() );
        configs.add( new MavenBuildExecConfigImpl() );
        configs.add( new DockerProviderConfigImpl() );
        configs.add( new ContextAwareDockerProvisioningConfig() );
        configs.add( new ContextAwareDockerRuntimeExecConfig() );

        String newPipeline = pipelineService.newPipeline( new PipelineConfigImpl( "mypipe", configs ) );

        allPipelineConfigs = pipelineService.getAllPipelineConfigs();

        assertEquals( 1, allPipelineConfigs.getItems().size() );

        Input input = new Input();

        input.put( "repo-name", "drools-workshop" );
        input.put( "branch", "master" );
        input.put( "out-dir", tempPath.getAbsolutePath() );
        input.put( "origin", "https://github.com/salaboy/drools-workshop" );
        input.put( "project-dir", "drools-webapp-example" );

        pipelineService.runPipeline( "mypipe", input );

        RuntimeList allRuntimes = runtimeService.getAllRuntimes();

        assertEquals( 1, allRuntimes.getItems().size() );

        runtimeService.destroyRuntime( allRuntimes.getItems().get( 0 ).getId() );

        allRuntimes = runtimeService.getAllRuntimes();

        assertEquals( 0, allRuntimes.getItems().size() );

    }

}
