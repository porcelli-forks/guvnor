package org.guvnor.ala.docker.model;

import java.util.List;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.model.MavenBuild;

/**
 * TODO: update me
 */
public class DockerBuild extends MavenBuild {

    public DockerBuild( final Project project,
                        final List<String> goals ) {
        super( project, goals );
    }

}
