
package org.guvnor.ala.services.rest.tests;

import org.junit.Test;
import javax.inject.Inject;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.executor.DockerProviderConfigExecutor;
import org.guvnor.ala.docker.model.DockerProviderType;
import org.guvnor.ala.registry.local.InMemoryRuntimeRegistry;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;
import org.guvnor.ala.services.api.RuntimeProvisioningService;
import org.guvnor.ala.services.api.itemlist.ProviderList;
import org.guvnor.ala.services.api.itemlist.ProviderTypeList;
import org.guvnor.ala.services.rest.RestRuntimeProvisioningServiceImpl;
import org.guvnor.ala.services.rest.factories.ProviderFactory;
import org.guvnor.ala.wildfly.model.WildflyProviderType;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;

/**
 * TODO: update me
 */
@RunWith( Arquillian.class )
public class RestRuntimeProvisioningImplTest {

    @Inject
    private RuntimeProvisioningService runtimeService;

    @Deployment()
    public static Archive createDeployment() throws Exception {
        JavaArchive deployment = ShrinkWrap.create( JavaArchive.class );
        deployment.addClass( RestRuntimeProvisioningServiceImpl.class );
        deployment.addClass( InMemoryRuntimeRegistry.class );
        deployment.addClass( ProviderFactory.class );
        deployment.addClass( DockerProviderType.class );
        deployment.addClass( WildflyProviderType.class );
        deployment.addClass( DockerProviderConfigExecutor.class );
        deployment.addAsManifestResource( EmptyAsset.INSTANCE, "beans.xml" );
        return deployment;
    }

    @Test
    public void testAPI() {

        ProviderTypeList allProviderTypes = runtimeService.getAllProviderTypes();
        for ( ProviderType pt : allProviderTypes.getItems() ) {
            System.out.println( " Provider Type: " + pt );
        }

        assertEquals( 2, allProviderTypes.getItems().size() );
        DockerProviderConfig dockerProviderConfig = new DockerProviderConfig() {
        };
        runtimeService.registerProvider( dockerProviderConfig );

        ProviderList allProviders = runtimeService.getAllProviders();

        for ( Provider p : allProviders.getItems() ) {
            System.out.println( "P : " + p );
        }
        assertEquals( 1, allProviders.getItems().size() );

    }

}
