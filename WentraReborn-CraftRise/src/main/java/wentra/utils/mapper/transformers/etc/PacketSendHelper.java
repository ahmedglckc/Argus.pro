package wentra.utils.mapper.transformers.etc;

public class PacketSendHelper {

    public static boolean cancelled = false;

    public static void sendPacketCallBack(Object packet) {
        cancelled = false;
    }
}