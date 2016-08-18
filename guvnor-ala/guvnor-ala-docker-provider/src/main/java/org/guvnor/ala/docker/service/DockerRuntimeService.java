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

package org.guvnor.ala.docker.service;

import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.ContainerState;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.docker.model.DockerRuntimeState;
import org.guvnor.ala.runtime.RuntimeService;

import static java.util.logging.Level.*;
import static java.util.logging.Logger.*;

public class DockerRuntimeService implements RuntimeService<DockerRuntime> {

    private final DockerAccessInterface docker;

    public DockerRuntimeService( final DockerAccessInterface docker ) {
        this.docker = docker;
    }

    @Override
    public void start( final DockerRuntime runtime ) {
        try {
            docker.getDockerClient( runtime.getProviderId() ).startContainer( runtime.getId() );
            refresh( runtime );
        } catch ( DockerException | InterruptedException ex ) {
            getLogger( DockerProvider.class.getName() ).log( SEVERE, null, ex );
        }

    }

    @Override
    public void stop( final DockerRuntime runtime ) {
        try {
            docker.getDockerClient( runtime.getProviderId() ).stopContainer( runtime.getId(), 0 );
            refresh( runtime );
        } catch ( DockerException | InterruptedException ex ) {
            getLogger( DockerProvider.class.getName() ).log( SEVERE, null, ex );
        }
    }

    @Override
    public void restart( final DockerRuntime runtime ) {
        try {
            docker.getDockerClient( runtime.getProviderId() ).restartContainer( runtime.getId() );
            refresh( runtime );
        } catch ( DockerException | InterruptedException ex ) {
            getLogger( DockerProvider.class.getName() ).log( SEVERE, null, ex );
        }
    }

    @Override
    public void refresh( final DockerRuntime runtime ) {
        try {
            ContainerInfo containerInfo = docker.getDockerClient( runtime.getProviderId() ).inspectContainer( runtime.getId() );
            ContainerState state = containerInfo.state();
            String stateString = "NA";
            if ( state.running() && !state.paused() ) {
                stateString = "Running";
            } else if ( state.paused() ) {
                stateString = "Paused";
            } else if ( state.restarting() ) {
                stateString = "Restarting";
            } else if ( state.oomKilled() ){
                stateString = "Killed";
            }
            runtime.setState( new DockerRuntimeState( stateString, state.startedAt().toString() ) );
        } catch ( DockerException | InterruptedException ex ) {
            getLogger( DockerRuntime.class.getName() ).log( SEVERE, null, ex );
        }

    }

    public void pause( DockerRuntime runtime ) {
        try {
            docker.getDockerClient( runtime.getProviderId() ).pauseContainer( runtime.getId() );
            refresh( runtime );
        } catch ( DockerException | InterruptedException ex ) {
            getLogger( DockerProvider.class.getName() ).log( SEVERE, null, ex );
        }
    }

}
