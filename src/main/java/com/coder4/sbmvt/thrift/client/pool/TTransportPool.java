/**
 * @(#)TTransportPool.java, Aug 10, 2017.
 * <p>
 * Copyright 2017 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.sbmvt.thrift.client.pool;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.thrift.transport.TTransport;

/**
 * @author coder4
 */
public class TTransportPool extends GenericKeyedObjectPool<String, TTransport> {

    private static int MAX_CONN = 128;
    private static int MIN_IDLE_CONN = 8;
    private static int MAX_IDLE_CONN = 32;

    public TTransportPool(TTransportPoolFactory factory) {
        super(factory);

        setTimeBetweenEvictionRunsMillis(45 * 1000);
        setNumTestsPerEvictionRun(5);
        setMaxWaitMillis(30 * 1000);

        setMaxTotal(MAX_CONN);
        setMaxTotalPerKey(MAX_CONN);
        setMinIdlePerKey(MIN_IDLE_CONN);
        setMaxTotalPerKey(MAX_IDLE_CONN);

        setTestOnCreate(true);
        setTestOnBorrow(true);
        setTestWhileIdle(true);
    }

    public void returnBrokenObject(String url, TTransport transport) {
        try {
            invalidateObject(url, transport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}