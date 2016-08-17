/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guvnor.ala.wildfly.model;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.base.BaseProvider;

public class WildflyProvider extends BaseProvider implements ProviderConfig {

    private final String hostId;
    private final String port;
    private final String managementPort;
    private final String user;
    private final String password;

    public WildflyProvider( final String name,
                           final String hostId, String port, String managementPort, String user, String password ) {
        super( name, WildflyProviderType.instance() );
        this.hostId = hostId;
        this.port = port;
        this.managementPort = managementPort;
        this.user = user;
        this.password = password;
        
    }

    public String getHostId() {
        return hostId;
    }
    
    public String getPort(){
        return port;
    }

    public String getManagementPort() {
        return managementPort;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
    
}
