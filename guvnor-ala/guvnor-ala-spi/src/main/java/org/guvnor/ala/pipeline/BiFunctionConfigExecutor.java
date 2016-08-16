package org.guvnor.ala.pipeline;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * TODO: update me
 */
public interface BiFunctionConfigExecutor<T, U, R> extends ConfigExecutor,
                                                           BiFunction<T, U, Optional<R>> {

}
