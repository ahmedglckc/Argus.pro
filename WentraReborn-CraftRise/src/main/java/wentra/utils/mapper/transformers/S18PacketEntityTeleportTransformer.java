package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import static wentra.utils.mapper.Mapper.*;

public class S18PacketEntityTeleportTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.S18PacketEntityTeleport;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if ((access & Opcodes.ACC_PUBLIC) != 0 && name.equals("<init>") &&
                        desc.equals("(L" + Type.getInternalName(Mapper.EntityClass) + ";)V")) {

                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        private String lastFieldName, lastFieldOwner, lastFieldDesc;
                        private String lastMethodOwner, lastMethodName;
                        private String lastPutFieldName, lastPutFieldDesc;

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDesc) {
                            if (opcode == Opcodes.GETFIELD && owner.equals(Type.getInternalName(Mapper.EntityClass))) {
                                lastFieldName = fieldName;
                                lastFieldOwner = owner;
                                lastFieldDesc = fieldDesc;
                            } else if (opcode == Opcodes.PUTFIELD &&
                                    owner.equals(Type.getInternalName(getTargetClass())) &&
                                    fieldDesc.equals("Z")) {
                                lastPutFieldName = fieldName;
                                lastPutFieldDesc = fieldDesc;
                            }
                            super.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
                        }

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String methodName, String methodDesc, boolean isInterface) {
                            if (opcode == Opcodes.INVOKEVIRTUAL &&
                                    methodDesc.equals("()Z") && lastFieldName != null) {
                                lastMethodOwner = owner;
                                lastMethodName = methodName;
                            }
                            super.visitMethodInsn(opcode, owner, methodName, methodDesc, isInterface);
                        }

                        @Override
                        public void visitEnd() {
                            if (lastPutFieldName != null && lastFieldName != null && lastMethodOwner != null && lastMethodName != null) {
                                try {
                                    Field f = Reflector.findField(Mapper.EntityClass, lastFieldName, lastFieldDesc);
                                    if (f != null) {
                                        onGround = f;

                                        Class<?> boolContainerClass = f.getType();
                                        Mapper.BooleanContainer = boolContainerClass;

                                        Method method = boolContainerClass.getDeclaredMethod(lastMethodName);
                                        if (method != null && method.getReturnType() == boolean.class) {
                                            Mapper.getValueBoolean = method;
                                            Mapper.BooleanContainer = boolContainerClass;
                                        }
                                    }
                                } catch (Exception e) {
                                    Mapper.log("[ERROR] Failed to resolve mappings: " + e.getMessage());
                                }
                            }
                            super.visitEnd();
                        }
                    };
                }

                return mv;
            }
        };

        cr.accept(cv, 0);
        byte[] outBytes = cw.toByteArray();

        try {
            File file = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\S18PacketEntityTeleport_Transformed.class");
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), outBytes);
        } catch (IOException e) {
            Mapper.log("[ERROR] Failed to write transformed class: " + e.getMessage());
        }

        return outBytes;
    }
}
