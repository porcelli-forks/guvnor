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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

import org.apache.maven.cli.MavenCli;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.gwt.GWTCodeServerMavenExecConfig;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.exceptions.BuildException;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;

import org.guvnor.ala.build.maven.model.MavenBuild;

public class GWTCodeServerMavenExecConfigExecutor implements BiFunctionConfigExecutor<MavenBuild, GWTCodeServerMavenExecConfig, MavenBuild> {

    private static final String GWT_CODE_SERVER_PORT = "gwt.codeServerPort";
    private static final String GWT_CODE_SERVER_LAUNCHER_DIR = "gwt.codeServer.launcherDir";
    private static final String GWT_CODE_SERVER_BIND_ADDRESS = "gwt.bindAddress";
    private boolean isCodeServerReady = false;
    private volatile Throwable error = null;
    private GWTCodeServerPortLeaser leaser;
    
    @Inject
    public GWTCodeServerMavenExecConfigExecutor( GWTCodeServerPortLeaser leaser ) {
        this.leaser = leaser;
    }

    @Override
    public Optional<MavenBuild> apply( final MavenBuild buildConfig,
            final GWTCodeServerMavenExecConfig config ) {
        final File webappFolder = new File( getRepositoryVisitor( buildConfig.getProject() ).getProjectFolder().getAbsolutePath(), "src/main/webapp" );
        List<String> goals = new ArrayList<>();
        goals.add( "gwt:run-codeserver" );
        goals.add( "-D" + GWT_CODE_SERVER_LAUNCHER_DIR + "=" + webappFolder.getAbsolutePath() );
        goals.add( "-D" + GWT_CODE_SERVER_PORT + "=" + String.valueOf( leaser.getAvailableCodeServerPort().getPortNumber() ) );
        goals.add( "-D" + GWT_CODE_SERVER_BIND_ADDRESS + "=" + config.getBindAddress() );

        build( buildConfig.getProject(), goals );

        return Optional.of( buildConfig );
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

    public void build( final Project project,
            final List<String> goals ) throws BuildException {
        BufferedReader bufferedReader = null;
        try {
            PipedOutputStream baosOut = new PipedOutputStream();
            PipedOutputStream baosErr = new PipedOutputStream();
            final PrintStream out = new PrintStream( baosOut, true );
            final PrintStream err = new PrintStream( baosErr, true );
            new Thread( () -> {
                executeMaven( project, out, err, goals.toArray( new String[]{} ) );
            } ).start();
            bufferedReader = new BufferedReader( new InputStreamReader( new PipedInputStream( baosOut ) ) );
            String line;
            StringBuilder sb = new StringBuilder();
            while ( !( isCodeServerReady || error != null ) ) {
                if ( ( line = bufferedReader.readLine() ) != null ) {
                    sb.append( line ).append( "\n" );
                    if ( line.contains( "The code server is ready at" ) ) {
                        isCodeServerReady = true;
                    }
                }
                //@TODO: send line to client
            }

        } catch ( IOException ex ) {
            // MavenCli will close abruptly the pipe when the build process finishes, so we just swallow this exception.
            ex.printStackTrace();
        }

    }

    private void executeMaven( final Project project, final PrintStream out, final PrintStream err,
            final String... goals ) {
        new MavenCli().doMain( goals,
                getRepositoryVisitor( project ).getProjectFolder().getAbsolutePath(),
                out, err );

    }

    private RepositoryVisitor getRepositoryVisitor( final Project project ) {
        return new RepositoryVisitor( project );
    }

    

}
