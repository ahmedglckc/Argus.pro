package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AbstractClientPlayerTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.AbstractClientPlayer;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                MethodVisitor mv = super.visitMethod(access, name, desc, sig, ex);

                String resourceLocationDesc = Mapper.ResourceLocation != null ?
                        "L" + Mapper.ResourceLocation.getName().replace('.', '/') + ";" : null;
                if ((access & Opcodes.ACC_PUBLIC) != 0 && resourceLocationDesc != null && desc.equals("()" + resourceLocationDesc)) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        int nullCheckCount = 0;
                        int putFieldCount = 0;
                        boolean containsKeyFound = false;

                        @Override
                        public void visitJumpInsn(int opcode, Label label) {
                            if (opcode == Opcodes.IFNULL || opcode == Opcodes.IFNONNULL) {
                                nullCheckCount++;
                            }
                            super.visitJumpInsn(opcode, label);
                        }

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDesc) {
                            if (opcode == Opcodes.PUTFIELD) {
                                putFieldCount++;
                            }
                            super.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
                        }

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String methodName, String methodDesc, boolean isInterface) {
                            if (opcode == Opcodes.INVOKEINTERFACE && owner.equals("java/util/Map") && methodName.equals("containsKey") && methodDesc.equals("(Ljava/lang/Object;)Z")) {
                                containsKeyFound = true;
                            }
                            super.visitMethodInsn(opcode, owner, methodName, methodDesc, isInterface);
                        }

                        @Override
                        public void visitEnd() {
                            try {
                                if (putFieldCount == 0) {
                                    if (nullCheckCount < 8 && !containsKeyFound) {
                                        Mapper.getLocationSkin = getTargetClass().getDeclaredMethod(name);
                                    } else if (nullCheckCount > 10 && containsKeyFound) {
                                        Mapper.getLocationCape = getTargetClass().getDeclaredMethod(name);
                                    }
                                }
                            } catch (NoSuchMethodException e) {
                                Mapper.log("[AbstractClientPlayerTransformer] Method not found: " + name);
                            }
                            super.visitEnd();
                        }
                    };
                }
                return mv;
            }
        };

        cr.accept(cv, 0);
        byte[] transformed = cw.toByteArray();

        try {
            File out = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\AbstractClientPlayer_Transformed.class");
            out.getParentFile().mkdirs();
            Files.write(out.toPath(), transformed);
        } catch (IOException e) {
        }

        return transformed;
    }
}