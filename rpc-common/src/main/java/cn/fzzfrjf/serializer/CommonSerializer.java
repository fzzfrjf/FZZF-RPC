package cn.fzzfrjf.serializer;


import cn.fzzfrjf.extension.SPI;
import cn.fzzfrjf.serializer.kryo.KryoSerializer;
import cn.fzzfrjf.serializer.protobuf.ProtobufSerializer;

@SPI
public interface CommonSerializer {
    <T>byte[] serialize(T obj);

    <T>T deSerialize(byte[] bytes,Class<T> clazz);
    int getSerializerCode();
    static CommonSerializer getSerializerByCode(int code){
        switch (code){
            case 0:
                return new ProtobufSerializer();
            case 1:
                return new KryoSerializer();
        }
        return null;
    }
}
