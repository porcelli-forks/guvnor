package org.guvnor.ala.services.swarm.tests;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.runner.RunWith;
import org.wildfly.swarm.jaxrs.JAXRSArchive;


@RunWith(Arquillian.class)
public class PipelineEndpointsTest {

    private final String APP_URL = "http://localhost:8080/";

    @Deployment(testable = true)
    public static Archive createDeployment() throws Exception {
        JAXRSArchive deployment = ShrinkWrap.create( JAXRSArchive.class );
        deployment.addPackages( true, "com.google.common" );
        deployment.setContextRoot( "/api" );
        deployment.addAllDependencies();
        return deployment;
    }

//    @Test
//    @RunAsClient
//    public void checkService() {
//        Client client = ClientBuilder.newClient();
//        WebTarget target = client.target( APP_URL  );
//        ResteasyWebTarget restEasyTarget = (ResteasyWebTarget)target;
//        PipelineService proxy = restEasyTarget.proxy( PipelineService.class );
//        
//
//        PipelineList allPipelines = proxy.getAllPipelines();
//        
//        
//        assertNotNull( allPipelines );
//        assertEquals( 1, allPipelines.getItems().size());
//        
//        System.out.println( "allPipelines 0 = " + allPipelines.getItems().get( 0 ) );
//
//    }

}
