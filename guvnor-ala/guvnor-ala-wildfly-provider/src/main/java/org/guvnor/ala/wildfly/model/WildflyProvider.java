/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guvnor.ala.wildfly.model;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.Provider;

public interface WildflyProvider extends ProviderConfig,
                                         Provider {

    String getHostId();

    String getPort();

    String getManagementPort();

    String getUser();

    String getPassword();

}
