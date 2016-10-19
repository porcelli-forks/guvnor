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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.guvnor.ala.build.Project;

import org.guvnor.ala.build.maven.model.impl.MavenProjectImpl;
import org.guvnor.ala.build.maven.util.MavenBuildExecutor;

import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitHub;
import org.guvnor.ala.source.git.GitRepository;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.kie.scanner.embedder.MavenProjectLoader;

/**
 *
 */
public class MavenCliOutputTest {

    public MavenCliOutputTest() {
    }

    private File tempPath;

    @Before
    public void setUp() throws IOException {
        tempPath = Files.createTempDirectory("zzz").toFile();
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(tempPath);
    }

    @Test
    public void waitForAppBuildTest() {
        final GitHub gitHub = new GitHub();
        final GitRepository repository = (GitRepository) gitHub.getRepository("salaboy/livespark-playground", new HashMap<String, String>() {
            {
                put("out-dir", tempPath.getAbsolutePath());
            }
        });
        final Source source = repository.getSource("master");
        boolean isCodeServerReady = false;
        Throwable error = null;
        PipedOutputStream baosOut = new PipedOutputStream();
        PipedOutputStream baosErr = new PipedOutputStream();
        final PrintStream out = new PrintStream(baosOut, true);
        final PrintStream err = new PrintStream(baosErr, true);
        PrintStream oldout = System.out;
        PrintStream olderr = System.err;
        new Thread(() -> {
            List<String> goals = new ArrayList<>();
            goals.add("package");
            Properties p = new Properties();
            p.setProperty("failIfNoTests", "false");

            final InputStream pomStream = org.uberfire.java.nio.file.Files.newInputStream(source.getPath().resolve("users-new").resolve("pom.xml"));
            MavenProject project = MavenProjectLoader.parseMavenPom(pomStream);

            final String expectedBinary = project.getArtifact().getArtifactId() + "-" + project.getArtifact().getVersion() + "." + project.getArtifact().getType();
            final org.guvnor.ala.build.maven.model.MavenProject mavenProject = new MavenProjectImpl(project.getId(),
                    project.getArtifact().getType(),
                    project.getName(),
                    expectedBinary,
                    source.getPath(),
                    source.getPath().resolve("users-new"),
                    source.getPath().resolve("target").resolve(expectedBinary).toAbsolutePath(),
                    null);
            final File pom = new File(getRepositoryVisitor(mavenProject).getProjectFolder(), "pom.xml");
            int executeMaven = MavenBuildExecutor.executeMaven(pom, out, err, p, goals.toArray(new String[0]));
            assertTrue(executeMaven == 0);

        }).start();
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader bufferedReader;
            bufferedReader = new BufferedReader(new InputStreamReader(new PipedInputStream(baosOut)));
            String line;

            while (!(isCodeServerReady || error != null)) {

                if ((line = bufferedReader.readLine()) != null) {
                    sb.append(line).append("\n");
                    if (line.contains("BUILD SUCCESS")) {
                        isCodeServerReady = true;
                        out.close();
                        err.close();
                        baosOut.close();
                        baosErr.close();
                        System.setOut(oldout);
                        System.setErr(olderr);
                    } else if (line.contains("BUILD FAILURE")) {
                        isCodeServerReady = true;
                        error = new IllegalStateException("damn, we find an issue with the build");
                        out.close();
                        err.close();
                        baosOut.close();
                        baosErr.close();
                        System.setOut(oldout);
                        System.setErr(olderr);
                    }
                }

                //@TODO: send line to client
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // The pipestream will be closed by MavenCli, so this exception is expected to happen when the process finishes
            // But both variables isCodeServerReady and error should be in teh correct state

        }
        String s = sb.toString();
        assertTrue(isCodeServerReady);
        assertTrue(error == null);

    }

    private RepositoryVisitor getRepositoryVisitor(final Project project) {
        return new RepositoryVisitor(project);
    }
}
