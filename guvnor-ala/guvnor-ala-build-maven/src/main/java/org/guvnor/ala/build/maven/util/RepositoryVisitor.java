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

package org.guvnor.ala.build.maven.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

public class RepositoryVisitor {

    private static final Random RND = new Random();
    private File root;

    public RepositoryVisitor( final Path projectPath,
                              final String projectName ) {
        this( projectPath,
              System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "guvnor" + RND.nextLong() + File.separatorChar + projectName,
              false );
    }

    public RepositoryVisitor( final Path projectPath,
                              final String _projectRoot,
                              final boolean recreateTempDir ) {
        this.root = makeTempRootDirectory( _projectRoot, recreateTempDir );

        try {
            visitPaths( root, Files.newDirectoryStream( projectPath ) );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public File getRoot() {
        return root;
    }

    private void visitPaths( final File parent,
                             final DirectoryStream<Path> directoryStream ) throws IOException {
        for ( final org.uberfire.java.nio.file.Path path : directoryStream ) {
            if ( Files.isDirectory( path ) ) {
                final File newParent = makeTempDirectory( parent, path.getFileName().toString() );
                visitPaths( newParent, Files.newDirectoryStream( path ) );
            } else {
                makeTempFile( parent, path );
            }
        }
    }

    private File makeTempDirectory( final File parent,
                                    final String filePath ) {
        File tempDirectory = new File( parent, filePath );
        if ( tempDirectory.exists() ) {
            return tempDirectory;
        }
        if ( !tempDirectory.isFile() ) {
            tempDirectory.mkdir();
        }
        return tempDirectory;
    }

    private File makeTempRootDirectory( final String tempRoot,
                                        final boolean recreateTempDir ) {
        final File tempRootDir = new File( tempRoot );
        if ( tempRootDir.exists() ) {
            if ( recreateTempDir ) {
                try {
                    FileUtils.deleteDirectory( tempRootDir );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } else {
            tempRootDir.mkdirs();
        }
        return tempRootDir;
    }

    private void makeTempFile( final File parent,
                               final Path path ) throws IOException {

        final int BUFFER = 2048;
        byte data[] = new byte[ BUFFER ];

        FileOutputStream output = null;
        try ( BufferedInputStream origin = new BufferedInputStream( Files.newInputStream( path ), BUFFER ) ) {
            final File tempFile = new File( parent, path.getFileName().toString() );
            if ( !tempFile.exists() ) {
                tempFile.createNewFile();
            }
            output = new FileOutputStream( tempFile );
            int count;
            while ( ( count = origin.read( data, 0, BUFFER ) ) != -1 ) {
                output.write( data, 0, count );
            }
        } finally {
            if ( output != null ) {
                output.close();
            }
        }
    }
}
