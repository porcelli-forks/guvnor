package org.guvnor.ala.runtime.providers;

public interface ProviderDestroyer {

    boolean supports( final ProviderId providerId );

    void destroy( final ProviderId providerId );

}
