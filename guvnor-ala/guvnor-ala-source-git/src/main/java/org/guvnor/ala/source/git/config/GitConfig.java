package org.guvnor.ala.source.git.config;

import org.guvnor.ala.config.SourceConfig;

public interface GitConfig extends SourceConfig {

    default String getRepoName() {
        return "${input.repo-name}";
    }

    default String getOrigin() {
        return "${input.origin}";
    }

    default String getBranch() {
        return "${input.branch}";
    }

    default String getOutPath() {
        return "${input.out-dir}";
    }
}
