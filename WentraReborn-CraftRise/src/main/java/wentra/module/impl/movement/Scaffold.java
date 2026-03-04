package wentra.module.impl.movement;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.TickEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Scaffold extends Module {
    private final String[] validBlockNames = {
            "wood", "stone", "brick", "cloth", "planks",
            "hardened_clay", "gravel", "sand", "end_stone"
    };
    private int lastSlot = -1;

    public Scaffold() {
        super("Scaffold", ModuleCategory.MOVEMENT, Keyboard.KEY_B);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        try {
            // Get player and world instances
            Object thePlayer = Mapper.getThePlayer.invoke(null);
            Object theWorld = Mapper.worldObj.get(thePlayer);

            // Get player position
            double x = Mapper.posX.getDouble(thePlayer);
            double y = Mapper.posY.getDouble(thePlayer) - 1;
            double z = Mapper.posZ.getDouble(thePlayer);

            // Create base BlockPos
            Object base = createBlockPos(x, y, z);
            if (base == null) {
                Mapper.log("[Scaffold] Failed to create base BlockPos");
                return;
            }

            // Define offsets and facings
            Object[] offs = {
                    createBlockPosOffset(base, 0, -1, 0), createBlockPosOffset(base, -1, 0, 0),
                    createBlockPosOffset(base, 1, 0, 0), createBlockPosOffset(base, 0, 0, -1),
                    createBlockPosOffset(base, 0, 0, 1), createBlockPosOffset(base, -1, 0, -1),
                    createBlockPosOffset(base, -1, 0, 1), createBlockPosOffset(base, 1, 0, -1),
                    createBlockPosOffset(base, 1, 0, 1)
            };
            Object[] facings = getEnumFacingValues(new String[]{
                    "UP", "EAST", "WEST", "SOUTH", "NORTH",
                    "SOUTH", "NORTH", "SOUTH", "NORTH"
            });

            for (int i = 0; i < offs.length; i++) {
                Object target = offs[i];
                if (target == null) continue;

                Object vec = createVec3(
                        getBlockPosX(target) + 0.5,
                        getBlockPosY(target) + 0.5,
                        getBlockPosZ(target) + 0.5
                );
                if (vec == null) continue;

                Object held = Mapper.getHeldItem.invoke(thePlayer);
                if (held == null) continue;

                Object facing = facings[i];
                if (facing == null) continue;

                Mapper.onPlayerRightClick.invoke(
                        Mapper.playerController.get(null),
                        thePlayer, theWorld, held,
                        base, facing, vec
                );
                break;
            }
        } catch (Exception ex) {
            Mapper.log("[Scaffold] Error in onTick: " + ex.getMessage());
        }
    }

    private Object createBlockPos(double x, double y, double z) {
        try {
            Constructor<?> constructor = Mapper.BlockPos.getConstructor(double.class, double.class, double.class);
            return constructor.newInstance(x, y, z);
        } catch (Exception e) {
            Mapper.log("[Scaffold] Failed to create BlockPos: " + e.getMessage());
            return null;
        }
    }

    private Object createBlockPosOffset(Object base, int xOff, int yOff, int zOff) {
        try {
            Method addMethod = Mapper.BlockPos.getMethod("add", int.class, int.class, int.class);
            return addMethod.invoke(base, xOff, yOff, zOff);
        } catch (Exception e) {
            Mapper.log("[Scaffold] Failed to create BlockPos offset: " + e.getMessage());
            return null;
        }
    }

    private Object createVec3(double x, double y, double z) {
        try {
            Constructor<?> constructor = Mapper.Vec3.getConstructor(double.class, double.class, double.class);
            return constructor.newInstance(x, y, z);
        } catch (Exception e) {
            Mapper.log("[Scaffold] Failed to create Vec3: " + e.getMessage());
            return null;
        }
    }

    private double getBlockPosX(Object blockPos) {
        try {
            Field xField = Mapper.BlockPos.getField("x");
            xField.setAccessible(true);
            return xField.getInt(blockPos);
        } catch (Exception e) {
            Mapper.log("[Scaffold] Failed to get BlockPos X: " + e.getMessage());
            return 0.0;
        }
    }

    private double getBlockPosY(Object blockPos) {
        try {
            Field yField = Mapper.BlockPos.getField("y");
            yField.setAccessible(true);
            return yField.getInt(blockPos);
        } catch (Exception e) {
            Mapper.log("[Scaffold] Failed to get BlockPos Y: " + e.getMessage());
            return 0.0;
        }
    }

    private double getBlockPosZ(Object blockPos) {
        try {
            Field zField = Mapper.BlockPos.getField("z");
            zField.setAccessible(true);
            return zField.getInt(blockPos);
        } catch (Exception e) {
            Mapper.log("[Scaffold] Failed to get BlockPos Z: " + e.getMessage());
            return 0.0;
        }
    }

    private Object[] getEnumFacingValues(String[] names) {
        Object[] facings = new Object[names.length];
        try {
            Field[] fields = Mapper.EnumFacing.getDeclaredFields();
            for (int i = 0; i < names.length; i++) {
                for (Field field : fields) {
                    if (field.getName().equals(names[i]) && Modifier.isStatic(field.getModifiers())) {
                        field.setAccessible(true);
                        facings[i] = field.get(null);
                        break;
                    }
                }
                if (facings[i] == null) {
                    Mapper.log("[Scaffold] EnumFacing value not found: " + names[i]);
                }
            }
        } catch (Exception e) {
            Mapper.log("[Scaffold] Error getting EnumFacing values: " + e.getMessage());
        }
        return facings;
    }
}