package cn.china.no.one.rpc.consumer;

import cn.china.no.one.rpc.common.RpcRequest;
import cn.china.no.one.rpc.common.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.consumer
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/12
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private String ip;
    private Integer port;
    private RpcResponse rpcResponse;
    private final Object obj = new Object();

    public RpcClient(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public RpcResponse send(RpcRequest request) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(RpcClient.this);
                        }
                    });
            ChannelFuture f = b.connect(ip, port).sync();
            f.channel().writeAndFlush(request).sync();
            synchronized (obj) {
                obj.wait();
            }
            if (rpcResponse != null) {
                f.channel().closeFuture().sync();
            }
            return rpcResponse;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.rpcResponse = rpcResponse;
        synchronized (obj) {
            obj.notifyAll();
        }
    }
}
