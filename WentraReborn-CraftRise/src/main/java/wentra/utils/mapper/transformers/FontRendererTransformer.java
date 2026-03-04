package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FontRendererTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.FontRenderer;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new ClassVisitor(Opcodes.ASM6, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, sig, exceptions);

                switch (desc) {
                    case "(Ljava/lang/String;)F": // Tek parametre string, float dönüyor
                        return hook(mv, "processFloat");
                    case "(Ljava/lang/String;I)I": // String + int, int dönüyor
                        return hook(mv, "process2Int");
                    case "(Ljava/lang/String;IZ)Ljava/lang/String;": // String + int + boolean, string dönüyor
                        return hook(mv, "processReturnStr");
                    case "(Ljava/lang/String;FFI)I": // String + float + float + int, int dönüyor
                        return hook(mv, "processA4");
                    case "(Ljava/lang/String;FFIZ)I": // String + float + float + int + boolean, int dönüyor
                        return hook(mv, "processA5");
                    case "(Ljava/lang/String;FFIZZ)I": // String + float + float + int + boolean + boolean, int dönüyor (aynı descriptor farklar)
                        return hook(mv, "processB6");
                    case "(Ljava/lang/String;III)I": // String + 3 int, int dönüyor
                        return hook(mv, "process3Int");
                    case "(Ljava/lang/String;IIII)V": // String + 4 int, void dönüyor
                        return hook(mv, "processVoid");
                    default:
                        return mv;
                }
            }

            private MethodVisitor hook(MethodVisitor mv, String helperMethod) {
                return new MethodVisitor(Opcodes.ASM6, mv) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        visitVarInsn(Opcodes.ALOAD, 0);
                        visitVarInsn(Opcodes.ALOAD, 1);
                        visitMethodInsn(Opcodes.INVOKESTATIC,
                                "wentra/utils/mapper/transformers/etc/FontRendererHelper",
                                helperMethod,
                                "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/String;",
                                false);
                        visitVarInsn(Opcodes.ASTORE, 1);
                    }
                };
            }
        };

        cr.accept(cv, 0);
        byte[] transformed = cw.toByteArray();

        try {
            File file = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\FontRenderer_Transformed.class");
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), transformed);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transformed;
    }
}
