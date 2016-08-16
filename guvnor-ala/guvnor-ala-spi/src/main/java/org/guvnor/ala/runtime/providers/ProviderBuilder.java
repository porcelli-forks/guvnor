package org.guvnor.ala.runtime.providers;

import java.util.Optional;
import java.util.function.Function;

import org.guvnor.ala.config.ProviderConfig;

public interface ProviderBuilder<T extends ProviderConfig, R extends Provider> extends Function<T, Optional<R>> {

    boolean supports( final ProviderConfig config );

}
