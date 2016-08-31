package org.guvnor.ala.docker.executor;

import java.util.Optional;

import org.guvnor.ala.config.Config;
import org.guvnor.ala.docker.config.DockerProvisioningConfig;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;
import org.guvnor.ala.docker.config.impl.DockerRuntimeConfigImpl;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;

public class DockerProvisioningConfigExecutor implements
                                              FunctionConfigExecutor<DockerProvisioningConfig, DockerRuntimeConfig> {

    @Override
    public Optional<DockerRuntimeConfig> apply( final DockerProvisioningConfig dockerRuntimeConfig ) {
        return Optional.of( new DockerRuntimeConfigImpl( dockerRuntimeConfig.getProviderId(),
                                                         dockerRuntimeConfig.getImageName(),
                                                         dockerRuntimeConfig.getPortNumber(),
                                                         Boolean.valueOf( dockerRuntimeConfig.getDockerPullValue() ) ) );

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
