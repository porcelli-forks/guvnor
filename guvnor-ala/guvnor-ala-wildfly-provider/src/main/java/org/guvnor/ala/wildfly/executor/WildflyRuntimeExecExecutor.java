
package org.guvnor.ala.wildfly.executor;

import java.util.Optional;
import static java.util.UUID.randomUUID;

import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.RuntimeConfig;

import org.guvnor.ala.exceptions.ProvisioningException;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.config.WildflyRuntimeConfiguration;
import org.guvnor.ala.wildfly.config.WildflyRuntimeExecConfig;
import org.guvnor.ala.wildfly.model.WildflyProvider;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyRuntimeExecExecutor<T extends WildflyRuntimeConfiguration> implements RuntimeBuilder<T, WildflyRuntime>,
        RuntimeDestroyer,
        FunctionConfigExecutor<T, WildflyRuntime> {

    private final RuntimeRegistry runtimeRegistry;
    private final WildflyAccessInterface wildfly;
    protected static final Logger LOG = LoggerFactory.getLogger( WildflyRuntimeExecExecutor.class );

    @Inject
    public WildflyRuntimeExecExecutor( final RuntimeRegistry runtimeRegistry,
            final WildflyAccessInterface docker ) {
        this.runtimeRegistry = runtimeRegistry;
        this.wildfly = docker;
    }

    @Override
    public Optional<WildflyRuntime> apply( final WildflyRuntimeConfiguration config ) {
        final Optional<WildflyRuntime> runtime = create( config );
        if ( runtime.isPresent() ) {
            runtimeRegistry.registerRuntime( runtime.get() );
        }
        return runtime;
    }

    private Optional<WildflyRuntime> create( final WildflyRuntimeConfiguration runtimeConfig ) throws ProvisioningException {

        String warPath = runtimeConfig.getWarPath();
        final Optional<WildflyProvider> _wildflyProvider = runtimeRegistry.getProvider( runtimeConfig.getProviderId(), WildflyProvider.class );

        WildflyProvider wildflyProvider = _wildflyProvider.get();

        int result = wildfly.getWildflyClient( runtimeConfig.getProviderId() ).deploy( wildflyProvider.getUser(),
                wildflyProvider.getPassword(), wildflyProvider.getHostId(), Integer.valueOf(wildflyProvider.getManagementPort()), warPath );

        if ( result != 200 ) {
            throw new ProvisioningException( "Deployment to Wildfly Failed with error code: " + result );
        }

        final String id = randomUUID().toString();
        String shortId = id.substring( 0, 12 );

        return Optional.of( new WildflyRuntime( shortId, runtimeConfig, wildflyProvider ) );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return WildflyRuntimeExecConfig.class;
    }

    @Override
    public String outputId() {
        return "wildfly-runtime";
    }

    @Override
    public boolean supports( final RuntimeConfig config ) {
        return config instanceof WildflyRuntimeConfiguration;
    }

    @Override
    public boolean supports( final RuntimeId runtimeId ) {
        return runtimeId instanceof WildflyRuntime
                || runtimeRegistry.getRuntimeById( runtimeId.getId() ) instanceof WildflyRuntime;
    }

    @Override
    public void destroy( final RuntimeId runtimeId ) {

        wildfly.getWildflyClient( runtimeId.getProviderId() ).undeploy();
        runtimeRegistry.unregisterRuntime( runtimeId );

    }
}
