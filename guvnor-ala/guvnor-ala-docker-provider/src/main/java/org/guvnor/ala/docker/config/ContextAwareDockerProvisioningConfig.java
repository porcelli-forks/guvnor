package org.guvnor.ala.docker.config;

import java.util.Collection;
import java.util.Map;

import org.guvnor.ala.build.maven.model.MavenProject;
import org.guvnor.ala.build.maven.model.PlugIn;
import org.guvnor.ala.pipeline.ContextAware;
import org.guvnor.ala.runtime.providers.ProviderId;

public class ContextAwareDockerProvisioningConfig implements
                                                  ContextAware,
                                                  DockerProvisioningConfig {

    private Map<String, ?> context;
    private String imageName = "${input.image-name}";
    private String portNumber = "${input.port-number}";

    @Override
    public void setContext( final Map<String, ?> context ) {
        this.context = context;

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
        return (ProviderId) context.get( "docker-provider" );
    }
}
