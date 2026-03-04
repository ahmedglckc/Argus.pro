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

import static wentra.utils.mapper.Mapper.*;

public class NetworkPlayerInfoTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.NetworkPlayerInfo;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        List<FieldInfo> resourceLocationFields = new ArrayList<>();

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                String resourceLocationDesc = Mapper.ResourceLocation != null
                        ? "L" + Mapper.ResourceLocation.getName().replace('.', '/') + ";"
                        : null;

                if ((access & Opcodes.ACC_PUBLIC) != 0 && resourceLocationDesc != null && desc.equals(resourceLocationDesc)) {
                    resourceLocationFields.add(new FieldInfo(name, desc));
                }
                return super.visitField(access, name, desc, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if ((access & Opcodes.ACC_PUBLIC) != 0 && desc.equals("()Z")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        private String fieldName = null;
                        private String fieldDesc = null;
                        private int nullCheckCount = 0;

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                            if (opcode == Opcodes.GETFIELD && owner.equals(Type.getInternalName(getTargetClass()))) {
                                fieldName = name;
                                fieldDesc = desc;
                            }
                            super.visitFieldInsn(opcode, owner, name, desc);
                        }

                        @Override
                        public void visitJumpInsn(int opcode, Label label) {
                            if (opcode == Opcodes.IFNULL || opcode == Opcodes.IFNONNULL) {
                                nullCheckCount++;
                            }
                            super.visitJumpInsn(opcode, label);
                        }

                        @Override
                        public void visitEnd() {
                            if (nullCheckCount == 1 && fieldName != null && fieldDesc != null) {
                                try {
                                    Field field = Reflector.findField(getTargetClass(), fieldName, fieldDesc);
                                    if (field != null) {
                                        Mapper.locationSkin = field;
                                    } else {
                                        Mapper.log("[NetworkPlayerInfoTransformer] [ERROR] Field not found: " + fieldName);
                                    }
                                } catch (Exception e) {
                                    Mapper.log("[NetworkPlayerInfoTransformer] [ERROR] Failed to map locationSkin: " + e.getMessage());
                                }
                            }
                            super.visitEnd();
                        }
                    };
                }

                return mv;
            }

            @Override
            public void visitEnd() {
                try {
                    String locationSkinName = Mapper.locationSkin != null ? Mapper.locationSkin.getName() : null;
                    int mappedCount = 0;

                    for (FieldInfo fieldInfo : resourceLocationFields) {
                        if (locationSkinName != null && fieldInfo.name.equals(locationSkinName)) {
                            continue;
                        }

                        Field field = Reflector.findField(getTargetClass(), fieldInfo.name, fieldInfo.desc);
                        if (field != null && mappedCount < 2) {
                            if (mappedCount == 0) {
                                Mapper.locationCape = field;
                            } else {
                                Mapper.CRlocationCape = field;
                            }
                            mappedCount++;
                        }
                    }

                    if (mappedCount != 2) {
                        Mapper.log("[NetworkPlayerInfoTransformer] [ERROR] Expected 2 ResourceLocation fields to map, found: " + mappedCount);
                    }
                } catch (Exception e) {
                    Mapper.log("[NetworkPlayerInfoTransformer] [ERROR] Failed to map ResourceLocation fields: " + e.getMessage());
                }

                super.visitEnd();
            }
        };

        cr.accept(cv, 0);
        byte[] transformed = cw.toByteArray();

        try {
            String path = "C:\\Users\\" + System.getenv("username") +
                    "\\AppData\\Roaming\\.craftrise\\libraries\\NetworkPlayerInfo_Transformed.class";
            File file = new File(path);
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), transformed);
        } catch (IOException e) {
            Mapper.log("[NetworkPlayerInfoTransformer] [ERROR] Failed to write transformed class: " + e.getMessage());
        }

        return transformed;
    }

    private static class FieldInfo {
        String name;
        String desc;

        FieldInfo(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }
}