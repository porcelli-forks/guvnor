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

import org.uberfire.java.nio.file.Path;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.source.Repository;
import org.guvnor.ala.source.Source;

/**
 * @author salaboy
 * @TODO: This is a not thread-safe implementation for local testing. A more
 * robust and distributed implementation should be provided for real use cases.
 * All the lookups mechanisms and structures needs to be improved for
 * performance.
 */
public class InMemorySourceRegistry implements SourceRegistry {

    private final Map<Path, Repository> repositorySourcesPath;
    //Store the repository id and path for reverse lookup
    private final Map<String, Path> pathByRepositoryId;
    private final Map<Repository, List<Project>> projectsByRepo;

    public InMemorySourceRegistry() {
        repositorySourcesPath = new HashMap<>();
        pathByRepositoryId = new HashMap<>();
        projectsByRepo = new HashMap<>();
    }

    @Override
    public void registerRepositorySources( final Path path,
                                           final Repository repo ) {
        repositorySourcesPath.put( path, repo );
        pathByRepositoryId.put( repo.getId(), path );
    }

    @Override
    public Path getRepositoryPath( Repository repo ) {
        return pathByRepositoryId.get( repo.getId() );
    }

    @Override
    public Path getRepositoryPathById( String repoId ) {
        return pathByRepositoryId.get( repoId );
    }

    @Override
    public Repository getRepositoryByPath( Path path ) {
        return repositorySourcesPath.get( path );
    }

    @Override
    public List<Repository> getAllRepositories() {
        return new ArrayList<>( repositorySourcesPath.values() );
    }

    @Override
    public void registerProject( Repository repo,
                                 Project project ) {
        projectsByRepo.putIfAbsent( repo, new ArrayList<>() );
        projectsByRepo.get( repo ).add( project );
    }

    @Override
    public List<Project> getAllProjects( Repository repository ) {
        List<Project> allProjects = new ArrayList<>();
        for ( Repository r : projectsByRepo.keySet() ) {
            allProjects.addAll( projectsByRepo.get( r ) );
        }
        return allProjects;
    }

    @Override
    public List<Project> getProjectByName( String projectName ) {
        List<Project> projectsByName = new ArrayList<>();
        // Nasty Lookup, fix and improve this for distributed implementation
        for ( Repository r : projectsByRepo.keySet() ) {
            for ( Project p : projectsByRepo.get( r ) ) {
                if ( p.getName().equals( projectName ) ) {
                    projectsByName.add( p );
                }
            }
        }
        return projectsByName;
    }

    @Override
    public Repository getRepositoryById( String repositoryId ) {
        return repositorySourcesPath.get( pathByRepositoryId.get( repositoryId ) );
    }

    @Override
    public void registerSource( final Repository repo,
                                final Source source ) {

    }

    @Override
    public void registerProject( final Source source,
                                 final Project projectConfig ) {

    }

}
