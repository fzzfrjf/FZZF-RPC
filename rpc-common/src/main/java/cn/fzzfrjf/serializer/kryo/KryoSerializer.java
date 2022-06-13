package cn.fzzfrjf.serializer.kryo;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;
import cn.fzzfrjf.enumeration.RpcError;
import cn.fzzfrjf.enumeration.SerializerCode;
import cn.fzzfrjf.exception.RpcException;
import cn.fzzfrjf.serializer.CommonSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });
    @Override
    public <T> byte[] serialize(T obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)){
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        }catch (Exception e){
            logger.error("Kryo序列化时发生错误" + e);
            throw new RpcException(RpcError.SERIALIZER_ERROR);
        }
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream)){
            Kryo kryo = kryoThreadLocal.get();
            T t = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return t;
        }catch (Exception e){
            logger.error("Kryo反序列化时发生错误" + e);
            throw new RpcException(RpcError.SERIALIZER_ERROR);
        }
    }

    @Override
    public int getSerializerCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
