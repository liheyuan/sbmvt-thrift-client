/**
 * @(#)EasyThriftClientBuilder.java, Aug 01, 2017.
 * <p>
 * Copyright 2017 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.sbmvt.thrift.client.builder;

import com.coder4.sbmvt.thrift.client.EasyThriftClient;
import org.apache.thrift.TServiceClient;

/**
 * @author coder4
 */
public class EasyThriftClientBuilder<TCLIENT extends TServiceClient> {

    private final EasyThriftClient<TCLIENT> client = new EasyThriftClient<>();

    protected EasyThriftClient<TCLIENT> build() {
        client.init();
        return client;
    }

    protected EasyThriftClientBuilder<TCLIENT> setHost(String host) {
        client.setThriftServerHost(host);
        return this;
    }

    protected EasyThriftClientBuilder<TCLIENT> setPort(int port) {
        client.setThriftServerPort(port);
        return this;
    }

    protected EasyThriftClientBuilder<TCLIENT> setThriftClass(Class<?> thriftClass) {
        client.setThriftClass(thriftClass);
        return this;
    }
}