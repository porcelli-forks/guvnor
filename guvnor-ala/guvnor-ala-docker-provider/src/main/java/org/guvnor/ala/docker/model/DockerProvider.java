/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guvnor.ala.docker.model;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.base.BaseProvider;

public class DockerProvider extends BaseProvider implements ProviderConfig {

    private final String hostId;

    public DockerProvider( final String name,
                           final String hostId ) {
        super( name, DockerProviderType.instance() );
        this.hostId = hostId;
    }

    public String getHostId() {
        return hostId;
    }
}
