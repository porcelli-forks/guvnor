package org.guvnor.ala.pipeline;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.guvnor.ala.config.Config;

/**
 * TODO: update me
 */
public final class StageUtil {

    private StageUtil() {

    }

    public static <INPUT extends Config, OUTPUT extends Config> Stage<INPUT, OUTPUT> config( final String name,
                                                                                             final Function<INPUT, OUTPUT> f ) {
        return new Stage<INPUT, OUTPUT>() {

            @Override
            public void execute( final INPUT input,
                                 final Consumer<OUTPUT> callback ) {
                callback.accept( f.apply( input ) );
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    public static <OUTPUT extends Config> Stage<?, OUTPUT> config( final String name,
                                                                   final Supplier<OUTPUT> s ) {
        return config( name, ignore -> s.get() );
    }

    public static <T extends Config> Stage<T, T> identity() {
        return config( "Identity", Function.identity() );
    }

}
