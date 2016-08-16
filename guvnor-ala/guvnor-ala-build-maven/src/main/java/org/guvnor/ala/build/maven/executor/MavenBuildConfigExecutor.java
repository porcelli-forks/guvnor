package org.guvnor.ala.build.maven.executor;

import java.util.Optional;

import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.MavenBuildConfig;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;

public class MavenBuildConfigExecutor implements BiFunctionConfigExecutor<Project, MavenBuildConfig, BuildConfig> {

    @Override
    public Optional<BuildConfig> apply( final Project project,
                                        final MavenBuildConfig mavenBuildConfig ) {
        return Optional.of( new MavenBuild( project, mavenBuildConfig.getGoals() ) );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return MavenBuildConfig.class;
    }

    @Override
    public String outputId() {
        return "maven-config";
    }

    @Override
    public String inputId() {
        return "maven-config";
    }

}
