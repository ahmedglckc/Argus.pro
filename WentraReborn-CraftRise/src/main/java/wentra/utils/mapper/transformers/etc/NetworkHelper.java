package wentra.utils.mapper.transformers.etc;

import wentra.module.ModuleManager;
import wentra.utils.mapper.Mapper;

public class NetworkHelper {
    public static boolean cancelled = false;

    public static void packetReceiveEvent(Object packet) {
        if (Mapper.S12PacketEntityVelocity.isInstance(packet) && ModuleManager.isEnabled("Velocity")) {
            cancelled = true;
        } else {
            cancelled = false;
        }
    }
}