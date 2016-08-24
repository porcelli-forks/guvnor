package org.guvnor.ala.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.runtime.providers.ProviderId;

/**
 * TODO: update me
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface ProvisioningConfig extends Config {

    ProviderId getProviderId();

}
