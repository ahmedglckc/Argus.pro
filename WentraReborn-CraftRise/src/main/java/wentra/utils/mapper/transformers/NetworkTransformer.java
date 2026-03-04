    package wentra.utils.mapper.transformers;

    import io.netty.channel.ChannelHandlerContext;
    import org.objectweb.asm.*;

    import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;
    import wentra.utils.mapper.transformers.etc.NetworkHelper;
    import wentra.utils.mapper.transformers.etc.PacketSendHelper;
    import wentra.utils.mapper.Mapper;
    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Paths;


    public class NetworkTransformer {

        public static Class<?> getTargetClass() {
            return Mapper.NetworkManager;
        }

        public static byte[] transform(byte[] classBytes) {
            String channel_read0_descriptor = Reflector.getDescriptor(getTargetClass(), ChannelHandlerContext.class, Mapper.PacketClass);
            String send_packet_descriptor = Reflector.getDescriptor(getTargetClass(), Mapper.PacketClass);
            ClassReader classReader = new ClassReader(classBytes);
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            classReader.accept(new ClassVisitor(Opcodes.ASM6, classWriter) {
                @Override
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    if (descriptor.equals(channel_read0_descriptor)) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        return new MethodVisitor(Opcodes.ASM6, mv) {
                            @Override
                            public void visitCode() {
                                mv.visitVarInsn(Opcodes.ALOAD, 2);
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");

                                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        NetworkHelper.class.getName().replace(".", "/"),
                                        NetworkHelper.class.getDeclaredMethods()[0].getName(),
                                        "(Ljava/lang/Object;)V",
                                        false);

                                Label continueLabel = new Label();
                                mv.visitFieldInsn(Opcodes.GETSTATIC,
                                        NetworkHelper.class.getName().replace(".", "/"),
                                        "cancelled",
                                        "Z");
                                mv.visitJumpInsn(Opcodes.IFEQ, continueLabel);
                                mv.visitInsn(Opcodes.RETURN);
                                mv.visitLabel(continueLabel);
                                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            }
                        };
                    }

                    if (descriptor.equals(send_packet_descriptor)) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        return new MethodVisitor(Opcodes.ASM6, mv) {
                            @Override
                            public void visitCode() {
                                mv.visitVarInsn(Opcodes.ALOAD, 1);
                                mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object");
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                                        PacketSendHelper.class.getName().replace(".", "/"),
                                        "sendPacketCallBack",
                                        "(Ljava/lang/Object;)V",
                                        false);
                                Label continueLabel = new Label();
                                mv.visitFieldInsn(Opcodes.GETSTATIC,
                                        PacketSendHelper.class.getName().replace(".", "/"),
                                        "cancelled",
                                        "Z");
                                mv.visitJumpInsn(Opcodes.IFEQ, continueLabel);
                                mv.visitInsn(Opcodes.RETURN);
                                mv.visitLabel(continueLabel);
                                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                            }
                        };
                    }

                    return super.visitMethod(access, name, descriptor, signature, exceptions);
                }
            }, 0);
            byte[] transformedBytes = classWriter.toByteArray();
            try {
                File file = new File("C:\\Users\\" + System.getenv("username") + "\\AppData\\Roaming\\.craftrise\\libraries\\NetworkManager_Transformed.class");
                file.getParentFile().mkdirs();

                Files.write(Paths.get(file.getAbsolutePath()), transformedBytes);
            } catch (IOException e) {
            }
            return transformedBytes;
        }
    }