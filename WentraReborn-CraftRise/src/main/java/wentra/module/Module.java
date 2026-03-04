package wentra.module;

import wentra.event.Event;
import wentra.event.EventBus;
import wentra.event.impl.EventRender3D;
import wentra.module.setting.ModuleCategory;
import wentra.module.setting.Setting;
import wentra.utils.SoundUtils;
import wentra.utils.mapper.Entity;
import wentra.utils.render.animations.Animation;
import wentra.utils.render.animations.impl.DecelerateAnimation;
import wentra.utils.render.notification.Notification;
import wentra.utils.render.notification.NotificationManager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static wentra.module.ModuleManager.listAllModules;

public abstract class Module {
    public final Animation animation = new DecelerateAnimation(250, 1.0);
    public final List<Setting> settings = new ArrayList<>();
    public ModuleCategory cat;
    public String name;
    public int key;
    public boolean toggled = false;
    public boolean keyIsPressed = false;
    private String tag = "";
    private Clip clip;
    private FloatControl volume;

    public Module(String name, ModuleCategory cat, int key) {
        this.name = name;
        this.cat = cat;
        this.key = key;
    }

    public boolean isToggled() {
        return toggled;
    }

    public String getName() {
        return name;
    }

    public boolean hasMode() {
        return tag != null;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void onEnable() {}

    public void onDisable() {}

    public void onRender2DEvent() {}

    public void onRender2DEvent(float partialTicks) {}

    public void onRender3DEvent(float partialTicks) {}

    public void onRender3DEvent(EventRender3D event) {}

    public void onRender2DEvent(Event event) {}

    public void onRenderParticlesEvent(float partialTicks) {}

    public void toggle() {
        toggled = !toggled;
        String title = "Module Toggled";
        String stateMsg = toggled ? "§aenabled" : "§cdisabled";
        String notif = name + " was " + stateMsg;
        String mode = wentra.module.impl.render.Notifications.mode.getValue();

        if (!name.equals("ClickTP") && !name.equals("UpClip") && !name.equals("DownClip")) {
            Notification.NotificationType type = toggled ? Notification.NotificationType.SUCCESS : Notification.NotificationType.DISABLE;
            try {
                SoundUtils.playSound(toggled ? "enable.wav" : "disable.wav", 0.3f);
                if ("Wentra".equals(mode)) {
                    NotificationManager.post(type, notif, "");
                } else {
                    NotificationManager.post(type, title, notif);
                }
                Entity.addChatMessage(notif, true);
            } catch (Exception e) {
                System.err.println("Error posting notification for " + name + ": " + e.getMessage());
            }
        }

        try {
            if (toggled) {
                onEnable();
                EventBus.subscribe(this);
            } else {
                onDisable();
                EventBus.unSubscribe(this);
            }
        } catch (Exception e) {
            System.err.println("Error toggling module " + name + ": " + e.getMessage());
            toggled = !toggled;
        }
    }

    public static void render2DEvent() {
        for (Module m : listAllModules()) {
            try {
                m.onRender2DEvent();
            } catch (Exception e) {
                System.err.println("Error in render2DEvent for " + m.getName() + ": " + e.getMessage());
            }
        }
    }

    public static void preRender2DEvent(float pt) {
        for (Module m : listAllModules()) {
            try {
                m.onRender2DEvent(pt);
            } catch (Exception e) {
                System.err.println("Error in preRender2DEvent for " + m.getName() + ": " + e.getMessage());
            }
        }
    }

    public static void renderParticlesEvent(float pt) {
        for (Module m : listAllModules()) {
            try {
                m.onRender3DEvent(pt);
            } catch (Exception e) {
                System.err.println("Error in renderParticlesEvent for " + m.getName() + ": " + e.getMessage());
            }
        }
    }
}