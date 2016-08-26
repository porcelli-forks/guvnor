
package org.guvnor.ala.docker.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

import org.guvnor.ala.pipeline.ContextAware;
import org.guvnor.ala.runtime.providers.ProviderId;

public class ContextAwareDockerRuntimeExecConfig implements
        ContextAware,
        DockerRuntimeExecConfig {

    @JsonIgnore
    private Map<String, ?> context;
    private ProviderId providerId;
    private String image;
    private String port;
    private boolean pull;

    @Override
    @JsonIgnore
    public void setContext( final Map<String, ?> context ) {
        this.context = context;
        final DockerRuntimeConfig dockerRuntimeConfiguration = ( DockerRuntimeConfig ) context.get( "docker-runtime-config" );
        this.providerId = dockerRuntimeConfiguration.getProviderId();
        this.image = dockerRuntimeConfiguration.getImage();
        this.port = dockerRuntimeConfiguration.getPort();
        this.pull = dockerRuntimeConfiguration.isPull();
    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

    @Override
    public String getImage() {
        return image;
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public boolean isPull() {
        return pull;
    }

    public void setProviderId( ProviderId providerId ) {
        this.providerId = providerId;
    }

    public void setImage( String image ) {
        this.image = image;
    }

    public void setPort( String port ) {
        this.port = port;
    }

    public void setPull( boolean pull ) {
        this.pull = pull;
    }

    @Override
    public String toString() {
        return "ContextAwareDockerRuntimeExecConfig{" + "providerId=" + providerId + ", image=" + image + ", port=" + port + ", pull=" + pull + '}';
    }

}
