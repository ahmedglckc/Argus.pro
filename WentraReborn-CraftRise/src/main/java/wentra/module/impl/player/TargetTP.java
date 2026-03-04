package wentra.module.impl.player;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;

import java.lang.reflect.Field;

public class TargetTP extends Module {
    int tries = 0;
    private long lastTp, lastHurt;
    private static final long TP_DELAY = 31, HURT_DELAY = 1500;

    public TargetTP() {
        super("RiseAntiCheat", ModuleCategory.PLAYER, 0);
    }

    @Subscribe
    public void onRender2DEvent(RenderEvent partialTicks) {
        if (!toggled) return;

        if (System.currentTimeMillis() - lastHurt >= HURT_DELAY) {
            Entity.setPositionAndRotation(Entity.getPosX(), Entity.getPosY() + 1, Entity.getPosZ(), Entity.getRotationYaw(), Entity.getRotationPitch());
            lastHurt = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - lastTp >= TP_DELAY) {
            if (Entity.getTicksExisted() % 3 == 0) {
                double[] pos = getNearestValidTargetPos();
                if (pos != null) {
                    Entity.setPositionAndRotation(pos[0], pos[1], pos[2], Entity.getRotationYaw(), Entity.getRotationPitch());
                    Entity.resetMotion(true);
                    tries++;
                    Entity.addChatMessage(String.valueOf(tries));
                } else {
                    Entity.addChatMessage("No valid target found");
                }
            }
            lastTp = System.currentTimeMillis();
        }
    }

    private double[] getNearestValidTargetPos() {
        double minDist = Double.MAX_VALUE;
        double[] best = null;

        for (Object p : Entity.getPlayerEntitiesInWorld()) {
            if (isBot(p)) continue;
            if (isNpc(p)) continue;

            double dx = Entity.getPosX(p) - Entity.getPosX();
            double dz = Entity.getPosZ(p) - Entity.getPosZ();
            double dist = dx * dx + dz * dz;

            if (dist < minDist) {
                minDist = dist;
                best = new double[]{Entity.getPosX(p), Entity.getPosY(p), Entity.getPosZ(p)};
            }
        }

        return best;
    }

    private boolean isNpc(Object ent) {
        try {
            String name = Entity.getName(ent);
            return name.contains("[CR]") || name.equals("[CR]");
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isBot(Object ent) {
        try {
            float hp = Entity.getEntityHealth(ent);
            int id = Entity.getId(ent);
            return hp == 1.0f && id != 8;
        } catch (Exception e) {
            return false;
        }
    }
}
