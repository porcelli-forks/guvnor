/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.guvnor.ala.build.maven.util;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.maven.execution.MavenExecutionResult;
import org.guvnor.ala.build.Project;
import org.kie.scanner.embedder.MavenEmbedder;
import org.kie.scanner.embedder.MavenEmbedderException;
import org.kie.scanner.embedder.MavenProjectLoader;
import org.kie.scanner.embedder.MavenRequest;

public final class MavenBuildExecutor {

    private MavenBuildExecutor() {
    }

    public static int executeMaven( final File pom,
                                    final Properties properties,
                                    final String... goals ) {
        return executeMaven( pom, System.out, System.err, properties, goals );
    }

    public static int executeMaven( final File pom,
                                    final PrintStream stdout,
                                    final PrintStream stderr,
                                    final Properties properties,
                                    final String... goals ) {
        final PrintStream oldout = System.out;
        final PrintStream olderr = System.err;
        System.setProperties( properties );

        final MavenEmbedder mavenEmbedder = newMavenEmbedder();
        try {
            if ( stdout != null ) {
                System.setOut( stdout );
            }
            if ( stderr != null ) {
                System.setErr( stderr );
            }

            final MavenRequest mavenRequest = MavenProjectLoader.createMavenRequest( false );
            mavenRequest.setGoals( Arrays.asList( goals ) );
            mavenRequest.setPom( pom.getAbsolutePath() );
            final MavenExecutionResult result = mavenEmbedder.execute( mavenRequest );
        } catch ( final MavenEmbedderException ex ) {
            return -1;
        } finally {
            mavenEmbedder.dispose();
            System.setOut( oldout );
            System.setErr( olderr );
        }
        return 0;
    }

    private static MavenEmbedder newMavenEmbedder() {
        MavenEmbedder mavenEmbedder;
        try {
            mavenEmbedder = new MavenEmbedder( MavenProjectLoader.createMavenRequest( false ) );
        } catch ( MavenEmbedderException e ) {
            throw new RuntimeException( e );
        }
        return mavenEmbedder;
    }

    public static RepositoryVisitor getRepositoryVisitor( final Project project ) {
        return new RepositoryVisitor( project );
    }

}
