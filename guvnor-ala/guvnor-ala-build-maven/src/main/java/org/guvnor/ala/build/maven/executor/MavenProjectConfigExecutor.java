package org.guvnor.ala.build.maven.executor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Inject;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.guvnor.ala.build.maven.config.MavenProjectConfig;
import org.guvnor.ala.build.maven.model.impl.MavenProjectImpl;
import org.guvnor.ala.build.maven.model.PlugIn;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.pipeline.BiFunctionConfigExecutor;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.source.Source;
import org.kie.scanner.embedder.MavenProjectLoader;
import org.uberfire.java.nio.file.Files;

public class MavenProjectConfigExecutor implements BiFunctionConfigExecutor<Source, MavenProjectConfig, ProjectConfig> {

    private final SourceRegistry sourceRegistry;

    @Inject
    public MavenProjectConfigExecutor( final SourceRegistry sourceRegistry ) {
        this.sourceRegistry = sourceRegistry;
    }

    @Override
    public Optional<ProjectConfig> apply( final Source source,
                                          final MavenProjectConfig mavenProjectConfig ) {
        final InputStream pomStream = Files.newInputStream( source.getPath().resolve( mavenProjectConfig.getProjectDir() ).resolve( "pom.xml" ) );
        final MavenProject project = MavenProjectLoader.parseMavenPom( pomStream );

        final Collection<PlugIn> buildPlugins = extractPlugins( project );

        final String expectedBinary = project.getArtifact().getArtifactId() + "-" + project.getArtifact().getVersion() + "." + project.getArtifact().getType();
        final org.guvnor.ala.build.maven.model.MavenProject mavenProject = new MavenProjectImpl( project.getId(),
                                                                                                 project.getArtifact().getType(),
                                                                                                 project.getName(),
                                                                                                 expectedBinary,
                                                                                                 source.getPath(),
                                                                                                 source.getPath().resolve( mavenProjectConfig.getProjectDir() ),
                                                                                                 source.getPath().resolve( "target" ).resolve( expectedBinary ).toAbsolutePath(),
                                                                                                 buildPlugins );

        sourceRegistry.registerProject( source, mavenProject );

        return Optional.of( mavenProject );
    }

    private Collection<PlugIn> extractPlugins( final MavenProject project ) {
        final Collection<PlugIn> result = new ArrayList<>( project.getBuildPlugins().size() );
        for ( org.apache.maven.model.Plugin plugin : project.getBuildPlugins() ) {
            final Map<String, Object> config = extractConfig( plugin.getConfiguration() );
            result.add( new PlugIn() {
                @Override
                public String getId() {
                    return plugin.getKey();
                }

                @Override
                public Map<String, ?> getConfiguration() {
                    return config;
                }
            } );
        }
        return result;
    }

    private Map<String, Object> extractConfig( final Object configuration ) {
        if ( configuration instanceof Xpp3Dom ) {
            final Map<String, Object> result = new HashMap<>();
            extractConfig( result, (Xpp3Dom) configuration );
            if ( result.containsKey( "configuration" ) ) {
                try {
                    return (Map<String, Object>) result.get( "configuration" );
                } catch ( Exception ex ) {
                    return Collections.emptyMap();
                }
            }
        }
        return Collections.emptyMap();
    }

    private void extractConfig( final Map<String, Object> content,
                                final Xpp3Dom xmlData ) {
        if ( xmlData.getChildCount() > 0 ) {
            final Map<String, Object> config = new HashMap<>( xmlData.getChildCount() );
            for ( final Xpp3Dom child : xmlData.getChildren() ) {
                extractConfig( config, child );
            }
            content.put( xmlData.getName(), config );
        } else {
            content.put( xmlData.getName(), xmlData.getValue() );
        }
    }

    @Override
    public Class<? extends Config> executeFor() {
        return MavenProjectConfig.class;
    }

    @Override
    public String outputId() {
        return "project";
    }

    @Override
    public String inputId() {
        return "maven-config";
    }
}
