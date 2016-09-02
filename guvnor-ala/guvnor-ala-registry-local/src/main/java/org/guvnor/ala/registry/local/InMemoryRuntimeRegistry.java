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

package org.guvnor.ala.registry.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.Runtime;
import org.guvnor.ala.runtime.RuntimeId;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.guvnor.ala.runtime.providers.ProviderType;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * @TODO: This is a not thread-safe implementation for local testing. A
 * more robust and distributed implementation should be provided for real
 * use cases. All the lookups mechanisms and structures needs to be improved for performance.
 */
@ApplicationScoped
public class InMemoryRuntimeRegistry implements RuntimeRegistry {

    private final Map<String, ProviderType> providerTypes;
    private final Map<String, Provider> providers;
    private final Map<ProviderType, List<Provider>> providersByType;
    private final Map<ProviderType, List<Runtime>> runtimesByProviderType;

    public InMemoryRuntimeRegistry() {
        providerTypes = new HashMap<>();
        providers = new HashMap<>();
        providersByType = new HashMap<>();
        runtimesByProviderType = new HashMap<>();
    }

    @Override
    public void registerProviderType( ProviderType pt ) {
        providerTypes.put( pt.getProviderTypeName(), pt );
    }

    @Override
    public List<ProviderType> getAllProviderTypes() {
        return new ArrayList<>( providerTypes.values() );
    }

    @Override
    public ProviderType getProviderType( String provider ) {
        return providerTypes.get( provider );
    }

    @Override
    public void unregisterProviderType( ProviderType providerType ) {
        providerTypes.remove( providerType.getProviderTypeName() );
    }

    @Override
    public void registerProvider( Provider provider ) {
        providers.put( provider.getId(), provider );
        if ( !providersByType.containsKey( provider.getProviderType() ) ) {
            providersByType.put( provider.getProviderType(), new ArrayList<>() );
        }
        providersByType.get( provider.getProviderType() ).add( provider );
    }

    @Override
    public List<Provider> getAllProviders() {
        return new ArrayList<>( providers.values() );
    }

    @Override
    public List<Provider> getProvidersByType( ProviderType type ) {
        return providersByType.get( type );
    }

    @Override
    public Provider getProvider( String providerName ) {
        return providers.get( providerName );
    }

    @Override
    public void unregisterProvider( Provider provider ) {
        providersByType.get( provider.getProviderType() ).remove( provider );
        providers.remove( provider.getId() );
    }

    @Override
    public void unregisterProvider( String providerName ) {
        for ( Provider p : providers.values() ) {
            if ( p.getId().equals( providerName ) ) {
                unregisterProvider( p );
            }
        }
    }

    @Override
    public void registerRuntime( Runtime runtime ) {
        final Provider provider = providers.get( runtime.getProviderId().getId() );

        if ( !runtimesByProviderType.containsKey( provider.getProviderType() ) ) {
            runtimesByProviderType.put( provider.getProviderType(), new ArrayList<>() );
        }
        runtimesByProviderType.get( provider.getProviderType() ).add( runtime );
    }

    @Override
    public List<Runtime> getAllRuntimes() {
        List<Runtime> runtimes = new ArrayList<>();
        for ( List<Runtime> rs : runtimesByProviderType.values() ) {
            runtimes.addAll( rs );
        }
        return runtimes;
    }

    @Override
    public List<Runtime> getRuntimesByProvider( ProviderType providerType ) {
        return new ArrayList<>( runtimesByProviderType.get( providerType ) );
    }

    @Override
    public Runtime getRuntimeById( String id ) {
        for ( ProviderType pt : runtimesByProviderType.keySet() ) {
            for ( Runtime r : runtimesByProviderType.get( pt ) ) {
                if ( r.getId().equals( id ) ) {
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public <T extends Provider> Optional<T> getProvider( final ProviderId providerId,
                                                         final Class<T> clazz ) {
        checkNotNull( "providerId", providerId );
        checkNotNull( "clazz", clazz );

        final Provider value = providers.get( providerId.getId() );
        if ( value == null ||
                !clazz.isAssignableFrom( value.getClass() ) ) {
            return Optional.empty();
        }
        return Optional.of( clazz.cast( value ) );
    }

    @Override
    public void unregisterRuntime( final RuntimeId runtime ) {
        final Provider provider = providers.get( runtime.getProviderId().getId() );
        runtimesByProviderType.get( provider.getProviderType() ).remove( runtime );
    }

}