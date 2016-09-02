package org.guvnor.ala.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base Project configuration interface. Provide different implementations if you want to support 
 *  different project types
 * @see Project
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface ProjectConfig extends Config {

}
