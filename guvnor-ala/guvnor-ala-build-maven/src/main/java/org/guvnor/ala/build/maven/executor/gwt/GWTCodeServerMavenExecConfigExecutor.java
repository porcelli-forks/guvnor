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
package org.guvnor.ala.build.maven.executor.gwt;

import java.io.File;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.maven.cli.MavenCli;
import org.apache.mina.util.ConcurrentHashSet;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.gwt.GWTCodeServerMavenExecConfig;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.gwt.CodeServerPortHandle;
import org.guvnor.ala.exceptions.BuildException;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;

public class GWTCodeServerMavenExecConfigExecutor implements FunctionConfigExecutor<MavenBuild, MavenBuild> {

    private static final String GWT_CODE_SERVER_PORT = "gwt.codeServerPort";
    private static final String GWT_CODE_SERVER_LAUNCHER_DIR = "gwt.codeServer.launcherDir";
    private static final int CODE_SERVER_LOWEST_PORT = 50000;
    private static final int CODE_SERVER_HIGHEST_PORT = 50100;
    private final Set<Integer> leasedCodeServerPorts = new ConcurrentHashSet<Integer>();

    public GWTCodeServerMavenExecConfigExecutor() {
    }

    @Override
    public Optional<MavenBuild> apply( final MavenBuild mavenBuild ) {
        final File webappFolder = new File( getRepositoryVisitor( mavenBuild.getProject() ).getProjectFolder().getAbsolutePath(), "src/main/webapp" );
        int result = build( mavenBuild.getProject(), asList( "gwt:run-codeserver",
                "-D" + GWT_CODE_SERVER_LAUNCHER_DIR + "=" + webappFolder.getAbsolutePath(),
                "-D" + GWT_CODE_SERVER_PORT + "=" + String.valueOf( getAvailableCodeServerPort().getPortNumber() ) ) );
        if ( result != 0 ) {
            throw new RuntimeException( "Cannot Start a GWT Code Server Look at the previous logs for more information." );

        }

        return Optional.of( mavenBuild );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return GWTCodeServerMavenExecConfig.class;
    }

    @Override
    public String outputId() {
        return "codeServer";
    }

    @Override
    public String inputId() {
        return "gwt-codeserver-config";
    }

    public int build( final Project project,
            final List<String> goals ) throws BuildException {
        return executeMaven( project, goals.toArray( new String[]{} ) );
    }

    private int executeMaven( final Project project,
            final String... goals ) {

        return new MavenCli().doMain( goals,
                getRepositoryVisitor( project ).getProjectFolder().getAbsolutePath(),
                System.err, System.err );
    }

    private RepositoryVisitor getRepositoryVisitor( final Project project ) {
        return new RepositoryVisitor( project );
    }

    private CodeServerPortHandle getAvailableCodeServerPort() {
        return new CodeServerPortHandle() {

            private Integer leasedPort = leaseAvailableCodeServerPort();

            @Override
            public void relinquishPort() {
                leasedCodeServerPorts.remove( leasedPort );
                leasedPort = null;
            }

            @Override
            public Integer getPortNumber() {
                if ( leasedPort != null ) {
                    return leasedPort;
                } else {
                    throw new RuntimeException( "Cannot get port number after relinquishing." );
                }
            }
        };
    }

    private synchronized Integer leaseAvailableCodeServerPort() {
        Integer port = CODE_SERVER_LOWEST_PORT;

        while ( port <= CODE_SERVER_HIGHEST_PORT && leasedCodeServerPorts.contains( port ) ) {
            port++;
        }

        if ( port > CODE_SERVER_HIGHEST_PORT ) {
            throw new RuntimeException( "All available code server ports are in use." );
        }

        leasedCodeServerPorts.add( port );

        return port;
    }

}
