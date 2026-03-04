package wentra.utils.mapper.transformers.etc.impl.helpers;

import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Packet {

    public static void addToSendQueue(Object pkt) {
        try {
            Object net = getNetHandler();
            Method m = Mapper.sendPacket;
            m.setAccessible(true);
            m.invoke(net, pkt);
        } catch (Exception e) {
            System.out.println("NetHandler hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Object getNetHandler() {
        try {
            Object p = Entity.getThePlayer();
            Field f = Mapper.sendQueue;
            f.setAccessible(true);
            return f.get(p);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}