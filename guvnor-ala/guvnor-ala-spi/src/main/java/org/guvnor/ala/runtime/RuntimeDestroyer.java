package org.guvnor.ala.runtime;

public interface RuntimeDestroyer {

    boolean supports( final RuntimeId runtimeId );

    void destroy( final RuntimeId runtimeId );

}
