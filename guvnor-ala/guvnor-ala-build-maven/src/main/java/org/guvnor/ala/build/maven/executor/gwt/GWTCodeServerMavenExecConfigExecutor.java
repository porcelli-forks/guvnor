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
import java.util.Properties;
import javax.inject.Inject;

import org.guvnor.ala.build.maven.config.gwt.GWTCodeServerMavenExecConfig;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.exceptions.BuildException;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;

import static org.guvnor.ala.build.maven.util.MavenBuildExecutor.*;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;

public class GWTCodeServerMavenExecConfigExecutor implements BiFunctionConfigExecutor<MavenBuild, GWTCodeServerMavenExecConfig, MavenBuild> {

    private static final String GWT_CODE_SERVER_PORT = "gwt.codeServerPort";
    private static final String GWT_CODE_SERVER_LAUNCHER_DIR = "gwt.codeServer.launcherDir";
    private static final String GWT_CODE_SERVER_BIND_ADDRESS = "gwt.bindAddress";
    private boolean isCodeServerReady = false;
    private volatile Throwable error = null;
    private GWTCodeServerPortLeaser leaser;
    @Inject
    public GWTCodeServerMavenExecConfigExecutor(GWTCodeServerPortLeaser leaser) {
        this.leaser = leaser;
        
    }

    @Override
    public Optional<MavenBuild> apply(final MavenBuild buildConfig,
            final GWTCodeServerMavenExecConfig config) {
        RepositoryVisitor repositoryVisitor = getRepositoryVisitor(buildConfig.getProject());
        final File projectFolder = repositoryVisitor.getProjectFolder();
        final File webappFolder = new File(projectFolder.getAbsolutePath(), "src/main/webapp");
        
        if((!leaser.isCodeServerRunning(buildConfig.getProject().getName()))){
            System.out.println("Starting GWT Code Server");
            final File pom = new File(projectFolder, "pom.xml");
           
            List<String> goals = new ArrayList<>();
            goals.add("gwt:run-codeserver");
            final Properties properties = new Properties(buildConfig.getProperties());
            properties.put(GWT_CODE_SERVER_LAUNCHER_DIR, webappFolder.getAbsolutePath());
            Integer portNumber = leaser.getAvailableCodeServerPort().getPortNumber();
            leaser.setCodeServerForProject(buildConfig.getProject().getName(), portNumber);
            properties.put(GWT_CODE_SERVER_PORT, String.valueOf(portNumber));
            properties.put(GWT_CODE_SERVER_BIND_ADDRESS, config.getBindAddress());
            build(pom, properties, goals);
        }else{
            System.out.println(" No need to start GWT Code Server");
        }

        return Optional.of(buildConfig);
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

    public void build(final File pom,
            final Properties properties,
            final List<String> goals) throws BuildException {
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        try {
            PipedOutputStream baosOut = new PipedOutputStream();
            PipedOutputStream baosErr = new PipedOutputStream();
            final PrintStream out = new PrintStream(baosOut, true);
            final PrintStream err = new PrintStream(baosErr, true);
            final PrintStream oldout = System.out;
            final PrintStream olderr = System.err;
            new Thread(() -> {
                executeMaven(pom, out, err, properties, goals.toArray(new String[]{}));
            }).start();
            bufferedReader = new BufferedReader(new InputStreamReader(new PipedInputStream(baosOut)));
            String line;

            while (!(isCodeServerReady || error != null)) {
                if ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                    if (line.contains("The code server is ready at")) {
                        isCodeServerReady = true;
                        out.close();
                        err.close();
                        baosOut.close();
                        baosErr.close();
                        System.setOut(oldout);
                        System.setErr(olderr);
                        System.out.println("Code Server Ready.. moving on... ");
                    }
                }
                //@TODO: send line to client
            }

        } catch (IOException ex) {
            // MavenCli will close abruptly the pipe when the build process finishes, so we just swallow this exception.
            ex.printStackTrace();
        }

    }
   

}
