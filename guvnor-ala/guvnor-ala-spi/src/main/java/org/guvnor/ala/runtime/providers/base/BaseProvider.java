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
package org.guvnor.ala.runtime.providers.base;

import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderType;

public abstract class BaseProvider implements Provider {

    protected String name;
    protected ProviderConfig config;
    protected ProviderType providerType;

    public BaseProvider() {
    }

    public BaseProvider( final String name,
                         final ProviderType providerType ) {
        this.name = name;
        this.providerType = providerType;
    }

    @Override
    public String getId() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public ProviderConfig getConfig() {
        return config;
    }

    @Override
    public ProviderType getProviderType() {
        return providerType;
    }

}
