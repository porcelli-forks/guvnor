package org.guvnor.ala.pipeline;

import java.util.Optional;
import java.util.function.Function;

/**
 * TODO: update me
 */
public interface FunctionConfigExecutor<T, R> extends ConfigExecutor,
                                                      Function<T, Optional<R>> {

}
