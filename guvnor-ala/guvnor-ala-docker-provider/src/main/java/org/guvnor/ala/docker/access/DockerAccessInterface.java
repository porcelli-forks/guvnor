
package org.guvnor.ala.docker.access;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import org.uberfire.commons.lifecycle.Disposable;
import org.guvnor.ala.runtime.providers.ProviderId;

/**
 * TODO: update me
 */
public interface DockerAccessInterface extends Disposable {

    DockerClient getDockerClient( final ProviderId providerId ) throws DockerException, InterruptedException;

}
