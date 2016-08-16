package org.guvnor.ala.build.maven.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;

import org.apache.maven.cli.MavenCli;
import org.guvnor.ala.build.Project;
import org.guvnor.ala.build.maven.config.MavenBuildExecConfig;
import org.guvnor.ala.build.maven.model.MavenBinary;
import org.guvnor.ala.build.maven.model.MavenBuild;
import org.guvnor.ala.build.maven.util.RepositoryVisitor;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.exceptions.BuildException;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.ala.registry.BuildRegistry;

public class MavenBuildExecConfigExecutor implements BiFunctionConfigExecutor<MavenBuild, MavenBuildExecConfig, BinaryConfig> {

    private final BuildRegistry buildRegistry;

    @Inject
    public MavenBuildExecConfigExecutor( final BuildRegistry buildRegistry ) {
        this.buildRegistry = buildRegistry;
    }

    @Override
    public Optional<BinaryConfig> apply( final MavenBuild mavenBuild,
                                         final MavenBuildExecConfig mavenBuildExecConfig ) {
        int result = build( mavenBuild.getProject(), mavenBuild.getGoals() );
        if(result != 0){
            throw new RuntimeException("Cannot build Maven Project. Look at the previous logs for more information.");
            
        }
        final MavenBinary binary = new MavenBinary( mavenBuild.getProject() );
        buildRegistry.registerBinary( binary );
        return Optional.of( binary );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return MavenBuildExecConfig.class;
    }

    @Override
    public String outputId() {
        return "binary";
    }

    @Override
    public String inputId() {
        return "maven-exec-config";
    }

    private final Map<Project, RepositoryVisitor> projectVisitorMap = new HashMap<>();

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
        final RepositoryVisitor projectVisitor;
        if ( projectVisitorMap.containsKey( project ) ) {
            projectVisitor = projectVisitorMap.get( project );
        } else {
            projectVisitor = new RepositoryVisitor( project );
            projectVisitorMap.put( project, projectVisitor );
        }
        return projectVisitor;
    }

}
