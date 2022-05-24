package cn.fzzfrjf.serializer;


public interface CommonSerializer {
    <T>byte[] serialize(T obj);

    <T>T deSerialize(byte[] bytes,Class<T> clazz);
    int getSerializerCode();
    static CommonSerializer getSerializerByCode(int code){
        switch (code){
            case 0:
                return new ProtobufSerializer();
        }
        return null;
    }
}
