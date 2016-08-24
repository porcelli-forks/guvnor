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

package org.guvnor.ala.wildfly.service;

import javax.inject.Inject;

import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.RuntimeManager;
import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.access.WildflyAppState;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.guvnor.ala.wildfly.model.WildflyRuntimeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WildflyRuntimeManager implements RuntimeManager {

    private final RuntimeRegistry runtimeRegistry;
    private final WildflyAccessInterface wildfly;
    protected static final Logger LOG = LoggerFactory.getLogger( WildflyRuntimeManager.class );

    @Inject
    public WildflyRuntimeManager( final RuntimeRegistry runtimeRegistry,
            final WildflyAccessInterface docker ) {
        this.runtimeRegistry = runtimeRegistry;
        this.wildfly = docker;
    }

    @Override
    public boolean supports( final RuntimeId runtimeId ) {
        return runtimeId instanceof WildflyRuntime
                || runtimeRegistry.getRuntimeById( runtimeId.getId() ) instanceof WildflyRuntime;
    }

    @Override
    public void start( RuntimeId runtimeId ) {
        WildflyRuntime runtime = ( WildflyRuntime ) runtimeRegistry.getRuntimeById( runtimeId.getId() );
        int result = wildfly.getWildflyClient( runtime.getProviderId() ).start( runtime.getId() );
        if ( result != 200 ) {
            throw new IllegalStateException(" There as a problem with starting your application, please check into the Wildfly Logs for more information.");
        }
        refresh( runtimeId );

    }

    @Override
    public void stop( RuntimeId runtimeId ) {
        WildflyRuntime runtime = ( WildflyRuntime ) runtimeRegistry.getRuntimeById( runtimeId.getId() );

        int result = wildfly.getWildflyClient( runtime.getProviderId() ).stop( runtime.getId() );
        if ( result != 200 ) {
            throw new IllegalStateException(" There as a problem with stopping your application, please check into the Wildfly Logs for more information.");
        }
        refresh( runtimeId );

    }

    @Override
    public void restart( RuntimeId runtimeId ) {
        WildflyRuntime runtime = ( WildflyRuntime ) runtimeRegistry.getRuntimeById( runtimeId.getId() );
        wildfly.getWildflyClient( runtime.getProviderId() ).restart( runtime.getId() );
        refresh( runtimeId );
    }

    @Override
    public void refresh( RuntimeId runtimeId ) {
        WildflyRuntime runtime = ( WildflyRuntime ) runtimeRegistry.getRuntimeById( runtimeId.getId() );
        WildflyAppState appState = wildfly.getWildflyClient( runtime.getProviderId() ).getAppState( runtime.getId() );
        runtime.setState( new WildflyRuntimeState( appState.getState(), appState.getStartedAt().toString() ) );
    }

    @Override
    public void pause( RuntimeId runtimeId ) {
        WildflyRuntime runtime = ( WildflyRuntime ) runtimeRegistry.getRuntimeById( runtimeId.getId() );
        wildfly.getWildflyClient( runtime.getProviderId() ).stop( runtime.getId() );
        refresh( runtimeId );
    }

}
