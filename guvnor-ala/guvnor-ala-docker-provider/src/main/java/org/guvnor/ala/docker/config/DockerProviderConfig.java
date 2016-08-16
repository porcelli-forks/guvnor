package org.guvnor.ala.docker.config;

import org.guvnor.ala.config.ProviderConfig;

public interface DockerProviderConfig extends ProviderConfig {

    default String getName() {
        return "local";
    }

    default String getHostIp() {
        return "0.0.0.0";
    }

}
