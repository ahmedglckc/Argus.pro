package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static wentra.utils.mapper.Mapper.*;

public class EntityLivingBaseTransformer {
    public static Class<?> getTargetClass() {
        return Mapper.EntityLivingBase;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        final List<String> customFields = new ArrayList<>();
        final List<String> customFieldsDesc = new ArrayList<>();
        final List<Integer> putFieldOrderInCtor = new ArrayList<>();
        final String[] commonCls = {null};

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                if ((access & Opcodes.ACC_PUBLIC) != 0) {
                    if (descriptor.startsWith("L") && descriptor.endsWith(";")) {
                        String clsName = descriptor.substring(1, descriptor.length() - 1).replace('/', '.');
                        if (!clsName.startsWith("java.") && !clsName.startsWith("javax.")) {
                            if (commonCls[0] == null) {
                                commonCls[0] = clsName;
                                customFields.add(name);
                                customFieldsDesc.add(descriptor);
                            } else if (commonCls[0].equals(clsName) && customFields.size() < 2) {
                                customFields.add(name);
                                customFieldsDesc.add(descriptor);
                            }
                        }
                    }
                }
                return super.visitField(access, name, descriptor, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if (name.equals("<init>") && desc.contains(Mapper.World.getName().replace('.', '/'))) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDesc) {
                            if (opcode == Opcodes.PUTFIELD) {
                                int idx = customFields.indexOf(fieldName);
                                if (idx != -1 && !putFieldOrderInCtor.contains(idx)) {
                                    putFieldOrderInCtor.add(idx);
                                }
                            }
                            super.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
                        }

                        @Override
                        public void visitEnd() {
                            if (putFieldOrderInCtor.size() == 2) {
                                moveStrafing = Reflector.findField(getTargetClass(), customFields.get(putFieldOrderInCtor.get(0)), customFieldsDesc.get(putFieldOrderInCtor.get(0)));
                                moveForward = Reflector.findField(getTargetClass(), customFields.get(putFieldOrderInCtor.get(1)), customFieldsDesc.get(putFieldOrderInCtor.get(1)));
                            }
                            super.visitEnd();
                        }
                    };
                }

                if ((access & Opcodes.ACC_PUBLIC) != 0 && (access & Opcodes.ACC_STATIC) == 0 && desc.equals("()V")) {
                    return new MethodVisitor(Opcodes.ASM6, mv) {
                        private int putFieldCount = 0;
                        private String boolField = null;

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                            if (opcode == Opcodes.PUTFIELD) {
                                putFieldCount++;
                                if (descriptor.equals("Z"))
                                    boolField = name;
                            }
                            super.visitFieldInsn(opcode, owner, name, descriptor);
                        }

                        @Override
                        public void visitEnd() {
                            if (putFieldCount == 2) {
                                if (boolField != null) {
                                    Mapper.isSwingInProgress = Reflector.findField(getTargetClass(), boolField, "Z");
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
        byte[] transformed = cw.toByteArray();

        try {
            File file = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\EntityLivingBase_Transformed.class");
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), transformed);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transformed;
    }
}