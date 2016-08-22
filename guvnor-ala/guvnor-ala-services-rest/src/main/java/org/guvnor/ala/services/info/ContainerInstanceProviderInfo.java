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

package org.guvnor.ala.services.info;

import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;

/**
 */
@XmlRootElement
public class ContainerInstanceProviderInfo {

    private String name;
    private String provider;
    private Map<String, String> config;

    public ContainerInstanceProviderInfo() {
    }

    public ContainerInstanceProviderInfo( String name,
                                          String provider,
                                          Map<String, String> config ) {
        this.name = name;
        this.provider = provider;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider( String provider ) {
        this.provider = provider;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig( Map<String, String> config ) {
        this.config = config;
    }

}
