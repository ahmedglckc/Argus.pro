package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;

import wentra.module.impl.combat.KillAura;
import wentra.utils.mapper.Mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class C02PacketTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.C02PacketUseEntity;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM6, classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor original_mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM6, original_mv) {
                    private int writeFloatCallCount = 0;

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        if ("writeFloat".equals(name) && "(F)Lio/netty/buffer/ByteBuf;".equals(descriptor)) {
                            writeFloatCallCount++;
                            if (writeFloatCallCount == KillAura.rng.getNumber()) {
                                super.visitInsn(Opcodes.POP);
                                super.visitLdcInsn(3.0f);
                            }
                        }
                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    }
                };
            }
        };

        ClassReader classReader = new ClassReader(classBytes);
        classReader.accept(classVisitor, 0);
        byte[] transformedClass = classWriter.toByteArray();
        try {
            File file = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\C02PacketUseEntityTrns.class");
            file.getParentFile().mkdirs();

            Files.write(Paths.get(file.getAbsolutePath()), transformedClass);

            //MapperUtils.log("Writed: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] transformedBytes = classWriter.toByteArray();
        return transformedBytes;
    }
}