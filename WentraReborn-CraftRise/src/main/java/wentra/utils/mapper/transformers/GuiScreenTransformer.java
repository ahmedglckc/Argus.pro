package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.objectweb.asm.Opcodes.*;

public class GuiScreenTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.GuiScreen;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] exc) {
                MethodVisitor mv = super.visitMethod(acc, name, desc, sig, exc);

                boolean match = name.equals(Mapper.mouseClicked.getName()) && desc.equals("(III)V") ||
                        name.equals(Mapper.mouseReleased.getName()) && desc.equals("(III)V") ||
                        name.equals(Mapper.drawScreen.getName()) && desc.equals("(IIF)V") ||
                        name.equals(Mapper.keyTyped.getName()) && desc.equals("(CI)V");

                if (!match) return mv;

                return new MethodVisitor(ASM9, mv) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        injectHook(name, desc);
                    }

                    @Override
                    public void visitInsn(int opcode) {
                        if (opcode >= IRETURN && opcode <= RETURN)
                            injectHook(name, desc);
                        super.visitInsn(opcode);
                    }

                    private void injectHook(String n, String d) {
                        visitVarInsn(ALOAD, 0);
                        if (n.equals(Mapper.drawScreen.getName())) {
                            visitVarInsn(ILOAD, 1);
                            visitVarInsn(ILOAD, 2);
                            visitVarInsn(FLOAD, 3);
                            visitMethodInsn(INVOKESTATIC,
                                    "wentra/utils/mapper/transformers/etc/GuiScreenHelper",
                                    "onDrawScreen",
                                    "(L" + Mapper.GuiScreen.getName().replace('.', '/') + ";IIF)V",
                                    false);
                        } else if (n.equals(Mapper.mouseClicked.getName())) {
                            visitVarInsn(ILOAD, 1);
                            visitVarInsn(ILOAD, 2);
                            visitVarInsn(ILOAD, 3);
                            visitMethodInsn(INVOKESTATIC,
                                    "wentra/utils/mapper/transformers/etc/GuiScreenHelper",
                                    "onMouseClick",
                                    "(L" + Mapper.GuiScreen.getName().replace('.', '/') + ";III)V",
                                    false);
                        } else if (n.equals(Mapper.mouseReleased.getName())) {
                            visitVarInsn(ILOAD, 1);
                            visitVarInsn(ILOAD, 2);
                            visitVarInsn(ILOAD, 3);
                            visitMethodInsn(INVOKESTATIC,
                                    "wentra/utils/mapper/transformers/etc/GuiScreenHelper",
                                    "onMouseRelease",
                                    "(L" + Mapper.GuiScreen.getName().replace('.', '/') + ";III)V",
                                    false);
                        } else if (n.equals(Mapper.keyTyped.getName())) {
                            visitVarInsn(ILOAD, 1);
                            visitMethodInsn(INVOKESTATIC,
                                    "wentra/utils/mapper/transformers/etc/GuiScreenHelper",
                                    "onKey",
                                    "(L" + Mapper.GuiScreen.getName().replace('.', '/') + ";C)V",
                                    false);
                        }
                    }
                };
            }
        };

        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        byte[] b = cw.toByteArray();


        try {
            File file = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\GuiScreen_Transformed.class");
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), b);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }
}
