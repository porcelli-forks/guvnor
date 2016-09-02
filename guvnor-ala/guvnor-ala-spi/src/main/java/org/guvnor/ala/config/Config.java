package org.guvnor.ala.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base Configuration Interface, you need to implements interface if you are creating a new 
 * configuratio type
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface Config {

}
