package wentra.utils.mapper.transformers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.*;

import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;
import wentra.utils.mapper.Mapper;

public class S12PacketTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.S12PacketEntityVelocity;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        String entityInternalName = Type.getInternalName(Mapper.EntityClass);

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if (name.equals("<init>")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        private final List<String> fieldChain = new ArrayList<>();
                        private int fieldCount = 0;

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDesc) {
                            if (opcode == Opcodes.GETFIELD && owner.equals(entityInternalName)) {
                                fieldChain.add(fieldName);
                                Field field = Reflector.findField(Mapper.EntityClass, fieldName, fieldDesc);
                                if (field != null && fieldCount < 3) {
                                    switch (fieldCount) {
                                        case 0:
                                            Mapper.motionX = field;
                                            break;
                                        case 1:
                                            Mapper.motionY = field;
                                            break;
                                        case 2:
                                            Mapper.motionZ = field;
                                            break;
                                    }
                                    fieldCount++;
                                }
                            }
                            super.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
                        }
                    };
                }

                return mv;
            }
        };

        cr.accept(cv, 0);
        byte[] transformed = cw.toByteArray();

        try {
            String path = "C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\S12PacketEntityVelocity_Transformed.class";
            File file = new File(path);
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), transformed);
        } catch (IOException e) {
        }

        return transformed;
    }
}
