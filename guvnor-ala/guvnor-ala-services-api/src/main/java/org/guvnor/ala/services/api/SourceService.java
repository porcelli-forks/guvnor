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

package org.guvnor.ala.services.api;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.services.exceptions.BusinessException;
import org.guvnor.ala.source.Repository;

import static javax.ws.rs.core.MediaType.*;

@Path("sources")
public interface SourceService {

    @GET
    @Produces(value = APPLICATION_JSON)
    @Path("")
    List<Repository> getAllRepositories() throws BusinessException;

    @GET
    @Produces(value = APPLICATION_JSON)
    @Path("{id}/path")
    String getPathByRepositoryId( @PathParam("id") String repoId ) throws BusinessException;

    @POST
    @Consumes(value = APPLICATION_JSON)
    @Produces(value = APPLICATION_JSON)
    @Path("")
    String registerRepository( Repository repo ) throws BusinessException;

    @POST
    @Consumes(value = APPLICATION_JSON)
    @Path("{id}")
    void registerProject( @PathParam("id") String repositoryId,
                          @NotNull Project project ) throws BusinessException;

    @GET
    @Consumes(value = APPLICATION_JSON)
    @Produces(value = APPLICATION_JSON)
    @Path("{id}/projects")
    List<Project> getAllProjects( @PathParam("id") String repositoryId ) throws BusinessException;

}
