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

package org.guvnor.ala.build;

import org.uberfire.java.nio.file.Path;
import org.guvnor.ala.exceptions.BuildException;

/*
 *         The Build interface define a set of operations to generate
 *         Binaries based on Projects
 */
public interface Build {

    /*
    *   Build the specified Project
    *   @param project a Project to build
    *   @throws BuildException if the build failed unexpectedly 
    *   @return the build output 0 if there wasn't an error
    *   @see Project
    */
    int build( Project project ) throws BuildException;

    /*
    * Check for the generated binaries for a given project
    * @param project a Project to check if the binaries are ready
    * @return true if the binaries are already generated
    * @see Project
    */
    boolean binariesReady( Project project ) throws BuildException;

    /* 
    * Returns the Path of the binaries if exist
    * @param project the project for the binaries that we are looking for 
    * @return String with the location of the binaries
    * @see Project
    */
    Path binariesPath( Project project ) throws BuildException;

    /* 
    * Clean the generated binaries if exist
    * @param project the project for the binaries that we are looking to clean 
    * @return the clean output 0 if there wasn't an error
    * @see Project
    */
    int cleanBinaries( Project project ) throws BuildException;

    /*
    * Create a Docker Image for the given project
    * @return the output of the execution, if 0 the docker image was created
    * @see Project
    */
    int createDockerImage( Project project,
                           boolean push,
                           String username,
                           String password ) throws BuildException;
}
