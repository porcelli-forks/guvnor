package org.guvnor.ala.docker.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.config.ProviderConfig;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface DockerProviderConfig extends ProviderConfig {

    default String getName() {
        return "local";
    }

    default String getHostIp() {
        return "0.0.0.0";
    }

}
