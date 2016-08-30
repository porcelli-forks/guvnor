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
package org.guvnor.ala.docker.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.base.BaseProvider;

@JsonTypeName( value = "DockerProvider")
public class DockerProvider extends BaseProvider implements ProviderConfig {

    private String hostId;

    public DockerProvider() {
    }

    
    public DockerProvider( final String name,
                           final String hostId ) {
        super( name, DockerProviderType.instance() );
        this.hostId = hostId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId( String hostId ) {
        this.hostId = hostId;
    }
    
}
