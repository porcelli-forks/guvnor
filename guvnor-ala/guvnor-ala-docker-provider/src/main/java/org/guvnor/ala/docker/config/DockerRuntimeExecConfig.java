package org.guvnor.ala.docker.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.guvnor.ala.config.RuntimeConfig;

@JsonTypeName(value = "DockerRuntimeExecConfig")
public interface DockerRuntimeExecConfig extends DockerRuntimeConfig,
                                                 RuntimeConfig {

}
