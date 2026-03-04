package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FloatContainerTransformer {

    private static boolean isTargetMethod = false;
    private static boolean containsSystemOut = false;
    private static String targetMethodName = null;

    public static Class<?> getTargetClass() {
        return Mapper.FloatContainer;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader classReader = new ClassReader(classBytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        classReader.accept(new ClassVisitor(Opcodes.ASM9, classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if ((access & Opcodes.ACC_PUBLIC) != 0 && descriptor.equals("()F")) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitCode() {
                            isTargetMethod = true;
                            containsSystemOut = false;
                            targetMethodName = name;
                            super.visitCode();
                        }

                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            if (owner.equals("java/io/PrintStream") && name.equals("println")) {
                                containsSystemOut = true;
                            }
                            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                        }

                        @Override
                        public void visitEnd() {
                            if (isTargetMethod && containsSystemOut) {
                                try {
                                    Class<?> floatContainerClass = getTargetClass();
                                    if (floatContainerClass != null) {
                                        Method method = floatContainerClass.getDeclaredMethod(targetMethodName);
                                        Mapper.getValueFloat = method;
                                        Mapper.FloatContainer = method.getDeclaringClass();
                                    }
                                } catch (NoSuchMethodException e) {
                                    System.err.println("[WENTRA] Method not found --> " + targetMethodName);
                                }
                            }
                            isTargetMethod = false;
                            super.visitEnd();
                        }
                    };
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        }, 0);

        byte[] b = classWriter.toByteArray();

        try {
            File file = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\FloatContainer_Transformed.class");
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), b);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }
}