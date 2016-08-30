package org.guvnor.ala.wildfly.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.guvnor.ala.config.ProviderConfig;

@JsonTypeName(value = "WildflyProviderConfig")
public interface WildflyProviderConfig extends ProviderConfig {

    default String getName() {
        return "local";
    }

    default String getHostIp() {
        return "${input.host}";
    }
    
    default String getPort() {
        return "${input.port}";
    }
    
    default String getManagementPort() {
        return "${input.management-port}";
    }
    
    default String getUser() {
        return "${input.wildfly-user}";
    }
    
    default String getPassword() {
        return "${input.wildfly-password}";
    }

}
