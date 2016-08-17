package org.guvnor.ala.docker.executor;

import java.util.Optional;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.docker.config.DockerProvisioningConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfiguration;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.runtime.providers.ProviderId;

public class DockerProvisioningConfigExecutor implements
                                              FunctionConfigExecutor<DockerProvisioningConfig, DockerRuntimeConfiguration> {

    @Override
    public Optional<DockerRuntimeConfiguration> apply( final DockerProvisioningConfig dockerRuntimeConfig ) {
        return Optional.of( new DockerRuntimeConfiguration() {
            @Override
            public ProviderId getProviderId() {
                return dockerRuntimeConfig.getProviderId();
            }

            @Override
            public String getImage() {
                return dockerRuntimeConfig.getImageName();
            }

            @Override
            public String getPort() {
                return dockerRuntimeConfig.getPortNumber();
            }

            @Override
            public boolean isPull() {
                return Boolean.valueOf( dockerRuntimeConfig.getDockerPullValue() );
            }
        } );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return DockerProvisioningConfig.class;
    }

    @Override
    public String outputId() {
        return "docker-runtime-config";
    }

}
