package wentra.utils.mapper.transformers.etc;

import java.lang.reflect.Field;

import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;
import wentra.utils.mapper.Mapper;

public class C02Helper {
    private int entity_id;
    private Object c02Instance;

    public C02Helper(int entityID) {
        entity_id = entityID;
        try {
            c02Instance = Mapper.C02PacketUseEntity.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            Mapper.log(e.getMessage());
        }
    }

    private Object getAction() {
        try {
            Class<?> clazz = Mapper.C02PacketUseEntityAction;
            Field attackEnum = clazz.getField("ATTACK");
            attackEnum.setAccessible(true);
            return attackEnum.get(null);
        } catch (Exception e) {
            Mapper.log(e.getMessage());
        }
        return null;
    }

    public Object getPacket() {
        try {
            if (c02Instance == null) {
                c02Instance = Mapper.C02PacketUseEntity.getDeclaredConstructor().newInstance();
            }

            Field entityidField = Reflector.findPrivateIntField(Mapper.C02PacketUseEntity);
            Field c02EntityActionType = Reflector.getFieldByType(Mapper.C02PacketUseEntity, Mapper.C02PacketUseEntityAction);

            entityidField.setAccessible(true);
            c02EntityActionType.setAccessible(true);
            entityidField.set(c02Instance, entity_id);
            c02EntityActionType.set(c02Instance, getAction());
            return c02Instance;
        } catch (Exception e) {
            Mapper.log(e.getMessage());
        }
        return null;
    }
}