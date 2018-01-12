package cn.china.no.one.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author chenjuzhi
 * @Title: rpc
 * @Package cn.china.no.one.rpc.common
 * @Description: ${TODO}
 * @Nimitz
 * @date 2018/1/11
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    public RpcDecoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //表示头长度的字节数，由于传的是一个int类型的值，所以这里值为4
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        // 标记到读的位置
        byteBuf.markReaderIndex();
        // 读取传送过来的消息的长度。ByteBuf的readInt()方法会让他的readIndex增加4
        int dataLen = byteBuf.readInt();
        // 小于0属于异常情况，关闭连接
        if (dataLen < 0) {
            channelHandlerContext.close();
        }
        //读到的消息体长度如果小于传送过来的消息长度，则resetReaderIndex。把readIndex重置到mark的地方
        if (byteBuf.readableBytes() < dataLen) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLen];
        byteBuf.readBytes(data);
        list.add(SerializationUtil.deserialize(data, clazz));
    }
}
