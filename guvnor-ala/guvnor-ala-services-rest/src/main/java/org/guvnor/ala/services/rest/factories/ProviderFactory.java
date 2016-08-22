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

package org.guvnor.ala.services.rest.factories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import org.guvnor.ala.config.ProviderConfig;
import org.guvnor.ala.runtime.providers.Provider;
import org.guvnor.ala.runtime.providers.ProviderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ProviderFactory {

    private final Collection<ProviderBuilder> builders = new ArrayList<>();

    @Inject
    private BeanManager beanManager;
    
    protected static final Logger LOG = LoggerFactory.getLogger( ProviderFactory.class );
    
    public ProviderFactory( ) {
    }
    
    @PostConstruct
    public void init() {
        Set<Bean<?>> beans = beanManager.getBeans( ProviderBuilder.class, new AnnotationLiteral<Any>() {
        } );
        for ( final Bean b : beans ) {
            try {
                // I don't want to register the CDI proxy, I need a fresh instance :(
                ProviderBuilder pb = ( ProviderBuilder ) b.getBeanClass().newInstance();
                builders.add( pb );
            } catch ( InstantiationException | IllegalAccessException ex ) {
                LOG.error( "Something went wrong with registering Provider Builders", ex );
            }
        }

    }

    public Optional<Provider> newProvider( ProviderConfig config ) {
        final Optional<ProviderBuilder> providerBuilder = builders.stream()
                .filter( p -> p.supports( config ) )
                .findFirst();
        if ( providerBuilder.isPresent() ) {
            return (Optional<Provider>) providerBuilder.get().apply( config );
        }
        return Optional.empty();
    }
}
