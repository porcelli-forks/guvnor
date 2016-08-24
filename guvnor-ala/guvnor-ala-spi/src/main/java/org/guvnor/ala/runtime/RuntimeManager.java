package org.guvnor.ala.runtime;

public interface RuntimeManager {

    boolean supports( final RuntimeId runtimeId );

    void start( final RuntimeId runtimeId );
    
    void stop( final RuntimeId runtimeId );
    
    void restart( final RuntimeId runtimeId );
    
    void refresh( final RuntimeId runtimeId );
    
    void pause( final RuntimeId runtimeId );

}
