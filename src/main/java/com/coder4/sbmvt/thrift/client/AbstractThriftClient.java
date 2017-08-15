/**
 * @(#)AbstractThriftClient.java, Aug 01, 2017.
 * <p>
 * Copyright 2017 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.sbmvt.thrift.client;

import com.coder4.sbmvt.thrift.client.func.ThriftCallFunc;
import com.coder4.sbmvt.thrift.client.func.ThriftExecFunc;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author coder4
 */
public abstract class AbstractThriftClient<TCLIENT extends TServiceClient> implements ThriftClient<TCLIENT> {

    protected static final int THRIFT_CLIENT_DEFAULT_TIMEOUT = 5000;

    protected static final int THRIFT_CLIENT_DEFAULT_MAX_FRAME_SIZE = 1024 * 1024 * 16;

    private Class<?> thriftClass;

    private static final TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();

    private TServiceClientFactory<TCLIENT> clientFactory;

    // For async call
    private ExecutorService threadPool;

    public void init() {
        try {
            clientFactory = getThriftClientFactoryClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException();
        }

        if (!check()) {
            throw new RuntimeException("Client config failed check!");
        }

        threadPool = new ThreadPoolExecutor(
                10, 100, 0,
                TimeUnit.MICROSECONDS, new LinkedBlockingDeque<>());
    }

    protected boolean check() {
        if (thriftClass == null) {
            return false;
        }
        return true;
    }

    @Override
    public <TRET> Future<TRET> asyncCall(ThriftCallFunc<TCLIENT, TRET> tcall) {
        return threadPool.submit(() -> this.call(tcall));
    }

    @Override
    public <TRET> Future<?> asyncExec(ThriftExecFunc<TCLIENT> texec) {
        return threadPool.submit(() -> this.exec(texec));
    }

    protected TCLIENT createClient(TTransport transport) throws Exception {
        // Step 1: get TProtocol
        TProtocol protocol = protocolFactory.getProtocol(transport);

        // Step 2: get client
        return clientFactory.getClient(protocol);
    }

    private Class<TServiceClientFactory<TCLIENT>> getThriftClientFactoryClass() {
        Class<TCLIENT> clientClazz = getThriftClientClass();
        if (clientClazz == null) {
            return null;
        }
        for (Class<?> clazz : clientClazz.getDeclaredClasses()) {
            if (TServiceClientFactory.class.isAssignableFrom(clazz)) {
                return (Class<TServiceClientFactory<TCLIENT>>) clazz;
            }
        }
        return null;
    }

    private Class<TCLIENT> getThriftClientClass() {
        for (Class<?> clazz : thriftClass.getDeclaredClasses()) {
            if (TServiceClient.class.isAssignableFrom(clazz)) {
                return (Class<TCLIENT>) clazz;
            }
        }
        return null;
    }

    public void setThriftClass(Class<?> thriftClass) {
        this.thriftClass = thriftClass;
    }
}