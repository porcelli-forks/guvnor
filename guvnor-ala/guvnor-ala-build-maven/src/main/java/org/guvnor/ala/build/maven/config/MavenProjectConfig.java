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
package org.guvnor.ala.build.maven.config;

import org.guvnor.ala.config.ProjectConfig;

/*
 * Maven specific Project configuration. This interface represents the basic information needed
 *  to configure a Maven Project. 
 * @see ProjectConfig
 */
public interface MavenProjectConfig extends ProjectConfig {

    /*
     * Get the Project Base Dir
     * in case of the Project Base Dir is not provided, 
     *  the expression ${input.project-dir} will be resolved by the pipeline input's map
     * @return String with the project dir path.
    */
    default String getProjectDir() {
        return "${input.project-dir}";
    }

    default String getProjectTempDir() {
        return "${input.project-temp-dir}";
    }

    default boolean preserveTempDir() {
        return Boolean.parseBoolean( "${input.preserve-temp-dir}" );
    }

}
