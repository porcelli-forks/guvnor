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

package org.guvnor.ala.services.api.backend;

import java.util.List;

import org.jboss.errai.bus.server.annotations.Remote;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.services.exceptions.BusinessException;
import org.guvnor.ala.source.Repository;

@Remote
public interface SourceServiceBackend {

    List<Repository> getAllRepositories() throws BusinessException;

    String getPathByRepositoryId( final String repoId ) throws BusinessException;

    String registerRepository( final Repository repo ) throws BusinessException;

    void registerProject( final String repositoryId,
                          final Project project ) throws BusinessException;

    List<Project> getAllProjects( final String repositoryId ) throws BusinessException;

}
