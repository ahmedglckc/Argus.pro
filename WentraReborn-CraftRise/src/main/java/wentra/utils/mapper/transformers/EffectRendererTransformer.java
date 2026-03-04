package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;

public class EffectRendererTransformer {
    public static Class<?> getTargetClass() {
        return Mapper.EffectRenderer;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {

            @Override
            public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                Type[] args = Type.getArgumentTypes(desc);
                Type retType = Type.getReturnType(desc);

                boolean isPublic = (acc & Opcodes.ACC_PUBLIC) != 0;
                boolean voidRet = retType.equals(Type.VOID_TYPE);
                boolean paramsOk = args.length == 2
                        && args[0].getClassName().equals(Mapper.EntityClass.getName())
                        && args[1].getSort() == Type.FLOAT;

                if (isPublic && voidRet && paramsOk) {
                    try {
                        java.lang.reflect.Method m = Mapper.EffectRenderer.getDeclaredMethod(name, Mapper.EntityClass, float.class);
                        Mapper.renderParticles = m;
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                    MethodVisitor mv = super.visitMethod(acc, name, desc, sig, ex);
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitCode() {
                            super.visitCode();

                            super.visitTypeInsn(Opcodes.NEW, "wentra/utils/mapper/transformers/etc/EffectRendererHelper");
                            super.visitInsn(Opcodes.DUP);
                            super.visitMethodInsn(Opcodes.INVOKESPECIAL, "wentra/utils/mapper/transformers/etc/EffectRendererHelper", "<init>", "()V", false);

                            super.visitVarInsn(Opcodes.ALOAD, 1);
                            super.visitVarInsn(Opcodes.FLOAD, 2);

                            super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "wentra/utils/mapper/transformers/etc/EffectRendererHelper", "Hook", "(Ljava/lang/Object;F)V", false);
                        }
                    };
                }

                return super.visitMethod(acc, name, desc, sig, ex);
            }
        };

        new ClassReader(classBytes).accept(cv, 0);
        return cw.toByteArray();
    }
}
