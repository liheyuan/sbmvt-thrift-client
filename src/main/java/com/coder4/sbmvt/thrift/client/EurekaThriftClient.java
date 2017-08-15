/**
 * @(#)EurekaThriftClient.java, Aug 10, 2017.
 * <p>
 * Copyright 2017 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.coder4.sbmvt.thrift.client;

import com.coder4.sbmvt.thrift.client.pool.TTransportPool;
import com.coder4.sbmvt.thrift.client.pool.TTransportPoolFactory;
import com.coder4.sbmvt.thrift.client.func.ThriftCallFunc;
import com.coder4.sbmvt.thrift.client.func.ThriftExecFunc;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.EurekaEvent;
import com.netflix.discovery.EurekaEventListener;
import com.netflix.discovery.shared.Application;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.transport.TTransport;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author coder4
 */
public class EurekaThriftClient<TCLIENT extends TServiceClient>
        extends AbstractThriftClient<TCLIENT> {

    private ApplicationInfoManager applicationInfoManager;

    private EurekaClient eurekaClient;

    private String thriftServiceName;

    private static int cnt = 0;

    private TTransportPool connPool;

    @Override
    public void init() {
        super.init();
        // init eurekaClient by hand
        initializeApplicationInfoManager();
        initializeEurekaClient();
        eurekaClient.registerEventListener(new EurekaEventListener() {
            @Override
            public void onEvent(EurekaEvent event) {
                System.out.println("EurekaEvent Cache Updated");
                System.out.println(event);
            }
        });
        // init pool
        connPool = new TTransportPool(new TTransportPoolFactory());
    }

    @Override
    public <TRET> TRET call(ThriftCallFunc<TCLIENT, TRET> tcall) {

        // Step 1: get TTransport
        TTransport tpt = null;
        String key = getConnBorrowKey();
        try {
            tpt = connPool.borrowObject(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Step 2: get client & call
        try {
            TCLIENT tcli = createClient(tpt);
            TRET ret = tcall.call(tcli);
            returnTransport(key, tpt);
            return ret;
        } catch (Exception e) {
            returnBrokenTransport(key, tpt);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exec(ThriftExecFunc<TCLIENT> texec) {
        // Step 1: get TTransport
        TTransport tpt = null;
        String key = getConnBorrowKey();
        try {

            // borrow transport
            tpt = connPool.borrowObject(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Step 2: get client & exec
        try {
            TCLIENT tcli = createClient(tpt);
            texec.exec(tcli);
            returnTransport(key, tpt);
        } catch (Exception e) {
            returnBrokenTransport(key, tpt);
            throw new RuntimeException(e);
        }
    }

    private String getConnBorrowKey() {
        // Get Application on eureka
        Application application = eurekaClient.getApplication(thriftServiceName);
        if (application == null) {
            throw new RuntimeException("Application " + thriftServiceName + " not found on eureka.");
        }

        // Get Instances on eureka
        List<InstanceInfo> instances = application.getInstances();
        if (CollectionUtils.isEmpty(instances)) {
            throw new RuntimeException("Application " + thriftServiceName + " instances empty");
        }

        // Get random instance
        int idx = cnt++ % instances.size();
        InstanceInfo instanceSelected = instances.get(idx);

        // Get ip and port
        String ip = instanceSelected.getIPAddr();
        int port = instanceSelected.getPort();
        String key = getConnPoolKey(ip, port);
        return key;
    }

    private void returnTransport(String key, TTransport transport) {
        connPool.returnObject(key, transport);
    }

    private void returnBrokenTransport(String key, TTransport transport) {
        connPool.returnBrokenObject(key, transport);
    }

    private String getConnPoolKey(String host, int port) {
        return host + ":" + port;
    }

    private synchronized void initializeApplicationInfoManager() {
        if (applicationInfoManager == null) {
            EurekaInstanceConfig instanceConfig = new MyDataCenterInstanceConfig();
            InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        }
    }

    private synchronized void initializeEurekaClient() {
        if (eurekaClient == null) {
            EurekaClientConfig clientConfig = new DefaultEurekaClientConfig();
            eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        }
    }

    public String getThriftServiceName() {
        return thriftServiceName;
    }

    public void setThriftServiceName(String thriftServiceName) {
        this.thriftServiceName = thriftServiceName;
    }
}