package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RenderManagerTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.RenderManager;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        final String[] setRenderPositionInfo = {null, null};
        final String[] renderWitherSkullInfo = {null, null};
        final String[] getDistanceToCameraInfo = {null, null};

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {

            @Override
            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
                return super.visitField(access, name, desc, signature, value);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if ((access & Opcodes.ACC_PUBLIC) != 0 && desc.equals("(DDD)V")) {
                    setRenderPositionInfo[0] = name;
                    setRenderPositionInfo[1] = desc;
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        int dCount = 0;

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDesc) {
                            if (opcode == Opcodes.PUTFIELD && fieldDesc.equals("D")) {
                                dCount++;
                                Field f = Reflector.findField(Mapper.RenderManager, fieldName, fieldDesc);
                                switch (dCount) {
                                    case 1:
                                        Mapper.renderPosX = f;
                                        break;
                                    case 2:
                                        Mapper.renderPosY = f;
                                        break;
                                    case 3:
                                        Mapper.renderPosZ = f;
                                        break;
                                }
                            }
                            super.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
                        }
                    };
                }

                if ((access & Opcodes.ACC_PUBLIC) != 0 && desc.equals("(L" + Type.getInternalName(Mapper.EntityClass) + ";F)V")) {
                    renderWitherSkullInfo[0] = name;
                    renderWitherSkullInfo[1] = desc;
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        boolean prevRotFound = false;

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                            if (opcode == Opcodes.PUTFIELD && owner.equals(Mapper.EntityClass.getName().replace('.', '/')) && desc.equals("F") && !prevRotFound) {
                                Mapper.prevRotationYaw = Reflector.findField(Mapper.EntityClass, name, desc);
                                prevRotFound = true;
                            }
                            super.visitFieldInsn(opcode, owner, name, desc);
                        }
                    };
                }


                if ((access & Opcodes.ACC_PUBLIC) != 0 && desc.equals("(DDD)D")) {
                    getDistanceToCameraInfo[0] = name;
                    getDistanceToCameraInfo[1] = desc;
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitFieldInsn(int opcode, String owner, String fieldName, String fieldDesc) {
                            super.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
                        }
                    };
                }

                if ((access & Opcodes.ACC_PUBLIC) != 0 && desc.equals("(L" + Type.getInternalName(Mapper.EntityClass) + ";FZ)Z")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        boolean firstIfFound = false;
                        boolean foundGL = false;
                        String lastIntFieldName = null;
                        int ldcFloatCount = 0;

                        @Override
                        public void visitLdcInsn(Object cst) {
                            if (!foundGL && cst instanceof Float && ((Float) cst) == 1.0f) {
                                ldcFloatCount++;
                            }
                            super.visitLdcInsn(cst);
                        }

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String fieldDesc) {
                            if (!firstIfFound && opcode == Opcodes.GETFIELD && fieldDesc.equals("I") && owner.equals(Type.getInternalName(Mapper.EntityClass))) {
                                lastIntFieldName = name;
                            }
                            super.visitFieldInsn(opcode, owner, name, fieldDesc);
                        }

                        @Override
                        public void visitJumpInsn(int opcode, Label label) {
                            if (!firstIfFound && (opcode == Opcodes.IFEQ || opcode == Opcodes.IFNE)) {
                                if (lastIntFieldName != null) {
                                    Field f = Reflector.findField(Mapper.EntityClass, lastIntFieldName, "I");
                                    if (f != null) {
                                        Mapper.ticksExisted = f;
                                        firstIfFound = true;
                                    }
                                }
                            }
                            super.visitJumpInsn(opcode, label);
                        }
                    };
                }

                if ((access & Opcodes.ACC_PUBLIC) != 0 && desc.equals("(L" + Type.getInternalName(Mapper.World) + ";L" + Type.getInternalName(Mapper.FontRenderer) + ";L" + Type.getInternalName(Mapper.EntityClass) + ";L" + Type.getInternalName(Mapper.EntityClass) + ";L" + Type.getInternalName(Mapper.GameSettings) + ";F)V")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        int floatPutCount = 0;
                        int viewerPosCount = 0;
                        boolean thirdPersonViewFound = false;
                        String lastGetFieldName = null;

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String fieldDesc) {
                            if (!thirdPersonViewFound && opcode == Opcodes.GETFIELD && fieldDesc.equals("I") && owner.equals(Type.getInternalName(Mapper.GameSettings))) {
                                Field f = Reflector.findField(Mapper.GameSettings, name, fieldDesc);
                                if (f != null) {
                                    Mapper.thirdPersonView = f;
                                    thirdPersonViewFound = true;
                                }
                            }
                            if (opcode == Opcodes.GETFIELD && fieldDesc.equals("F") && owner.equals(Type.getInternalName(Mapper.EntityClass))) {
                                lastGetFieldName = name;
                            }
                            if (opcode == Opcodes.PUTFIELD && fieldDesc.equals("F") && owner.equals(Type.getInternalName(Mapper.RenderManager))) {
                                if (floatPutCount >= 4 && floatPutCount < 6) {
                                    Field rotationField = lastGetFieldName != null ? Reflector.findField(Mapper.EntityClass, lastGetFieldName, "F") : null;
                                    if (rotationField != null) {
                                        switch (floatPutCount) {
                                            case 4:
                                                Mapper.prevRotationYaw = rotationField;
                                                break;
                                            case 5:
                                                Mapper.prevRotationPitch = rotationField;
                                                break;
                                        }
                                    }
                                }
                                floatPutCount++;
                                lastGetFieldName = null;
                            }
                            if (opcode == Opcodes.GETFIELD && fieldDesc.equals("D") && owner.equals(Type.getInternalName(Mapper.EntityClass))) {
                                lastGetFieldName = name;
                            }
                            if (opcode == Opcodes.PUTFIELD && fieldDesc.equals("D") && owner.equals(Type.getInternalName(Mapper.RenderManager)) && viewerPosCount < 3) {
                                Field viewerField = Reflector.findField(Mapper.RenderManager, name, fieldDesc);
                                switch (viewerPosCount) {
                                    case 0:
                                        Mapper.viewerPosX = viewerField;
                                        break;
                                    case 1:
                                        Mapper.viewerPosY = viewerField;
                                        break;
                                    case 2:
                                        Mapper.viewerPosZ = viewerField;
                                        break;
                                }
                                viewerPosCount++;
                                lastGetFieldName = null;
                            }
                            super.visitFieldInsn(opcode, owner, name, fieldDesc);
                        }

                        @Override
                        public void visitJumpInsn(int opcode, Label label) {
                            super.visitJumpInsn(opcode, label);
                        }
                    };
                }

                return mv;
            }
        };

        cr.accept(cv, 0);
        byte[] transformed = cw.toByteArray();

        try {
            if (setRenderPositionInfo[0] != null && setRenderPositionInfo[1] != null) {
                Class<?>[] parameterTypes = new Class<?>[]{double.class, double.class, double.class};
                Mapper.setRenderPosition = Mapper.RenderManager.getMethod(setRenderPositionInfo[0], parameterTypes);
            }
            if (renderWitherSkullInfo[0] != null && renderWitherSkullInfo[1] != null) {
                Class<?>[] parameterTypes = new Class<?>[]{Mapper.EntityClass, float.class};
                Mapper.renderWitherSkull = Mapper.RenderManager.getMethod(renderWitherSkullInfo[0], parameterTypes);
            }
            if (getDistanceToCameraInfo[0] != null && getDistanceToCameraInfo[1] != null) {
                Class<?>[] parameterTypes = new Class<?>[]{double.class, double.class, double.class};
                Mapper.getDistanceToCamera = Mapper.RenderManager.getMethod(getDistanceToCameraInfo[0], parameterTypes);
            }
        } catch (Exception e) {
            Mapper.log("[RenderManagerTransformer] [ERROR] Failed to resolve methods: " + e.getMessage());
        }

        try {
            File file = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\RenderManager_Transformed.class");
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), transformed);
        } catch (IOException e) {
        }

        return transformed;
    }
}