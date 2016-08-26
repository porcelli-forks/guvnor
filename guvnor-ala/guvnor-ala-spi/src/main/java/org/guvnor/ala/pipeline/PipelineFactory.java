package org.guvnor.ala.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.ala.config.Config;

public final class PipelineFactory {

    private PipelineFactory() {

    }

    public static <INPUT extends Config, OUTPUT extends Config> PipelineBuilder<INPUT, OUTPUT> startFrom( final Stage<INPUT, OUTPUT> stage ) {
        return new PipelineBuilder<INPUT, OUTPUT>() {
            private final List<Stage> stages = new ArrayList<>();

            {
                stages.add( stage );
            }

            @Override
            public <T extends Config> PipelineBuilder<INPUT, T> andThen( final Stage<? super OUTPUT, T> nextStep ) {
                stages.add( nextStep );
                return (PipelineBuilder<INPUT, T>) this;
            }

            @Override
            public Pipeline buildAs( final String name ) {
                return new BasePipeline(name, stages);
            }
            
            @Override
            public Pipeline build( final PipelineConfig config ) {
                stages.clear();
                for ( final  Config c : config.getConfigStages() ) {
                    stages.add( StageUtil.config( c.toString(), f -> c ) );
                }
                return new BasePipeline( config.getName(), stages );
            }

        };
    }

}
