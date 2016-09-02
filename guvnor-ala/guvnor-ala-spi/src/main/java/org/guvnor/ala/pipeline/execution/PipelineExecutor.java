/*
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.ala.pipeline.execution;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.ContextAware;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.Stage;

import static org.guvnor.ala.util.VariableInterpolation.*;

/*
 * Represent the Pipeline Executor which will be in charge of executing a pipeline instance 
 *  by using the Input data provided. After executing the pipeline a Consumer callback will be executed.
*/
public class PipelineExecutor {

    private final Map<Class, ConfigExecutor> configExecutors = new HashMap<>();

    public PipelineExecutor() {
    }

    public void init( final Collection<ConfigExecutor> configExecutors ) {
        for ( final ConfigExecutor configExecutor : configExecutors ) {
            this.configExecutors.put( configExecutor.executeFor(), configExecutor );
        }
    }

    public PipelineExecutor( final Collection<ConfigExecutor> configExecutors ) {
        init( configExecutors );
    }

    public <T> void execute( final Input input,
            final Pipeline pipeline,
            final Consumer<T> callback ) {
        final PipelineContext context = new PipelineContext( pipeline );
        context.start( input );
        context.pushCallback( callback );
        continuePipeline( context );
    }

    private void continuePipeline( final PipelineContext context ) {
        while ( !context.isFinished() ) {
            final Stage<Object, ?> stage = getCurrentStage( context );
            final Object newInput = pollOutput( context );

            try {
                stage.execute( newInput, output -> {
                    final ConfigExecutor executor = resolve( output.getClass() );
                    if ( output instanceof ContextAware ) {
                        ( ( ContextAware ) output ).setContext( Collections.unmodifiableMap( context.getValues() ) );
                    }
                    final Object newOutput = interpolate( context.getValues(), output );
                    context.getValues().put( executor.inputId(), newOutput );
                    if ( executor instanceof BiFunctionConfigExecutor ) {
                        final Optional result = ( Optional ) ( ( BiFunctionConfigExecutor ) executor ).apply( newInput, newOutput );
                        context.pushOutput( executor.outputId(), result.get() );
                    } else if ( executor instanceof FunctionConfigExecutor ) {
                        final Optional result = ( Optional ) ( ( FunctionConfigExecutor ) executor ).apply( newOutput );
                        context.pushOutput( executor.outputId(), result.get() );
                    }

                    continuePipeline( context );
                } );
            } catch ( final Throwable t ) {
                t.printStackTrace();
                throw new RuntimeException( "An error occurred while executing the " + ( stage == null ? "null" : stage.getName() ) + " stage.", t );
            }
            return;
        }
        if ( context.isFinished() ) {
            final Object output = pollOutput( context );
            while ( context.hasCallbacks() ) {
                context.applyCallbackAndPop( output );
            }
        }
    }

    private ConfigExecutor resolve( final Class<?> clazz ) {
        final ConfigExecutor result = configExecutors.get( clazz );
        if ( result != null ) {
            return result;
        }
        for ( final Map.Entry<Class, ConfigExecutor> entry : configExecutors.entrySet() ) {
            if ( entry.getKey().isAssignableFrom( clazz ) ) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static Object pollOutput( final PipelineContext context ) {
        return context.pollOutput()
                .orElseThrow( () -> new IllegalStateException( "The " + PipelineContext.class.getSimpleName() + " was polled with no previous output." ) );
    }

    private static Stage<Object, ?> getCurrentStage( final PipelineContext context ) {
        return context
                .getCurrentStage()
                .orElseThrow( () -> new IllegalStateException( "There was not current stage even though the process has not finished." ) );
    }
}
