
package org.guvnor.ala.docker.config;

import org.guvnor.ala.config.ProvisioningConfig;

public interface DockerProvisioningConfig extends ProvisioningConfig {

    default String getImageName() {
        return "${input.image-name}";
    }

    default String getPortNumber() {
        return "${input.port-number}";
    }

    default String getDockerPullValue() {
        return "${input.docker-pull}";
    }

}
