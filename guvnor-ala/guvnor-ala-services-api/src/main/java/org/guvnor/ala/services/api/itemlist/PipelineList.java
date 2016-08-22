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

package org.guvnor.ala.services.api.itemlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.services.api.ItemList;


public class PipelineList implements ItemList<Pipeline> {

    private Pipeline[] pipelines;

    public PipelineList() {
    }

    public PipelineList( List<Pipeline> pipelines ) {
        this.pipelines = pipelines.toArray( new Pipeline[pipelines.size()] );
    }

    public PipelineList( Pipeline[] providers ) {
        this.pipelines = providers;
    }

    public Pipeline[] getPipelines() {
        return pipelines;
    }

    public void setPipelines( Pipeline[] pipelines ) {
        this.pipelines = pipelines;
    }

    @Override
    @JsonIgnore
    public List<Pipeline> getItems() {
        if ( pipelines == null ) {
            return Collections.emptyList();
        }
        return Arrays.asList( pipelines );
    }

}
