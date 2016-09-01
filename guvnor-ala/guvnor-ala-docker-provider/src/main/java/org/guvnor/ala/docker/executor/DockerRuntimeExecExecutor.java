
package org.guvnor.ala.docker.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import org.guvnor.ala.config.Config;
import org.guvnor.ala.config.RuntimeConfig;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.docker.config.DockerRuntimeExecConfig;
import org.guvnor.ala.docker.model.DockerProvider;
import org.guvnor.ala.docker.model.DockerRuntime;
import org.guvnor.ala.exceptions.ProvisioningException;
import org.guvnor.ala.pipeline.FunctionConfigExecutor;
import org.guvnor.ala.registry.RuntimeRegistry;
import org.guvnor.ala.runtime.RuntimeBuilder;
import org.guvnor.ala.runtime.RuntimeDestroyer;
import org.guvnor.ala.runtime.RuntimeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.guvnor.ala.docker.config.DockerRuntimeConfig;

public class DockerRuntimeExecExecutor<T extends DockerRuntimeConfig> implements RuntimeBuilder<T, DockerRuntime>,
        RuntimeDestroyer,
        FunctionConfigExecutor<T, DockerRuntime> {

    private final RuntimeRegistry runtimeRegistry;
    private final DockerAccessInterface docker;
    protected static final Logger LOG = LoggerFactory.getLogger( DockerRuntimeExecExecutor.class );

    @Inject
    public DockerRuntimeExecExecutor( final RuntimeRegistry runtimeRegistry,
            final DockerAccessInterface docker ) {
        this.runtimeRegistry = runtimeRegistry;
        this.docker = docker;
    }

    @Override
    public Optional<DockerRuntime> apply( final DockerRuntimeConfig config ) {
        final Optional<DockerRuntime> runtime = create( config );
        if ( runtime.isPresent() ) {
            runtimeRegistry.registerRuntime( runtime.get() );
        }
        return runtime;
    }

    private Optional<DockerRuntime> create( final DockerRuntimeConfig runtimeConfig ) throws ProvisioningException {
        if ( runtimeConfig.isPull() ) {
            try {
                LOG.info( "Pulling Docker Image: " + runtimeConfig.getImage() );
                docker.getDockerClient( runtimeConfig.getProviderId() ).pull( runtimeConfig.getImage() );
            } catch ( DockerException | InterruptedException ex ) {
                LOG.error( ex.getMessage(), ex );
                throw new ProvisioningException( "Error Pulling Docker Image: " + runtimeConfig.getImage() + "with error: " + ex.getMessage() );
            }
        }

        final String[] ports = { runtimeConfig.getPort() };
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();

        final Optional<DockerProvider> _dockerProvider = runtimeRegistry.getProvider( runtimeConfig.getProviderId(), DockerProvider.class );

        if ( !_dockerProvider.isPresent() ) {
            return Optional.empty();
        }
        final DockerProvider dockerProvider = _dockerProvider.get();
        final List<PortBinding> randomPort = new ArrayList<>();
        final PortBinding randomPortBinding = PortBinding.randomPort( dockerProvider.getHostId() );

        randomPort.add( randomPortBinding );
        portBindings.put( runtimeConfig.getPort(), randomPort );

        final HostConfig hostConfig = HostConfig.builder().portBindings( portBindings ).build();

        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig( hostConfig )
                .image( runtimeConfig.getImage() )
                .exposedPorts( ports )
                .build();

        final ContainerCreation creation;
        try {
            creation = docker.getDockerClient( runtimeConfig.getProviderId() ).createContainer( containerConfig );
        } catch ( DockerException | InterruptedException ex ) {
            LOG.error( ex.getMessage(), ex );
            throw new ProvisioningException( "Error Creating Docker Container with image: " + runtimeConfig.getImage() + "with error: " + ex.getMessage() );
        }

        final String id = creation.id();
        String shortId = id.substring( 0, 12 );

        return Optional.of( new DockerRuntime( shortId, runtimeConfig, dockerProvider ) );
    }

    @Override
    public Class<? extends Config> executeFor() {
        return DockerRuntimeExecConfig.class;
    }

    @Override
    public String outputId() {
        return "docker-runtime";
    }

    @Override
    public boolean supports( final RuntimeConfig config ) {
        return config instanceof DockerRuntimeConfig;
    }

    @Override
    public boolean supports( final RuntimeId runtimeId ) {
        return runtimeId instanceof DockerRuntime
                || runtimeRegistry.getRuntimeById( runtimeId.getId() ) instanceof DockerRuntime;
    }

    @Override
    public void destroy( final RuntimeId runtimeId ) {
        try {
            LOG.info( "Killing Container: " + runtimeId.getId() );
            docker.getDockerClient( runtimeId.getProviderId() ).killContainer( runtimeId.getId() );
            LOG.info( "Removing Container: " + runtimeId.getId() );
            docker.getDockerClient( runtimeId.getProviderId() ).removeContainer( runtimeId.getId() );
            runtimeRegistry.unregisterRuntime( runtimeId );
        } catch ( DockerException | InterruptedException ex ) {
            LOG.error( ex.getMessage(), ex );
            try {
                // Trying to remove the container if it cannot be killed
                LOG.info( "Attempting to Remove Container without Killing: " + runtimeId.getId() );
                docker.getDockerClient( runtimeId.getProviderId() ).removeContainer( runtimeId.getId() );
                runtimeRegistry.unregisterRuntime( runtimeId );
            } catch ( DockerException | InterruptedException ex2 ) {
                LOG.error( ex.getMessage(), ex2 );
                throw new ProvisioningException( "Error destroying Docker Runtime: " + ex.getMessage() );
            }

        }
    }
}
