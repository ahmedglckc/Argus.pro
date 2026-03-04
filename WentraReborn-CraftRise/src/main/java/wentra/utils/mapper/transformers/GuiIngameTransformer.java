package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;

import wentra.utils.mapper.transformers.etc.Render2DHelper;
import wentra.utils.mapper.Mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GuiIngameTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.GuiIngame;
    }
    
    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM6, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (descriptor.equals("(F)V")) {
                    return new MethodVisitor(Opcodes.ASM6, methodVisitor) {
                        @Override
                        public void visitInsn(int opcode) {
                            if (opcode == Opcodes.RETURN) {
                                visitVarInsn(Opcodes.FLOAD, 1);
                                String methodName = Render2DHelper.class.getDeclaredMethods()[0].getName();
                                visitMethodInsn(Opcodes.INVOKESTATIC,
                                        Render2DHelper.class.getName().replace(".", "/"),
                                        methodName,
                                        "(F)V",
                                        false);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }
                return methodVisitor;
            }
        };

        cr.accept(cv, 0);
        byte[] transformed = cw.toByteArray();

        try {
            File file = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\GuiInGame_Transformed.class");
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), transformed);
        } catch (IOException e) {
        }

        return transformed;
    }
}
