
package org.guvnor.ala.docker.config.impl;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.guvnor.ala.build.maven.model.MavenProject;
import org.guvnor.ala.build.maven.model.PlugIn;
import org.guvnor.ala.config.CloneableConfig;
import org.guvnor.ala.docker.config.DockerProvisioningConfig;
import org.guvnor.ala.pipeline.ContextAware;
import org.guvnor.ala.runtime.providers.ProviderId;

public class ContextAwareDockerProvisioningConfig implements
                                                  ContextAware,
                                                  DockerProvisioningConfig,
                                                  CloneableConfig<DockerProvisioningConfig> {

    @JsonIgnore
    private Map<String, ?> context;
    private String imageName = "${input.image-name}";
    private String portNumber = "${input.port-number}";
    private ProviderId providerId;
    private String dockerPullValue = "${input.docker-pull}";

    public ContextAwareDockerProvisioningConfig() {
        this.imageName = DockerProvisioningConfig.super.getImageName();
        this.portNumber = DockerProvisioningConfig.super.getPortNumber();
        this.dockerPullValue = DockerProvisioningConfig.super.getDockerPullValue();
    }

    public ContextAwareDockerProvisioningConfig( final String imageName,
                                                 final String portNumber,
                                                 final ProviderId providerId,
                                                 final String dockerPullValue ) {
        this.imageName = imageName;
        this.portNumber = portNumber;
        this.providerId = providerId;
        this.dockerPullValue = dockerPullValue;
    }

    @Override
    @JsonIgnore
    public void setContext( final Map<String, ?> context ) {
        this.context = context;
        this.providerId = (ProviderId) this.context.get( "docker-provider" );
        try {
            final Object _project = context.get( "project" );
            if ( _project != null && _project instanceof MavenProject ) {
                final Collection<PlugIn> plugIns = ( (MavenProject) _project ).getBuildPlugins();
                for ( final PlugIn plugIn : plugIns ) {
                    if ( plugIn.getId().equals( "io.fabric8:docker-maven-plugin" ) ) {
                        final Map<String, Object> _config = (Map<String, Object>) plugIn.getConfiguration().get( "images" );
                        imageName = getValue( _config, "image" ).get( "name" ).toString();
                        portNumber = getValue( getValue( getValue( _config, "image" ), "build" ), "ports" ).get( "port" ).toString();
                        break;
                    }
                }
            }
        } catch ( final Exception ex ) {
        }
    }

    private Map<String, Object> getValue( final Map<String, Object> _config,
                                          final String key ) {
        return (Map<String, Object>) _config.get( key );
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public String getPortNumber() {
        return portNumber;
    }

    @Override
    public ProviderId getProviderId() {
        return providerId;
    }

    public void setImageName( String imageName ) {
        this.imageName = imageName;
    }

    public void setPortNumber( String portNumber ) {
        this.portNumber = portNumber;
    }

    @Override
    public String getDockerPullValue() {
        return dockerPullValue;
    }

    public void setProviderId( ProviderId providerId ) {
        this.providerId = providerId;
    }

    public void setDockerPullValue( String dockerPullValue ) {
        this.dockerPullValue = dockerPullValue;
    }

    @Override
    public String toString() {
        return "ContextAwareDockerProvisioningConfig{" + "imageName=" + imageName + ", portNumber=" + portNumber + ", providerId=" + providerId + ", dockerPullValue=" + dockerPullValue + '}';
    }

    @Override
    public DockerProvisioningConfig asNewClone( final DockerProvisioningConfig origin ) {
        return new ContextAwareDockerProvisioningConfig( origin.getImageName(),
                                                         origin.getPortNumber(),
                                                         origin.getProviderId(),
                                                         origin.getDockerPullValue() );
    }
}