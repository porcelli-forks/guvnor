package org.guvnor.ala.config;

import org.guvnor.ala.runtime.providers.ProviderId;

public interface RuntimeConfig extends Config {

    ProviderId getProviderId();
}
