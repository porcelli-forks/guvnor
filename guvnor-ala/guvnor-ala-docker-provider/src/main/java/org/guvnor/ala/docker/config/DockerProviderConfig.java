package org.guvnor.ala.docker.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.guvnor.ala.config.ProviderConfig;

@JsonTypeName(value = "DockerProviderConfig")
public interface DockerProviderConfig extends ProviderConfig {

    default String getName() {
        return "local";
    }

    default String getHostIp() {
        return "0.0.0.0";
    }

}
