package cn.china.no.one.rpc.consumer;

import cn.china.no.one.rpc.common.RpcRequest;
import cn.china.no.one.rpc.common.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;
import java.util.UUID;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.consumer
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/12
 */
public class RpcProxy {

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClass().getClassLoader(), new Class[]{interfaceClass}, (proxy, method, args) -> {
            RpcRequest request = new RpcRequest();
            request.setRequestId(UUID.randomUUID().toString());
            request.setClassName(method.getDeclaringClass().getName());
            request.setMethodName(method.getName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            if (null == serviceDiscovery) {
                return null;
            }
            String serverAddress = serviceDiscovery.discovery();
            String[] ipAndPort = serverAddress.split(":");
            RpcClient client = new RpcClient(ipAndPort[0], Integer.valueOf(ipAndPort[1]));
            RpcResponse response = client.send(request);
            if (null == response.getError()) {
                return response.getResult();
            } else {
                throw response.getError();
            }
        });
    }
}