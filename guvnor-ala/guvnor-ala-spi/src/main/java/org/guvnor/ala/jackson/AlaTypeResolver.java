/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.guvnor.ala.jackson;

import java.lang.reflect.Method;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;

public class AlaTypeResolver implements TypeIdResolver {

    private JavaType baseType;

    @Override
    public void init( final JavaType baseType ) {
        this.baseType = baseType;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    @Override
    public String idFromValue( Object obj ) {

        return idFromValueAndType( obj, obj.getClass() );
    }

    @Override
    public String idFromBaseType() {
        System.err.println( "idFromBaseType called!" );
        return "nothing";
    }

    @Override
    public String idFromValueAndType( final Object obj,
                                      final Class<?> clazz ) {
        try {
            final Method method = obj.getClass().getMethod( "getType" );
            if ( method == null ) {
                return idFromBaseType();
            }
            return method.invoke( obj ).toString();
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        return idFromBaseType();
    }

    @Override
    public JavaType typeFromId( final DatabindContext context,
                                final String type ) {
        return context.constructSpecializedType( baseType, getType( type ) );
    }

    @Override
    public JavaType typeFromId( final String type ) {
        return TypeFactory.defaultInstance().constructSpecializedType( baseType, getType( type ) );
    }

    private Class<?> getType( final String type ) {
        try {
            return ClassUtil.findClass( type );
        } catch ( ClassNotFoundException e ) {
            throw new IllegalStateException( "cannot find class '" + type + "'" );
        }
    }

}
