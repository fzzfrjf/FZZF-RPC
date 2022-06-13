package cn.fzzfrjf.serializer.protobuf;

import cn.fzzfrjf.enumeration.SerializerCode;
import cn.fzzfrjf.serializer.CommonSerializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProtobufSerializer implements CommonSerializer {

    private static LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();
    @Override
    public <T>byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(clazz);
        byte[] data;
        try{
            data = ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        }finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    public <T>T deSerialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes,obj,schema);
        return obj;
    }

    @Override
    public int getSerializerCode() {
        return SerializerCode.valueOf("PROTOBUF").getCode();
    }

    private <T>Schema<T> getSchema(Class<T> clazz){
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if(Objects.isNull(schema)){
            schema = RuntimeSchema.getSchema(clazz);
            if(Objects.nonNull(schema)){
                schemaCache.put(clazz,schema);
            }
        }
        return schema;
    }
}
