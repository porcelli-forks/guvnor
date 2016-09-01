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

import org.guvnor.ala.runtime.RuntimeEndpoint;

public class BaseRuntimeEndpoint implements RuntimeEndpoint {

    private String host;
    private int port;
    private String context;

    public BaseRuntimeEndpoint() {
    }

    public BaseRuntimeEndpoint( String host,
            int port,
            String context ) {
        this.host = host;
        this.port = port;
        this.context = context;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost( String host ) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort( int port ) {
        this.port = port;
    }

    @Override
    public String getContext() {
        return context;
    }

    public void setContext( String context ) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "RuntimeEndpoint{" + "host=" + host + ", port=" + port + ", context=" + context + '}';
    }

}
