package cn.china.no.one.rpc.provider;

import cn.china.no.one.rpc.common.RpcRequest;
import cn.china.no.one.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.provider
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/11
 */
public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private Map<String, Object> handlerMap;

    public RequestHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Rpc handler exception ", cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        try {
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (Exception e) {
            response.setError(e);
        }
        //返回
        channelHandlerContext.writeAndFlush(response);
    }

    private Object handle(RpcRequest rpcRequest) throws InvocationTargetException {
        Object serviceBean = handlerMap.get(rpcRequest.getClassName());
        FastClass fastClass = FastClass.create(rpcRequest.getClass());
        FastMethod fastMethod = fastClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        return fastMethod.invoke(serviceBean, rpcRequest.getParameters());
    }
}
