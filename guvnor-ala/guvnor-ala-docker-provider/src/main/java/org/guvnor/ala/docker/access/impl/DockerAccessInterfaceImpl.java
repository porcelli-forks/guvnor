
package org.guvnor.ala.docker.access.impl;

import java.util.HashMap;
import java.util.Map;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.Info;
import org.uberfire.commons.lifecycle.Disposable;
import org.guvnor.ala.docker.access.DockerAccessInterface;
import org.guvnor.ala.runtime.providers.ProviderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerAccessInterfaceImpl
        implements DockerAccessInterface,
        Disposable {

    protected static final Logger LOG = LoggerFactory.getLogger( DockerAccessInterfaceImpl.class );
    final Map<String, DockerClient> clientMap = new HashMap<>();

    @Override
    public DockerClient getDockerClient( final ProviderId providerId ) throws DockerException, InterruptedException {
        if ( !clientMap.containsKey( providerId.getId() ) ) {
            clientMap.put( providerId.getId(), buildClient( providerId ) );
        }
        return clientMap.get( providerId.getId() );
    }

    private DockerClient buildClient( final ProviderId providerId ) throws DockerException, InterruptedException  {
        if ( providerId.getId().equals( "local" ) ) {
            try{
                if ( System.getProperty( "os.name" ).toLowerCase().contains( "mac" ) ) {
                    DefaultDockerClient dockerClient = DefaultDockerClient.builder().uri( DefaultDockerClient.DEFAULT_UNIX_ENDPOINT ).build();
                    // This test the docker client connection to see if the client was built properly
                    Info info = dockerClient.info();
                    LOG.info("Connected to Docker Client Info: " + info );
                    return dockerClient;
                }
            }catch(DockerException | InterruptedException ex){
                try {
                    DefaultDockerClient dockerClient = DefaultDockerClient.fromEnv().build();
                    Info info = dockerClient.info();
                    LOG.info("Connected to Docker Client Info: " + info );
                    return dockerClient;

                } catch ( DockerCertificateException e ) {
                    throw new RuntimeException( e );
                }
            }
        }

        return null;
    }

    @Override
    public void dispose() {
        clientMap.values().forEach( DockerClient::close );
    }
}
