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

package org.guvnor.ala.runtime.base;

import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeEndpoint;
import org.guvnor.ala.runtime.RuntimeInfo;
import org.guvnor.ala.runtime.RuntimeState;
import org.guvnor.ala.runtime.providers.ProviderId;

public abstract class BaseRuntime implements Runtime {

    private String id;
    protected RuntimeConfig config;
    private RuntimeInfo info;
    private RuntimeState state;
    private RuntimeEndpoint endpoint;
    private ProviderId providerId;

    public BaseRuntime( final String id,
                        final RuntimeConfig config,
                        final ProviderId providerId ) {
        this.id = id;
        this.config = config;
        this.providerId = providerId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId( String id ) {
        this.id = id;
    }

    @Override
    public void setConfig( RuntimeConfig config ) {
        this.config = config;
    }

    @Override
    public RuntimeConfig getConfig() {
        return config;
    }

    @Override
    public RuntimeInfo getInfo() {
        return info;
    }

    @Override
    public void setInfo( RuntimeInfo info ) {
        this.info = info;
    }

    @Override
    public RuntimeState getState() {
        return state;
    }

    @Override
    public void setState( RuntimeState state ) {
        this.state = state;
    }

    @Override
    public RuntimeEndpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public void setEndpoint( RuntimeEndpoint endpoint ) {
        this.endpoint = endpoint;
    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

}
