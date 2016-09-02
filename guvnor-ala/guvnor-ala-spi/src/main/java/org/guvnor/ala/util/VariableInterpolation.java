package org.guvnor.ala.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.lang.text.StrSubstitutor;
import org.guvnor.ala.config.CloneableConfig;

/**
 * This class deals with Variable Interpolations inside pipelines. 
 * It uses byteman to create intermediary types for the results 
 *  created by the interpolation process
 */
public final class VariableInterpolation {

    private VariableInterpolation() {

    }

    private static final ConfigurationInterpolator interpolator = new ConfigurationInterpolator();
    private static final StrSubstitutor substitutor = new StrSubstitutor( interpolator );

    public static <T> T interpolate( final Map<String, Object> values,
                                     final T object ) {
        interpolator.setDefaultLookup( new MapOfMapStrLookup( values ) );
        return proxy( object );
    }

    static class MapOfMapStrLookup extends StrLookup {

        private final Map map;

        MapOfMapStrLookup( Map map ) {
            this.map = map;
        }

        @Override
        public String lookup( String key ) {
            if ( this.map == null ) {
                return null;
            } else {
                int dotIndex = key.indexOf( "." );
                Object obj = this.map.get( key.substring( 0, dotIndex < 0 ? key.length() : dotIndex ) );
                if ( obj instanceof Map ) {
                    return new MapOfMapStrLookup( ( (Map) obj ) ).lookup( key.substring( key.indexOf( "." ) + 1 ) );
                } else if ( obj != null && !( obj instanceof String ) && key.contains( "." ) ) {
                    final String subkey = key.substring( key.indexOf( "." ) + 1 );
                    for ( PropertyDescriptor descriptor : new PropertyUtilsBean().getPropertyDescriptors( obj ) ) {
                        if ( descriptor.getName().equals( subkey ) ) {
                            try {
                                return descriptor.getReadMethod().invoke( obj ).toString();
                            } catch ( Exception ex ) {
                                continue;
                            }
                        }
                    }
                }

                return obj == null ? null : obj.toString();
            }
        }
    }

    public static <T> T proxy( final T instance ) {
        try {
            final Class<?>[] _interfaces;
            if ( instance.getClass().getInterfaces().length == 0 ) {
                _interfaces = instance.getClass().getSuperclass().getInterfaces();
            } else {
                _interfaces = instance.getClass().getInterfaces();
            }

            T result = (T) new ByteBuddy()
                    .subclass( Object.class )
                    .implement( _interfaces )
                    .method( ElementMatchers.any() )
                    .intercept( InvocationHandlerAdapter.of( new InterpolationHandler( instance ) ) )
                    .make()
                    .load( instance.getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION )
                    .getLoaded()
                    .newInstance();
            if ( instance instanceof CloneableConfig ) {
                return (T) ( (CloneableConfig) result ).asNewClone( result );
            }
            return result;
        } catch ( final Exception ignored ) {
            ignored.printStackTrace();
            return instance;
        }
    }

    public static class InterpolationHandler implements InvocationHandler {

        Object object;

        public InterpolationHandler( final Object object ) {
            this.object = object;
        }

        @Override
        public Object invoke( Object proxy,
                              Method method,
                              Object[] args ) throws Throwable {
            Object result = method.invoke( object, args );

            if ( result != null && result instanceof String ) {
                return substitutor.replace( (String) result );
            } else {
                return result;
            }
        }
    }
}