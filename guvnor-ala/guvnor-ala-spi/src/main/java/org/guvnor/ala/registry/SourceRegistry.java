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

import org.uberfire.java.nio.file.Path;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.source.Repository;
import org.guvnor.ala.source.Source;

public interface SourceRegistry {

    void registerRepositorySources( final Path path,
                                    final Repository repo );

    Path getRepositoryPath( final Repository repo );

    Repository getRepositoryByPath( final Path location );

    List<Repository> getAllRepositories();

    Path getRepositoryPathById( final String repoId );

    void registerProject( final Repository repo,
                          final Project project );

    List<Project> getAllProjects( final Repository repo );

    List<Project> getProjectByName( final String usersnew );

    Repository getRepositoryById( final String repositoryId );

    void registerSource( final Repository repo,
                         final Source source );

    void registerProject( final Source source,
                          final Project projectConfig );
}
