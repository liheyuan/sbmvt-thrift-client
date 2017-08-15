/**
 * @(#)ThriftUrlStr.java, Aug 11, 2017.
 * <p>
 * Copyright 2017 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.sbmvt.thrift.client.utils;

import java.util.Optional;

/**
 * @author coder4
 */
public class ThriftUrlStr {

    private String host;
    private int port;

    public ThriftUrlStr(String host, int port) {
        this.setHost(host);
        this.setPort(port);
    }

    public static Optional<ThriftUrlStr> parse(String url) {
        String tmp [] = url.split(":");
        if (tmp.length != 2) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(new ThriftUrlStr(tmp[0], Integer.parseInt(tmp[1])));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}