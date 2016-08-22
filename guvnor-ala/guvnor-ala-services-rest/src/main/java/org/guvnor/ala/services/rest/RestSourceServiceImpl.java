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

package org.guvnor.ala.services.rest;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.services.api.SourceService;
import org.guvnor.ala.services.exceptions.BusinessException;
import org.guvnor.ala.source.Repository;


@ApplicationScoped
public class RestSourceServiceImpl implements SourceService {

    @Override
    public List<Repository> getAllRepositories() throws BusinessException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPathByRepositoryId( String repoId ) throws BusinessException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String registerRepository( Repository repo ) throws BusinessException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void registerProject( String repositoryId, Project project ) throws BusinessException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Project> getAllProjects( String repositoryId ) throws BusinessException {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }

//    @Inject
//    private SourceRegistry registry;
//
//    @PostConstruct
//    public void init() {
//        System.out.println( "Post Construct Source ServiceImpl here!" );
//    }
//
//    @Override
//    public List<Repository> getAllRepositories() throws BusinessException {
//        return registry.getAllRepositories();
//    }
//
//    @Override
//    public String getPathByRepositoryId( final String repositoryId ) throws BusinessException {
//        return registry.getRepositoryPathById( repositoryId ).toUri().toString();
//    }
//
//    @Override
//    public String registerRepository( final Repository repo ) throws BusinessException {
//        try {
//            registry.registerRepositorySources( repo.getSource().getPath(), repo );
//            return repo.getId();
//        } catch ( Exception ex ) {
//            Logger.getLogger( RestSourceServiceImpl.class.getName() ).log( Level.SEVERE, null, ex );
//            throw new BusinessException( ex.getMessage(), ex );
//        }
//
//    }
//
//    @Override
//    public void registerProject( String repositoryId,
//                                 Project project ) throws BusinessException {
//        Repository repo = registry.getRepositoryById( repositoryId );
//        registry.registerProject( repo, project );
//    }
//
//    @Override
//    public List<Project> getAllProjects( String repositoryId ) throws BusinessException {
//        Repository repo = registry.getRepositoryById( repositoryId );
//        return registry.getAllProjects( repo );
//    }

}
