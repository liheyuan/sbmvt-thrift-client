/**
 * @(#)EurekaThriftClientBuilder.java, Aug 10, 2017.
 * <p>
 * Copyright 2017 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.sbmvt.thrift.client.builder;

import com.coder4.sbmvt.thrift.client.EurekaThriftClient;
import org.apache.thrift.TServiceClient;

/**
 * @author coder4
 */
public class EurekaThriftClientBuilder<TCLIENT extends TServiceClient> {

    private final EurekaThriftClient<TCLIENT> client = new EurekaThriftClient<>();

    protected EurekaThriftClient<TCLIENT> build() {
        client.init();
        return client;
    }

    protected EurekaThriftClientBuilder<TCLIENT> setThriftServiceName(String serviceName) {
        client.setThriftServiceName(serviceName);
        return this;
    }

    protected EurekaThriftClientBuilder<TCLIENT> setThriftClass(Class<?> thriftClass) {
        client.setThriftClass(thriftClass);
        return this;
    }
}