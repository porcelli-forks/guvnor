
package org.guvnor.ala.services.swarm.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.apache.commons.io.FileUtils;
import org.guvnor.ala.build.maven.config.impl.MavenBuildConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenBuildExecConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.docker.config.ContextAwareDockerProvisioningConfig;
import org.guvnor.ala.docker.config.ContextAwareDockerRuntimeExecConfig;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.impl.DockerBuildConfigImpl;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.services.api.PipelineService;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.backend.PipelineConfigImpl;
import org.guvnor.ala.services.api.itemlist.PipelineConfigsList;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;
import org.guvnor.ala.source.git.config.impl.GitConfigImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

@RunWith( Arquillian.class )
@Ignore
public class PipelineEndpointsTest {

    private final String APP_URL = "http://localhost:8080/";

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

    @Deployment( testable = true )
    public static Archive createDeployment() throws Exception {

        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
            .importRuntimeDependencies().resolve().withTransitivity().asFile();
        
        JAXRSArchive deployment = ShrinkWrap.create( JAXRSArchive.class );
//        deployment.addPackages( true, "com.google.common" );
//        deployment.addPackages( true, "org.apache.commons.lang" );
//        deployment.addPackages( true, "org.codehaus.plexus.util.xml" );
//        deployment.addPackages( true, "org.apache.maven" );
//        deployment.addPackages( true, "org.codehaus.plexus" );
//        deployment.addPackages( true, "org.eclipse.aether" );
//        deployment.addPackages( true, "org.eclipse.sisu.plexus" );
//        deployment.addPackages( true, "org.apache.maven" );
//        deployment.addPackages( true, "org.sonatype.plexus.components.cipher" );
//        deployment.addPackages( true, "org.codehaus.plexus.util.xml" );
        deployment.addAsLibraries( files );
        deployment.setContextRoot( "/api" );
//        deployment.addAllDependencies();
        deployment.addAsManifestResource( "META-INF/services/org.uberfire.java.nio.file.spi.FileSystemProvider",
                "/services/org.uberfire.java.nio.file.spi.FileSystemProvider" );
        return deployment;
    }

    @Test
    @RunAsClient
    public void checkService() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target( APP_URL );
        ResteasyWebTarget restEasyTarget = ( ResteasyWebTarget ) target;
        PipelineService proxyPipeline = restEasyTarget.proxy( PipelineService.class );

        RuntimeProvisioningService proxyRuntime = restEasyTarget.proxy( RuntimeProvisioningService.class );

        ProviderTypeList allProviderTypes = proxyRuntime.getAllProviderTypes();

        assertNotNull( allProviderTypes );
        assertEquals( 2, allProviderTypes.getItems().size() );

        DockerProviderConfig dockerProviderConfig = new DockerProviderConfigImpl();
        proxyRuntime.registerProvider( dockerProviderConfig );

        ProviderList allProviders = proxyRuntime.getAllProviders();
        assertEquals( 1, allProviders.getItems().size() );
        assertTrue( allProviders.getItems().get( 0 ) instanceof DockerProvider );

        PipelineConfigsList allPipelines = proxyPipeline.getAllPipelineConfigs();

        assertNotNull( allPipelines );
        assertEquals( 0, allPipelines.getItems().size() );

        List<Config> configs = new ArrayList<>();
        configs.add( new GitConfigImpl() );
        configs.add( new MavenProjectConfigImpl() );
        configs.add( new MavenBuildConfigImpl() );
        configs.add( new DockerBuildConfigImpl() );
        configs.add( new MavenBuildExecConfigImpl() );
        configs.add( new DockerProviderConfigImpl() );
        configs.add( new ContextAwareDockerProvisioningConfig() );
        configs.add( new ContextAwareDockerRuntimeExecConfig() );

        String newPipeline = proxyPipeline.newPipeline( new PipelineConfigImpl( "mypipe", configs ) );

        System.out.println( "Pipeline: " + newPipeline );
        Input input = new Input();

        input.put( "repo-name", "drools-workshop" );
        input.put( "branch", "master" );
        input.put( "out-dir", tempPath.getAbsolutePath() );
        input.put( "origin", "https://github.com/salaboy/drools-workshop" );
        input.put( "project-dir", "drools-webapp-example" );

        proxyPipeline.runPipeline( "mypipe", input );

        RuntimeList allRuntimes = proxyRuntime.getAllRuntimes();

        assertEquals( 1, allRuntimes.getItems().size() );

        proxyRuntime.destroyRuntime( allRuntimes.getItems().get( 0 ).getId() );

        allRuntimes = proxyRuntime.getAllRuntimes();

        assertEquals( 0, allRuntimes.getItems().size() );
    }

}
