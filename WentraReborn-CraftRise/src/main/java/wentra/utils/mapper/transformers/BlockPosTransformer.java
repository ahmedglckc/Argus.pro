package wentra.utils.mapper.transformers;

import wentra.utils.mapper.Mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BlockPosTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.BlockPos;
    }

    public static byte[] transform(byte[] classBytes) {
        try {
            String path = "C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\BlockPos_Transformed.class";
            File file = new File(path);
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), classBytes);
        } catch (IOException e) {
            Mapper.log("[NetworkPlayerInfoTransformer] [ERROR] Failed to write transformed class: " + e.getMessage());
        }

        return classBytes;
    }
}
