package org.guvnor.ala.pipeline;

import org.guvnor.ala.config.Config;

/**
 * TODO: update me
 */
public interface ConfigExecutor {

    Class<? extends Config> executeFor();

    String outputId();

    default String inputId() {
        return "none";
    }

}
