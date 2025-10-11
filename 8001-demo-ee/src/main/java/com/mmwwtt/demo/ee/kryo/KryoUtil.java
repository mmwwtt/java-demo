package com.mmwwtt.demo.ee.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Objects;

public final class KryoUtil {

    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int MAX_BUFFER_SIZE = -1;   // 无上限

    /* 线程本地 Kryo 工厂 */
    private static final ThreadLocal<Kryo> KRYO_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false);          // 禁用预注册，任意类都能拷贝
        kryo.setInstantiatorStrategy(new org.objenesis.strategy.StdInstantiatorStrategy());
        return kryo;
    });

    /* 线程本地输出/输入缓存，避免反复分配 */
    private static final ThreadLocal<Output> OUTPUT_LOCAL = ThreadLocal.withInitial(() -> new Output(DEFAULT_BUFFER_SIZE, MAX_BUFFER_SIZE));
    private static final ThreadLocal<Input>  INPUT_LOCAL  = ThreadLocal.withInitial(() -> new Input());

    private KryoUtil() {}

    /**
     * 深拷贝任意对象
     * @param source 原对象
     * @param <T>    类型
     * @return 全新副本
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T source) {
        Objects.requireNonNull(source, "source cannot be null");

        Kryo kryo   = KRYO_LOCAL.get();
        Output output = OUTPUT_LOCAL.get();
        Input input  = INPUT_LOCAL.get();

        output.reset();                  // 清空缓冲区以便复用, 但先清空
        kryo.writeClassAndObject(output, source);
        output.flush();

        input.setBuffer(output.getBuffer());
        return (T) kryo.readClassAndObject(input);
    }

    /* 清理 ThreadLocal，防止内存泄漏（可选，容器停止时调用） */
    public static void remove() {
        KRYO_LOCAL.remove();
        OUTPUT_LOCAL.remove();
        INPUT_LOCAL.remove();
    }
}