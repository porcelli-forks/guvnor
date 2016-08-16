package org.guvnor.ala.runtime;

import java.util.Optional;
import java.util.function.Function;

import org.guvnor.ala.config.RuntimeConfig;

public interface RuntimeBuilder<T extends RuntimeConfig, R extends Runtime> extends Function<T, Optional<R>> {

    boolean supports( final RuntimeConfig config );

}
