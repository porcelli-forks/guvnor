package org.guvnor.ala.docker.executor;

import java.util.Optional;

import javax.inject.Inject;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.docker.config.DockerProviderConfig;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.providers.ProviderBuilder;
import org.guvnor.ala.runtime.providers.ProviderDestroyer;
import org.guvnor.ala.runtime.providers.ProviderId;

public class DockerProviderConfigExecutor implements ProviderBuilder<DockerProviderConfig, DockerProvider>,
                                                     ProviderDestroyer,
                                                     FunctionConfigExecutor<DockerProviderConfig, DockerProvider> {

    private final RuntimeRegistry runtimeRegistry;

    @Inject
    public DockerProviderConfigExecutor( final RuntimeRegistry runtimeRegistry ) {
        this.runtimeRegistry = runtimeRegistry;
    }

    @Override
    public Optional<DockerProvider> apply( final DockerProviderConfig dockerProviderConfig ) {
        final DockerProvider provider = new DockerProvider( dockerProviderConfig.getName(), dockerProviderConfig.getHostIp() );
        runtimeRegistry.registerProvider( provider );
        return Optional.of( provider );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return DockerProviderConfig.class;
    }

    @Override
    public String outputId() {
        return "docker-provider";
    }

    @Override
    public boolean supports( final ProviderConfig config ) {
        return config instanceof DockerProviderConfig;
    }

    @Override
    public boolean supports( final ProviderId providerId ) {
        return providerId instanceof DockerProvider;
    }

    @Override
    public void destroy( final ProviderId providerId ) {
    }

}
