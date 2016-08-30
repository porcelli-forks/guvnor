package org.guvnor.ala.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.runtime.providers.ProviderId;

/**
 * TODO: update me
 */
public interface ProvisioningConfig extends Config {

    ProviderId getProviderId();

}
