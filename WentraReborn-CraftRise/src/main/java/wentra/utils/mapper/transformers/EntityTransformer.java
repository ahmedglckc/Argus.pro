package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EntityTransformer {
    public static Class<?> getTargetClass() {
        return Mapper.EntityClass;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                MethodVisitor mv = super.visitMethod(acc, name, desc, sig, ex);

                if ((acc & Opcodes.ACC_PUBLIC) != 0 && (acc & Opcodes.ACC_STATIC) == 0) {

                    if (desc.equals("()V")) {
                        return new MethodVisitor(Opcodes.ASM9, mv) {
                            int putCount = 0;
                            String boolF = null, floatF = null;

                            @Override
                            public void visitFieldInsn(int opcode, String owner, String fname, String fd) {
                                if (opcode == Opcodes.PUTFIELD) {
                                    putCount++;
                                    if (fd.equals("Z")) boolF = fname;
                                    if (fd.equals("F")) floatF = fname;
                                }
                                super.visitFieldInsn(opcode, owner, fname, fd);
                            }

                            @Override
                            public void visitEnd() {
                                if (putCount == 2) {
                                    if (boolF != null)
                                        Mapper.isInWeb = Reflector.findField(getTargetClass(), boolF, "Z");
                                    if (floatF != null)
                                        Mapper.fallDistance = Reflector.findField(getTargetClass(), floatF, "F");
                                }
                                super.visitEnd();
                            }
                        };
                    }

                    if (desc.equals("(DDDFF)V")) {
                        return new MethodVisitor(Opcodes.ASM9, mv) {
                            int putCount = 0;
                            List<String> putFields = new ArrayList<>();
                            List<String> putFieldDescs = new ArrayList<>();

                            Field getFieldByIndex(int i) {
                                return Reflector.findField(getTargetClass(), putFields.get(i), putFieldDescs.get(i));
                            }

                            @Override
                            public void visitFieldInsn(int opcode, String owner, String fname, String fd) {
                                if (opcode == Opcodes.PUTFIELD && putCount < 9) {
                                    putFields.add(fname);
                                    putFieldDescs.add(fd);
                                    putCount++;
                                }
                                super.visitFieldInsn(opcode, owner, fname, fd);
                            }

                            @Override
                            public void visitEnd() {
                                try {
                                    for (int i = 0; i < Math.min(9, putFields.size()); i++) {
                                        Field f = getFieldByIndex(i);
                                        if (f != null) {
                                            String classPath = f.getDeclaringClass().getName().replace('.', '/');
                                        }
                                    }

                                    if (putFields.size() == 12) {
                                        Mapper.setLocationAndAngles = getTargetClass().getMethod(name, double.class, double.class, double.class, float.class, float.class);

                                        Mapper.prevPosX = getFieldByIndex(1);
                                        Mapper.prevPosY = getFieldByIndex(4);
                                        Mapper.prevPosZ = getFieldByIndex(7);
                                    }
                                    if (putFields.size() >= 9) {
                                        Mapper.posX = getFieldByIndex(0);
                                        Mapper.lastTickPosX = getFieldByIndex(1);
                                        Mapper.posY = getFieldByIndex(2);
                                        Mapper.lastTickPosY = getFieldByIndex(3);
                                        Mapper.posZ = getFieldByIndex(4);
                                        Mapper.lastTickPosZ = getFieldByIndex(5);
                                        Mapper.rotationYaw = getFieldByIndex(6);
                                        Mapper.rotationPitch = getFieldByIndex(8);
                                    }
                                } catch (Exception e) {
                                    Mapper.log("[EntityTransformer][visitEnd Error] " + e.getMessage());
                                    e.printStackTrace();
                                }
                                super.visitEnd();
                            }
                        };
                    }

                    // setPositionAndUpdate (DDD)V
                    if (desc.equals("(DDD)V")) {
                        return new MethodVisitor(Opcodes.ASM9, mv) {
                            int invokeCount = 0;

                            @Override
                            public void visitMethodInsn(int opcode, String owner, String mName, String mDesc, boolean itf) {
                                if (opcode == Opcodes.INVOKEVIRTUAL && mDesc.equals("(DDDFF)V")) {
                                    invokeCount++;
                                }
                                super.visitMethodInsn(opcode, owner, mName, mDesc, itf);
                            }

                            @Override
                            public void visitEnd() {
                                if (invokeCount > 0) {
                                    try {
                                        Mapper.setPositionAndUpdate = getTargetClass().getMethod(name, double.class, double.class, double.class);
                                    } catch (Exception e) {
                                        Mapper.log("[EntityTransformer][ERROR] setPositionAndUpdate bulunamadı: " + e.getMessage());
                                    }
                                }
                                super.visitEnd();
                            }
                        };
                    }
                }

                return mv;
            }
        };

        cr.accept(cv, 0);

        byte[] out = cw.toByteArray();

        try {
            File f = new File("C:\\Users\\" + System.getenv("USERNAME") + "\\AppData\\Roaming\\.craftrise\\libraries\\Entity_Transformed.class");
            f.getParentFile().mkdirs();
            Files.write(Paths.get(f.getAbsolutePath()), out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }
}