package org.guvnor.ala.services.swarm.tests;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;

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

@RunWith(Arquillian.class)
public class RuntimeEndpointsTest {

    private final String APP_URL = "http://localhost:8080/";

    @Deployment(testable = true)
    public static Archive createDeployment() throws Exception {
        JAXRSArchive deployment = ShrinkWrap.create( JAXRSArchive.class );
        deployment.setContextRoot( "/api" );
        deployment.addPackages( true, "com.google.common" );
        deployment.addAllDependencies();
        return deployment;
    }

    @Test
    @RunAsClient
    public void checkService() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target( APP_URL  );
        ResteasyWebTarget restEasyTarget = (ResteasyWebTarget)target;
        RuntimeProvisioningService proxy = restEasyTarget.proxy( RuntimeProvisioningService.class );
        

        ProviderTypeList allProviderTypes = proxy.getAllProviderTypes();
        
        
        assertNotNull( allProviderTypes );
        assertEquals( 2, allProviderTypes.getItems().size());
        
        for(ProviderType pt : allProviderTypes.getItems()){
            System.out.println( " PT: " + pt );
        }
        
        ProviderList allProviders = proxy.getAllProviders();
        
         for(Provider p : allProviders.getItems()){
            System.out.println( " P: " + p);
           
        }

    }

}
