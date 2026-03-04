package wentra.utils.render.glutils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class GLAllocation {
    public static synchronized ByteBuffer createDirectByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer createDirectIntBuffer(int capacity) {
        return createDirectByteBuffer(capacity << 2).asIntBuffer();
    }
}