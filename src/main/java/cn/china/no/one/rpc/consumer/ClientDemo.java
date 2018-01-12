package cn.china.no.one.rpc.consumer;

import cn.china.no.one.rpc.service.IHelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.consumer
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/12
 */
public class ClientDemo {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("rpc-client.xml");
        RpcProxy rpcProxy = applicationContext.getBean("rpcProxy", RpcProxy.class);
        IHelloService helloService = rpcProxy.create(IHelloService.class);
        helloService.sayHello();
    }
}
