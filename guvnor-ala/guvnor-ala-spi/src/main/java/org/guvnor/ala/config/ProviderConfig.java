package org.guvnor.ala.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * TODO: update me
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface ProviderConfig extends Config {

}
