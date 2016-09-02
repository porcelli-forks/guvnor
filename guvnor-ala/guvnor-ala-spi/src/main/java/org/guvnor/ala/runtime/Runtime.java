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

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.*;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.*;

/**
 * Implementations of this interface represent a Runtime (Docker Image running
 * or a WAR deployed into a
 * server)
 */
@JsonTypeInfo( use = CLASS, include = WRAPPER_OBJECT )
public interface Runtime extends RuntimeId {

    /*
     * Set the runtime id
     * @param String id
     * 
     */
    void setId( String id );

    /*
     * Get the runtime endpoint
     * @return RuntimeEndpoint
     * @see RuntimeEndpoint
     */
    RuntimeEndpoint getEndpoint();

    /*
     * Set the runtime endpoint
     * @param RuntimeEndpoint to be set
     * @see RuntimeEndpoint
     */
    void setEndpoint( RuntimeEndpoint endpoint );

    /*
     * Set the runtime config
     * @param RuntimeConfig to be set
     * @see RuntimeConfig
     */
    void setConfig( RuntimeConfig config );

    /*
     * Get the runtime config
     * @return RuntimeConfig for this Runtime
     * @see RuntimeConfig
     */
    RuntimeConfig getConfig();

    /*
     * Set the runtime state
     * @param RuntimeState to be set
     * @see RuntimeState
     */
    void setState( RuntimeState state );

    /*
     * Get the runtime state
     * @return RuntimeState for this Runtime
     * @see RuntimeState
     */
    RuntimeState getState();

    /*
     * Set the runtime info
     * @param RuntimeInfo to be set
     * @see RuntimeInfo
     */
    void setInfo( RuntimeInfo info );

    /*
     * Get the runtime info
     * @return RuntimeInfo for this Runtime
     * @see RuntimeInfo
     */
    RuntimeInfo getInfo();

}
