/**
 * @(#)RPCFUNC.java, Aug 01, 2017.
 * <p>
 * Copyright 2017 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.sbmvt.thrift.client.func;

import org.apache.thrift.TServiceClient;

/**
 * @author coder4
 */
public interface ThriftCallFunc<TCLIENT extends TServiceClient, TRET> {

    TRET call(TCLIENT tcall) throws Exception;

}