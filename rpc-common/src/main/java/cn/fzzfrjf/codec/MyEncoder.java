package cn.fzzfrjf.codec;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.enumeration.PackageType;
import cn.fzzfrjf.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/*
        +---------------+---------------+-----------------+-------------+
        |  Magic Number |  Package Type | Serializer Type | Data Length |
        |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
        +---------------+---------------+-----------------+-------------+
        |                          Data Bytes                           |
        |                   Length: ${Data Length}                      |
        +---------------------------------------------------------------+

 */


public class MyEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0x19990430;

    private final CommonSerializer serializer;

    public MyEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if(msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACKAGE.getCode());
        }else{
            out.writeInt(PackageType.RESPONSE_PACKAGE.getCode());
        }
        out.writeInt(serializer.getSerializerCode());
        byte[] data = serializer.serialize(msg);
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
