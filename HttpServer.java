//
// Decompiled by Jadx - 477ms
//
package com.netspace.library.servers;

import java.io.IOException;

public class HttpServer extends NanoHTTPD {
    public HttpServer() throws IOException {
        super(8011);
    }
}
