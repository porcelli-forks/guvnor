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

/*
 * BaseProvide implementation to be extended by concrete Providers
*/
public abstract class BaseProvider implements Provider {

    protected String id;
    protected ProviderConfig config;
    protected ProviderType providerType;

    public BaseProvider() {
    }

    public BaseProvider( final String id,
                         final ProviderType providerType ) {
        this.id = id;
        this.providerType = providerType;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    @Override
    public ProviderConfig getConfig() {
        return config;
    }

    @Override
    public ProviderType getProviderType() {
        return providerType;
    }

    public void setConfig( ProviderConfig config ) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "Provider{" + "id=" + id + ", config=" + config + ", providerType=" + providerType + '}';
    }
    
    

}
