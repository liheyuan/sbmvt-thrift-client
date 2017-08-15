/**
 * @(#)ThriftClient.java, Aug 01, 2017.
 * <p>
 * Copyright 2017 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.sbmvt.thrift.client;

import com.coder4.sbmvt.thrift.client.func.ThriftCallFunc;
import com.coder4.sbmvt.thrift.client.func.ThriftExecFunc;
import org.apache.thrift.TServiceClient;

import java.util.concurrent.Future;

/**
 * @author coder4
 */
public interface ThriftClient<TCLIENT extends TServiceClient> {

    /**
     * sync call with return value
     * @param tcall thrift rpc client call
     * @param <TRET> return type
     * @return
     */
    <TRET> TRET call(ThriftCallFunc<TCLIENT, TRET> tcall);

    /**
     * sync call without return value
     * @param texec thrift rpc client
     */
    void exec(ThriftExecFunc<TCLIENT> texec);

    /**
     * async call with return value
     * @param tcall thrift rpc client call
     * @param <TRET>
     * @return
     */
    <TRET> Future<TRET> asyncCall(ThriftCallFunc<TCLIENT, TRET> tcall);


    /**
     * asnyc call without return value
     * @param texec thrift rpc client call
     */
    <TRET> Future<?> asyncExec(ThriftExecFunc<TCLIENT> texec);

}