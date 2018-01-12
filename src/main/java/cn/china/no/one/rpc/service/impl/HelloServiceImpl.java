package cn.china.no.one.rpc.service.impl;

import cn.china.no.one.rpc.common.RpcService;
import cn.china.no.one.rpc.service.IHelloService;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.service.impl
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/11
 */
@RpcService(IHelloService.class)
public class HelloServiceImpl implements IHelloService {

    @Override
    public void sayHello(){
        System.out.println("Hello");
    }
}
