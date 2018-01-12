package cn.china.no.one.rpc.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.provider
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/11
 */
public class BootStrap {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("classpath:rpc-server.xml");
    }
}
