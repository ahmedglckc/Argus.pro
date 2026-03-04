package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

public class KeyBindingTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.KeyBinding;
    }

    public static byte[] transform(byte[] classBytes) {
        try {
            Field[] fields = getTargetClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                int acc = f.getModifiers();
                if (f.getType() == boolean.class && ((acc & Opcodes.ACC_PROTECTED) != 0)) {
                    Mapper.KeyBindPressed = f;
                    break;
                }
            }
            if (Mapper.KeyBindPressed == null) {
                Mapper.log("[KeyBindingTransformer] protected boolean field NOT found!");
            }
        } catch (Exception e) {
            Mapper.log("[KeyBindingTransformer] error finding pressed field: " + e.getMessage());
        }

        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                String expectedDesc = "(L" + Mapper.Minecraft.getName().replace('.', '/') + ";IZ)V";
                if ((access & Opcodes.ACC_PUBLIC) != 0 &&
                        (access & Opcodes.ACC_STATIC) != 0 &&
                        desc.equals(expectedDesc)) {
                    try {
                        Class<?>[] parameterTypes = new Class<?>[]{Mapper.Minecraft, int.class, boolean.class};
                        Mapper.setKeyBindState = getTargetClass().getDeclaredMethod(name, parameterTypes);
                    } catch (NoSuchMethodException e) {
                        Mapper.log("[KeyBindingTransformer] [ERROR] Method not found: " + name);
                    }
                }

                return mv;
            }
        };

        cr.accept(cv, 0);
        byte[] transformed = cw.toByteArray();

        try {
            String path = "C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\KeyBinding_Transformed.class";
            File file = new File(path);
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), transformed);
        } catch (IOException e) {
            Mapper.log("[KeyBindingTransformer] [ERROR] Failed to write transformed class: " + e.getMessage());
        }

        return transformed;
    }
}
