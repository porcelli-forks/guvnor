package org.guvnor.ala.wildfly.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.guvnor.ala.config.RuntimeConfig;

@JsonTypeName(value = "WildflyRuntimeExecConfig")
public interface WildflyRuntimeExecConfig extends WildflyRuntimeConfiguration,
                                                  RuntimeConfig {

}
