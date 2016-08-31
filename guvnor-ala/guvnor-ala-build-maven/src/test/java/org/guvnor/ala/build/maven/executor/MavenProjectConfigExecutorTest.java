package org.guvnor.ala.build.maven.executor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.guvnor.ala.build.Binary;
import org.guvnor.ala.build.maven.config.impl.MavenBuildConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenBuildExecConfigImpl;
import org.guvnor.ala.build.maven.config.impl.MavenProjectConfigImpl;
import org.guvnor.ala.config.BinaryConfig;
import org.guvnor.ala.config.BuildConfig;
import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.config.SourceConfig;
import org.guvnor.ala.pipeline.ContextAware;
import org.guvnor.ala.pipeline.Input;
import org.guvnor.ala.pipeline.Pipeline;
import org.guvnor.ala.pipeline.PipelineFactory;
import org.guvnor.ala.pipeline.Stage;
import org.guvnor.ala.pipeline.execution.PipelineExecutor;
import org.guvnor.ala.registry.BuildRegistry;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.registry.local.InMemoryBuildRegistry;
import org.guvnor.ala.registry.local.InMemorySourceRegistry;
import org.guvnor.ala.source.git.config.GitConfig;
import org.guvnor.ala.source.git.executor.GitConfigExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.*;
import static org.guvnor.ala.pipeline.StageUtil.*;

public class MavenProjectConfigExecutorTest {

    private File tempPath;

    @Before
    public void setUp() {
        try {
            tempPath = Files.createTempDirectory( "xxx" ).toFile();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        FileUtils.deleteQuietly( tempPath );
    }

    @Test
    public void testAPI() {
        final SourceRegistry sourceRegistry = new InMemorySourceRegistry();
        final BuildRegistry buildRegistry = new InMemoryBuildRegistry();

        final Stage<Input, SourceConfig> sourceConfig = config( "Git Source", ( s ) -> new MyGitConfig() );
        final Stage<SourceConfig, ProjectConfig> projectConfig = config( "Maven Project", ( s ) -> new MavenProjectConfigImpl() );
        final Stage<ProjectConfig, BuildConfig> buildConfig = config( "Maven Build Config", ( s ) -> new MavenBuildConfigImpl() );
        final Stage<BuildConfig, BinaryConfig> buildExec = config( "Maven Build", ( s ) -> new MavenBuildExecConfigImpl() );
        final Pipeline pipe = PipelineFactory
                .startFrom( sourceConfig )
                .andThen( projectConfig )
                .andThen( buildConfig )
                .andThen( buildExec ).buildAs( "my pipe" );

        final PipelineExecutor executor = new PipelineExecutor( asList( new GitConfigExecutor( sourceRegistry ), new MavenProjectConfigExecutor( sourceRegistry ), new MavenBuildConfigExecutor(), new MavenBuildExecConfigExecutor( buildRegistry ) ) );
        executor.execute( new Input() {{
            put( "repo-name", "drools-workshop" );
            put( "branch", "master" );
            put( "out-dir", tempPath.getAbsolutePath() );
            put( "origin", "https://github.com/salaboy/drools-workshop" );
            put( "project-dir", "drools-webapp-example" );
        }}, pipe, ( Binary b ) -> System.out.println( b.getName() ) );
    }

    static class MyGitConfig implements GitConfig,
                                        ContextAware {

        private Map<String, ?> context;

        @Override
        public void setContext( final Map<String, ?> context ) {
            this.context = context;
        }

        @Override
        public String getRepoName() {
            return ( (Map) context.get( "input" ) ).get( "repo-name" ).toString();
        }

        @Override
        public String getBranch() {
            return "master";
        }

    }

}
