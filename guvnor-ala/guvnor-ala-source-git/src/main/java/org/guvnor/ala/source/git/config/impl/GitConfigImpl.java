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

package org.guvnor.ala.source.git.config.impl;

import org.guvnor.ala.source.git.config.GitConfig;

public class GitConfigImpl implements GitConfig {

    private String outPath;
    private String branch;
    private String origin;
    private String repoName;

    @Override
    public String getOutPath() {
        return ( outPath != null ) ? outPath : GitConfig.super.getOutPath();

    }

    @Override
    public String getBranch() {
        return ( branch != null ) ? branch : GitConfig.super.getBranch();
    }

    @Override
    public String getOrigin() {
        return ( origin != null ) ? origin : GitConfig.super.getOrigin();
    }

    @Override
    public String getRepoName() {
        return ( repoName != null ) ? repoName : GitConfig.super.getRepoName();
    }

    public void setOutPath( String outPath ) {
        this.outPath = outPath;
    }

    public void setBranch( String branch ) {
        this.branch = branch;
    }

    public void setOrigin( String origin ) {
        this.origin = origin;
    }

    public void setRepoName( String repoName ) {
        this.repoName = repoName;
    }

    @Override
    public String toString() {
        return "GitConfigImpl{" + "outPath=" + outPath + ", branch=" + branch + ", origin=" + origin + ", repoName=" + repoName + '}';
    }

}
