package org.guvnor.ala.pipeline;

import org.guvnor.ala.config.Config;

public interface PipelineBuilder<INPUT extends Config, OUTPUT extends Config> {

    <T extends Config> PipelineBuilder<INPUT, T> andThen( final Stage<? super OUTPUT, T> nextStep );

    Pipeline buildAs( final String name );
    
    Pipeline build( final PipelineConfig config );
}
