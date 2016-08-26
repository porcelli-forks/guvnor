/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.docker.config.impl;

import org.guvnor.ala.docker.config.DockerBuildConfig;

public class DockerBuildConfigImpl implements DockerBuildConfig {

    private String username;
    private String password;
    private Boolean push;

    @Override
    public boolean push() {
        return ( push != null ) ? push : DockerBuildConfig.super.push();

    }

    @Override
    public String getPassword() {
        return ( password != null ) ? password : DockerBuildConfig.super.getPassword();
    }

    @Override
    public String getUsername() {
        return ( username != null ) ? username : DockerBuildConfig.super.getUsername();
    }

    @Override
    public String toString() {
        return "DockerBuildConfigImpl{" + "username=" + username + ", password=" + password + ", push=" + push + '}';
    }
    
    

}
