package cn.china.no.one.rpc.provider;

import cn.china.no.one.rpc.common.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jdk.nashorn.internal.runtime.linker.Bootstrap;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.provider
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/11
 */
public class RpcServer implements ApplicationContextAware,InitializingBean {

    private ServiceRegister register;
    private String serverAddress;
    private Map<String, Object> handlerMap = new HashMap<>();

    public void setRegister(ServiceRegister register) {
        this.register = register;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new RequestHandler(handlerMap));
                        }
                    });
            String[] hostAndPort = serverAddress.split(":");
            ChannelFuture future = bootstrap.bind(hostAndPort[0], Integer.parseInt(hostAndPort[1])).sync();
            // 注册服务
            register.register(serverAddress);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> beansMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(beansMap)) {
            for (Object objectBean: beansMap.values()) {
                // 服务类实现的服务接口 写在注解RpcService里面了
                String interfaceName = objectBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, objectBean);
            }
        }
    }
}
