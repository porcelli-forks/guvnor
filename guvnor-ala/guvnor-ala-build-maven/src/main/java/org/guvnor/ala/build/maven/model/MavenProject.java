package org.guvnor.ala.build.maven.model;

import java.util.Collection;

import org.guvnor.ala.build.Project;

public interface MavenProject extends Project {

    Collection<PlugIn> getBuildPlugins();

}
