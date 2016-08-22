/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.services.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.ala.pipeline.ConfigExecutor;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.events.PipelineEventHandler;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.PipelineRegistry;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.services.api.PipelineService;
import org.guvnor.ala.services.api.itemlist.PipelineList;
import org.guvnor.ala.services.exceptions.BusinessException;



@ApplicationScoped
public class RestPipelineServiceImpl implements PipelineService {

    @Inject
    private PipelineRegistry pipelineRegistry;
    
    @Inject
    private SourceRegistry sourceRegistry;
    
    @Inject
    private BuildRegistry buildRegistry;
    
//    @Inject
//    private InMemoryRuntimeRegistry runtimeRegistry;
//    
//    @Inject
//    private DockerAccessInterface dockerAccessInterface;
    
    @Inject
    @Any
    private Instance<PipelineEventHandler> eventHandlers;


    @Override
    public PipelineList getAllPipelines() throws BusinessException {
        return new PipelineList(pipelineRegistry.getAllPipelines());
    }

    @Override
    public String newPipeline( Pipeline pipeline ) throws BusinessException {
        String id = UUID.randomUUID().toString();
//        pipeline.setName( id );
        pipelineRegistry.registerPipeline( pipeline );
        return pipeline.getName();
    }

    @Override
    public void runPipeline( final String name ) throws BusinessException {
        
        Pipeline pipe = pipelineRegistry.getPipelineByName( name );
        List<ConfigExecutor> configs = new ArrayList<>();
        final PipelineExecutor executor = new PipelineExecutor(configs);
        executor.execute( new Input() {
            {
                put( "repo-name", "drools-workshop" );
                put( "branch", "master" );
//                put( "out-dir", tempPath.getAbsolutePath() );
                put( "origin", "https://github.com/salaboy/drools-workshop" );
                put( "project-dir", "drools-webapp-example" );
            }
        }, pipe, (org.guvnor.ala.runtime.Runtime b) -> System.out.println( b ) );
//        PipelineInstance newPipelineInstance = new PipelineInstanceImpl( pipelineRegistry.getPipelineByName( name ) );
//
//        for ( PipelineEventHandler peh : eventHandlers ) {
//            newPipelineInstance.registerEventHandler( peh );
//        }
//        PipelineDataContext results = newPipelineInstance.execute();

    }

}
