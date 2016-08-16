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

package org.guvnor.ala.runtime;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.runtime.providers.Provider;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.*;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.*;

/**
 * @author salaboy
 *         <p>
 *         This class represent a Runtime (Docker Image running or a WAR deployed into a
 *         server)
 *         It also allows you to interact with the runtime state executing operations
 *         such as start, stop, restart
 */
@JsonTypeInfo(use = CLASS, include = WRAPPER_OBJECT)
public interface Runtime extends RuntimeId {

    void setId( String id );

    RuntimeEndpoint getEndpoint();

    void setEndpoint( RuntimeEndpoint endpoint );

    void setConfig( RuntimeConfig config );

    RuntimeConfig getConfig();

    void setState( RuntimeState state );

    RuntimeState getState();

    void setInfo( RuntimeInfo info );

    RuntimeInfo getInfo();

}
