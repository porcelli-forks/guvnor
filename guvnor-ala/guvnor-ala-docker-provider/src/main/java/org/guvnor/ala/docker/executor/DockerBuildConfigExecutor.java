package org.guvnor.ala.docker.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.docker.config.DockerBuildConfig;
import org.guvnor.ala.docker.model.DockerBuildImpl;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;

public class DockerBuildConfigExecutor implements BiFunctionConfigExecutor<MavenBuild, DockerBuildConfig, BuildConfig> {

    @Override
    public Optional<BuildConfig> apply( final MavenBuild buildConfig,
                                        final DockerBuildConfig dockerBuildConfig ) {
        final List<String> goals = new ArrayList<>( buildConfig.getGoals() );
        if ( dockerBuildConfig.push() ) {
            goals.add( "-Ddocker.username=" + dockerBuildConfig.getUsername() );
            goals.add( "-Ddocker.password=" + dockerBuildConfig.getPassword() );
        }
        goals.add( "docker:build" );
        if ( dockerBuildConfig.push() ) {
            goals.add( "docker:push" );
        }
        return Optional.of( new DockerBuildImpl( buildConfig.getProject(), goals ) );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return DockerBuildConfig.class;
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
