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

import org.guvnor.ala.runtime.RuntimeService;

import org.guvnor.ala.wildfly.access.WildflyAccessInterface;
import org.guvnor.ala.wildfly.access.WildflyAppState;
import org.guvnor.ala.wildfly.model.WildflyRuntime;
import org.guvnor.ala.wildfly.model.WildflyRuntimeState;

public class WildflyRuntimeService implements RuntimeService<WildflyRuntime> {

    private final WildflyAccessInterface wildfly;

    public WildflyRuntimeService( final WildflyAccessInterface wildfly ) {
        this.wildfly = wildfly;
    }

    @Override
    public void start( final WildflyRuntime runtime ) {
        wildfly.getWildflyClient( runtime.getProviderId() ).start( runtime.getId() );
        refresh( runtime );
    }

    @Override
    public void stop( final WildflyRuntime runtime ) {
        wildfly.getWildflyClient( runtime.getProviderId() ).stop( runtime.getId(), 0 );
        refresh( runtime );
    }

    @Override
    public void restart( final WildflyRuntime runtime ) {
        wildfly.getWildflyClient( runtime.getProviderId() ).restart( runtime.getId() );
        refresh( runtime );
    }

    @Override
    public void refresh( final WildflyRuntime runtime ) {
            WildflyAppState appState  = wildfly.getWildflyClient( runtime.getProviderId() ).getAppState( runtime.getId() );
            runtime.setState( new WildflyRuntimeState( appState.getState(), appState.getStartedAt().toString() ) );
    }

}
