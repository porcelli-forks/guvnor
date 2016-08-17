
package org.guvnor.ala.wildfly.config;

import java.util.Optional;

import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.ProviderConfig;

import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.providers.ProviderBuilder;
import org.guvnor.ala.runtime.providers.ProviderDestroyer;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.wildfly.model.WildflyProvider;

public class WildflyProviderConfigExecutor implements ProviderBuilder<WildflyProviderConfig, WildflyProvider>,
        ProviderDestroyer,
        FunctionConfigExecutor<WildflyProviderConfig, WildflyProvider> {

    private final RuntimeRegistry runtimeRegistry;

    @Inject
    public WildflyProviderConfigExecutor( final RuntimeRegistry runtimeRegistry ) {
        this.runtimeRegistry = runtimeRegistry;
    }

    @Override
    public Optional<WildflyProvider> apply( final WildflyProviderConfig wildflyProviderConfig ) {
        final WildflyProvider provider = new WildflyProvider( wildflyProviderConfig.getName(), wildflyProviderConfig.getHostIp(),
                wildflyProviderConfig.getPort(), wildflyProviderConfig.getManagementPort(), 
                wildflyProviderConfig.getUser(), wildflyProviderConfig.getPassword() );
        runtimeRegistry.registerProvider( provider );
        return Optional.of( provider );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return WildflyProviderConfig.class;
    }

    @Override
    public String outputId() {
        return "wildfly-provider";
    }

    @Override
    public boolean supports( final ProviderConfig config ) {
        return config instanceof WildflyProviderConfig;
    }

    @Override
    public boolean supports( final ProviderId providerId ) {
        return providerId instanceof WildflyProvider;
    }

    @Override
    public void destroy( final ProviderId providerId ) {
    }

}
