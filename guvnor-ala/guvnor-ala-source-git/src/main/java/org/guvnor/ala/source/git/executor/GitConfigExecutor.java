package org.guvnor.ala.source.git.executor;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import javax.inject.Inject;

import org.uberfire.java.nio.file.FileSystems;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.SourceRegistry;
import org.guvnor.ala.source.Source;
import org.guvnor.ala.source.git.GitRepository;
import org.guvnor.ala.source.git.UFLocal;
import org.guvnor.ala.source.git.config.GitConfig;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class GitConfigExecutor implements FunctionConfigExecutor<GitConfig, Source> {

    private final SourceRegistry sourceRegistry;

    @Inject
    public GitConfigExecutor( final SourceRegistry sourceRegistry ) {
        this.sourceRegistry = sourceRegistry;
    }

    @Override
    public Optional<Source> apply( final GitConfig gitConfig ) {
        checkNotEmpty( "repo-name parameter is mandatory", gitConfig.getRepoName() );

        final URI uri = URI.create( "git://" + gitConfig.getRepoName() );
        FileSystems.newFileSystem( uri, new HashMap<String, Object>() {{
            if ( gitConfig.getOrigin() != null && !gitConfig.getOrigin().isEmpty() ) {
                put( "origin", gitConfig.getOrigin() );
            } else {
                put( "init", Boolean.TRUE );
            }
            if ( gitConfig.getOutPath() != null && !gitConfig.getOutPath().isEmpty() ) {
                put( "out-dir", gitConfig.getOutPath() );
            }
        }} );

        final GitRepository gitRepository = (GitRepository) new UFLocal().getRepository( gitConfig.getRepoName(), Collections.emptyMap() );
        final Optional<Source> source = Optional.ofNullable( gitRepository.getSource( gitConfig.getBranch() != null && !gitConfig.getBranch().isEmpty() ? gitConfig.getBranch() : "master" ) );
        if ( source.isPresent() ) {
            sourceRegistry.registerSource( gitRepository, source.get() );
        }
        return source;
    }

    @Override
    public Class<? extends Config> executeFor() {
        return GitConfig.class;
    }

    @Override
    public String outputId() {
        return "source";
    }

    @Override
    public String inputId() {
        return "git-config";
    }
}
