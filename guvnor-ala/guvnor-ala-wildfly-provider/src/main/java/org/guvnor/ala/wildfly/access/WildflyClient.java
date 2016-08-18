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

package org.guvnor.ala.wildfly.access;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jboss.dmr.ModelNode;

import static java.lang.System.*;
import java.util.logging.Level;
import static java.util.logging.Level.*;
import java.util.logging.Logger;
import static java.util.logging.Logger.*;
import static org.apache.http.entity.mime.HttpMultipartMode.*;
import static org.apache.http.entity.mime.MultipartEntityBuilder.create;
import static org.apache.http.impl.client.HttpClients.*;
import static org.apache.http.entity.ContentType.create;

/**
 * Based on: https://github.com/heiko-braun/http-upload
 */
@JsonIgnoreType
public class WildflyClient {

    
    private String providerName;

    public WildflyClient() {
    }

    public WildflyClient( String providerName ) {
        this.providerName = providerName;
    }
    
    
    /*
     * return the HTTP status from deploying the app or -1 if there is an exception which is logged.
    */
    public int deploy( String user,
                       String password,
                       String host,
                       int port,
                       String filePath ) {

        // the digest auth backend
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope( host, port ),
                new UsernamePasswordCredentials( user, password ) );

        CloseableHttpClient httpclient = custom()
                .setDefaultCredentialsProvider( credsProvider )
                .build();

        HttpPost post = new HttpPost( "http://" + host + ":" + port + "/management-upload" );

        post.addHeader( "X-Management-Client-Name", "HAL" );

        // the file to be uploaded
        File file = new File( filePath );
        FileBody fileBody = new FileBody( file );

        // the DMR operation
        ModelNode operation = new ModelNode();
        operation.get( "address" ).add( "deployment", file.getName() );
        operation.get( "operation" ).set( "add" );
        operation.get( "runtime-name" ).set( file.getName() );
        operation.get( "enabled" ).set( true );
        operation.get( "content" ).add().get( "input-stream-index" ).set( 0 );  // point to the multipart index used

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            operation.writeBase64( bout );
        } catch ( IOException ex ) {
            getLogger(WildflyClient.class.getName() ).log( SEVERE, null, ex );
        }

        // the multipart
        MultipartEntityBuilder builder = create();
        builder.setMode( BROWSER_COMPATIBLE );
        builder.addPart( "uploadFormElement", fileBody );
        builder.addPart( "operation", new ByteArrayBody( bout.toByteArray(), create( "application/dmr-encoded" ), "blob" ) );
        HttpEntity entity = builder.build();

        try {
            entity.writeTo(System.out);
        } catch ( IOException ex ) {
            Logger.getLogger( WildflyClient.class.getName() ).log( Level.SEVERE, null, ex );
        }
        post.setEntity( entity );

        try {
            HttpResponse response = httpclient.execute( post );

            out.println( ">>> Deploying Response Entity: " + response.getEntity() );
            out.println( ">>> Deploying Response Satus: " + response.getStatusLine().getStatusCode() );
            return response.getStatusLine().getStatusCode();
        } catch ( IOException ex ) {
            ex.printStackTrace();
            getLogger(WildflyClient.class.getName() ).log( SEVERE, null, ex );
        }
        return -1;
    }
    
    public int undeploy(){
        return 0;
    }
    
    public void close(){
        
        
    }

    public void start( String id ) {
        
    }

    public void stop( String id, int i ) {
        
    }

    public void restart( String id ) {
        
    }

    public WildflyAppState getAppState( String id ) {
         // the digest auth backend
//        CredentialsProvider credsProvider = new BasicCredentialsProvider();
//        credsProvider.setCredentials(
//                new AuthScope( host, port ),
//                new UsernamePasswordCredentials( user, password ) );
//
//        CloseableHttpClient httpclient = custom()
//                .setDefaultCredentialsProvider( credsProvider )
//                .build();
//        
//        // the DMR operation
//        ModelNode operation = new ModelNode();
//        
//        operation.get( "operation" ).set( "add" );
//        
//        operation.get( "enabled" ).set( true );
//        operation.get( "content" ).add().get( "input-stream-index" ).set( 0 );  // point to the multipart index used
//
//        ByteArrayOutputStream bout = new ByteArrayOutputStream();
//        try {
//            operation.writeBase64( bout );
//        } catch ( IOException ex ) {
//            getLogger(WildflyClient.class.getName() ).log( SEVERE, null, ex );
//        }
//
//        // the multipart
//        MultipartEntityBuilder builder = create();
//        builder.setMode( BROWSER_COMPATIBLE );
//        
//        builder.addPart( "operation", new ByteArrayBody( bout.toByteArray(), create( "application/dmr-encoded" ), "blob" ) );
//        HttpEntity entity = builder.build();
//        HttpPost post = new HttpPost( "http://" + host + ":" + port + "/" );
//        try {
//            entity.writeTo(System.out);
//        } catch ( IOException ex ) {
//            Logger.getLogger( WildflyClient.class.getName() ).log( Level.SEVERE, null, ex );
//        }
//        post.setEntity( entity );
//
//        try {
//            HttpResponse response = httpclient.execute( post );
//
//            out.println( ">>> Deploying Response Entity: " + response.getEntity() );
//            out.println( ">>> Deploying Response Satus: " + response.getStatusLine().getStatusCode() );
//           
//        } catch ( IOException ex ) {
//            ex.printStackTrace();
//            getLogger(WildflyClient.class.getName() ).log( SEVERE, null, ex );
//        }
        
        return new WildflyAppState();
    }
}
