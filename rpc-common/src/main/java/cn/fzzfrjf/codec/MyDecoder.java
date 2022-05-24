package cn.fzzfrjf.codec;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;
import cn.fzzfrjf.enumeration.PackageType;
import cn.fzzfrjf.enumeration.RpcError;
import cn.fzzfrjf.exception.RpcException;
import cn.fzzfrjf.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/*
        +---------------+---------------+-----------------+-------------+
        |  Magic Number |  Package Type | Serializer Type | Data Length |
        |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
        +---------------+---------------+-----------------+-------------+
        |                          Data Bytes                           |
        |                   Length: ${Data Length}                      |
        +---------------------------------------------------------------+

 */


public class MyDecoder extends ReplayingDecoder {
    private static final int MAGIC_NUMBER = 0x19990430;
    private static final Logger logger = LoggerFactory.getLogger(MyDecoder.class);
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        if(MAGIC_NUMBER != magic){
            logger.error("不识别的协议包");
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageType = in.readInt();
        Class<?> packageClass;
        if(packageType == PackageType.REQUEST_PACKAGE.getCode()){
            packageClass = RpcRequest.class;
        }else if(packageType == PackageType.RESPONSE_PACKAGE.getCode()){
            packageClass = RpcResponse.class;
        }else{
            logger.error("不识别的数据包");
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        CommonSerializer serializer = CommonSerializer.getSerializerByCode(in.readInt());
        int length = in.readInt();
        byte[] data = new byte[length];
        in.readBytes(data);
        Object o = serializer.deSerialize(data, packageClass);
        out.add(o);
    }
}
