/*
 * Copyright 2016 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.build.maven.executor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;

import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitHub;
import org.guvnor.ala.source.git.GitRepository;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.kie.scanner.embedder.MavenProjectLoader;
import org.uberfire.java.nio.file.Files;

/**
 *
 */
public class MavenProjectLoaderTest {

    private File tempPath;

    @Before
    public void setUp() throws IOException {
        tempPath = java.nio.file.Files.createTempDirectory( "ccc" ).toFile();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly( tempPath );
    }

    @Test
    public void helloMavenProjectLoader() {
        final GitHub gitHub = new GitHub();
        final GitRepository repository = ( GitRepository ) gitHub.getRepository( "salaboy/drools-workshop", new HashMap<String, String>() {
            {
                put( "out-dir", tempPath.getAbsolutePath() );
            }
        } );
        final Source source = repository.getSource( "master" );
        assertNotNull( source );
        InputStream pomStream = Files.newInputStream( source.getPath().resolve( "drools-webapp-example" ).resolve( "pom.xml" ) );

        MavenProject project = MavenProjectLoader.parseMavenPom( pomStream );

        assertNotNull( project );
        System.out.println( "Project " + project );
        pomStream = Files.newInputStream( source.getPath().resolve( "drools-webapp-example" ).resolve( "pom.xml" ) );

        project = MavenProjectLoader.parseMavenPom( pomStream );

        assertNotNull( project );
        System.out.println( "Project " + project );

    }

}
