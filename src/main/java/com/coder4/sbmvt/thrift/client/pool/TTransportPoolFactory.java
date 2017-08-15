/**
 * @(#)TTransportPoolFacttory.java, Aug 11, 2017.
 * <p>
 * Copyright 2017 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.sbmvt.thrift.client.pool;

import com.coder4.sbmvt.thrift.client.utils.ThriftUrlStr;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.Optional;

/**
 * @author coder4
 */
public class TTransportPoolFactory extends BaseKeyedPooledObjectFactory<String, TTransport> {

    protected static final int THRIFT_CLIENT_DEFAULT_TIMEOUT = 5000;

    protected static final int THRIFT_CLIENT_DEFAULT_MAX_FRAME_SIZE = 1024 * 1024 * 16;

    @Override
    public TTransport create(String key) throws Exception {
        Optional<ThriftUrlStr> urlOp = ThriftUrlStr.parse(key);
        if (urlOp.isPresent()) {
            ThriftUrlStr url = urlOp.get();
            TSocket socket = new TSocket(url.getHost(), url.getPort(), THRIFT_CLIENT_DEFAULT_TIMEOUT);

            TTransport transport = new TFramedTransport(
                    socket, THRIFT_CLIENT_DEFAULT_MAX_FRAME_SIZE);

            transport.open();

            return transport;
        } else {
            return null;
        }
    }

    @Override
    public PooledObject<TTransport> wrap(TTransport transport) {
        return new DefaultPooledObject<>(transport);
    }

    @Override
    public void destroyObject(String key, PooledObject<TTransport> obj) throws Exception {
        obj.getObject().close();
    }

    @Override
    public boolean validateObject(String key, PooledObject<TTransport> obj) {
        return obj.getObject().isOpen();
    }

}