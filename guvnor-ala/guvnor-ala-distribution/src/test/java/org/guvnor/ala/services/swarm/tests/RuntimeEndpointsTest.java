
package org.guvnor.ala.services.swarm.tests;

import com.spotify.docker.client.UnixConnectionSocketFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.docker.config.impl.DockerProviderConfigImpl;
import org.guvnor.ala.docker.config.impl.DockerRuntimeConfigImpl;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.api.itemlist.RuntimeList;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

import static org.junit.Assert.*;

@RunWith( Arquillian.class )
public class RuntimeEndpointsTest {

    private final String APP_URL = "http://localhost:8080/";

    @Deployment( testable = true )
    public static Archive createDeployment() throws Exception {
        JAXRSArchive deployment = ShrinkWrap.create( JAXRSArchive.class );
        deployment.setContextRoot( "/api" );
        deployment.addPackages( true, "com.google.common" );
        deployment.addClass( UnixConnectionSocketFactory.class );
        deployment.addPackages( true, "org.apache.http" );

        deployment.addAllDependencies();
        return deployment;
    }

    @Test
    @RunAsClient
    public void checkService() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target( APP_URL );
        ResteasyWebTarget restEasyTarget = ( ResteasyWebTarget ) target;
        RuntimeProvisioningService proxy = restEasyTarget.proxy( RuntimeProvisioningService.class );

        ProviderTypeList allProviderTypes = proxy.getAllProviderTypes();

        assertNotNull( allProviderTypes );
        assertEquals( 2, allProviderTypes.getItems().size() );

        DockerProviderConfig dockerProviderConfig = new DockerProviderConfigImpl();
        proxy.registerProvider( dockerProviderConfig );

        ProviderList allProviders = proxy.getAllProviders();
        assertEquals( 1, allProviders.getItems().size() );
        assertTrue( allProviders.getItems().get( 0 ) instanceof DockerProvider );
        DockerProvider dockerProvider = ( DockerProvider ) allProviders.getItems().get( 0 );
        DockerRuntimeConfig runtimeConfig = new DockerRuntimeConfigImpl( dockerProvider, "kitematic/hello-world-nginx", "8080", true );

        RuntimeList allRuntimes = proxy.getAllRuntimes();
        assertEquals( 0, allRuntimes.getItems().size() );

        String newRuntime = proxy.newRuntime( runtimeConfig );

        allRuntimes = proxy.getAllRuntimes();
        assertEquals( 1, allRuntimes.getItems().size() );

        proxy.startRuntime( newRuntime );

        allRuntimes = proxy.getAllRuntimes();
        assertEquals( 1, allRuntimes.getItems().size() );

        Runtime runtime = allRuntimes.getItems().get( 0 );

        assertTrue( runtime instanceof DockerRuntime );
        DockerRuntime dockerRuntime = ( DockerRuntime ) runtime;
        
        assertEquals( "Running", dockerRuntime.getState().getStatus());
        proxy.stopRuntime( newRuntime );

        allRuntimes = proxy.getAllRuntimes();
        assertEquals( 1, allRuntimes.getItems().size() );
        runtime = allRuntimes.getItems().get( 0 );

        assertTrue( runtime instanceof DockerRuntime );
        dockerRuntime = ( DockerRuntime ) runtime;
        
        
        assertEquals( "Stopped", dockerRuntime.getState().getStatus());
        
        proxy.destroyRuntime( newRuntime );

        allRuntimes = proxy.getAllRuntimes();
        assertEquals( 0, allRuntimes.getItems().size() );

    }

}
