
package org.guvnor.ala.wildfly.access;

import org.uberfire.commons.lifecycle.Disposable;
import org.guvnor.ala.runtime.providers.ProviderId;

/**
 * TODO: update me
 */
public interface WildflyAccessInterface extends Disposable {

    WildflyClient getWildflyClient( final ProviderId providerId );

}
