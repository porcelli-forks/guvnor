package org.guvnor.ala.build.maven.config;

import org.guvnor.ala.config.ProjectConfig;

public interface MavenProjectConfig extends ProjectConfig {

    default String getProjectDir() {
        return "${input.project-dir}";
    }
}
