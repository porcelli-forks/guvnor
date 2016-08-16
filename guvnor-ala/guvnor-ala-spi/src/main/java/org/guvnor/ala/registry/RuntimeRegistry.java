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

package org.guvnor.ala.registry;

import java.util.List;
import java.util.Optional;

import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;

public interface RuntimeRegistry {

    void registerProviderType( final ProviderType pt );

    List<ProviderType> getAllProviderTypes();

    ProviderType getProviderTypeByName( final String provider );

    void unregisterProviderType( final ProviderType providerType );

    void registerProvider( final Provider provider );

    Provider getProvider( final String providerName );

    List<Provider> getAllProviders();

    List<Provider> getProvidersByType( final ProviderType type );

    void unregisterProvider( final Provider provider );

    void unregisterProvider( final String providerName );

    void registerRuntime( final Runtime runtime );

    List<Runtime> getAllRuntimes();

    List<Runtime> getRuntimesByProvider( final ProviderType provider );

    Runtime getRuntimeById( final String id );

    void unregisterRuntime( final RuntimeId runtime );

    <T extends Provider> Optional<T> getProvider( final ProviderId providerId,
                                                  final Class<T> clazz );
}
