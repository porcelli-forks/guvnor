
package org.guvnor.ala.wildfly.access.impl;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.commons.lifecycle.Disposable;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.access.WildflyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyAccessInterfaceImpl
        implements WildflyAccessInterface,
        Disposable {

    protected static final Logger LOG = LoggerFactory.getLogger(WildflyAccessInterfaceImpl.class );
    final Map<String, WildflyClient> clientMap = new HashMap<>();

    @Override
    public WildflyClient getWildflyClient( final ProviderId providerId ) {
        if ( !clientMap.containsKey( providerId.getId() ) ) {
            clientMap.put( providerId.getId(), buildClient( providerId ) );
        }
        return clientMap.get( providerId.getId() );
    }

    private WildflyClient buildClient( final ProviderId providerId )   {
        return new WildflyClient();
    }

    @Override
    public void dispose() {
        clientMap.values().forEach( WildflyClient::close );
    }
}
