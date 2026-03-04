package wentra.utils.render.notification;

import wentra.utils.render.animations.Direction;
import wentra.utils.render.font.FontUtil;
import wentra.utils.render.glutils.ScaledResolution;

import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {
    private static float toggleTime = 2;
    private static final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public static void post(Notification.NotificationType type, String title, String description) {
        post(new Notification(type, title, description));
    }

    public static void post(Notification.NotificationType type, String title, String description, float time) {
        post(new Notification(type, title, description, time));
    }

    private static void post(Notification notification) {
        notifications.add(notification);
    }

    public static float getToggleTime() {
        return toggleTime;
    }

    public static float setToggleTime(float toggleTime) {
        return NotificationManager.toggleTime = toggleTime;
    }

    public static void render() {
        float yOffset = 10;
        int notificationHeight = 25;
        ScaledResolution sr = new ScaledResolution();

        NotificationManager.setToggleTime(3);

        for (Notification notification : new CopyOnWriteArrayList<>(notifications)) {
            boolean expired = notification.getTimerUtil().hasTimeElapsed((long) notification.getTime());

            notification.getAnimation().update();

            if (expired && notification.getAnimation().isDone()) {
                notifications.remove(notification);
                continue;
            }

            float notificationWidth = Math.max(FontUtil.getStringWidth(notification.getTitle()), FontUtil.getStringWidth(notification.getDescription())) + 30;
            float baseX = sr.getScaledWidth() - notificationWidth - 10;
            Double progress = notification.getAnimation().getOutput();

            if (expired) {
                notification.getAnimation().setDirection(Direction.BACKWARDS);
            }

            float x = (float) (baseX + (notificationWidth + 10) * (1 - progress));
            float y = sr.getScaledHeight() - yOffset - notificationHeight;

            String mode = wentra.module.impl.render.Notifications.mode.getValue();
            if ("Exhi".equals(mode)) {
                notification.drawExhi(x, y, notificationWidth, notificationHeight);
            } else if ("Wentra".equals(mode)) {
                notification.drawWentra(x, y, notificationWidth, notificationHeight);
            }

            yOffset += notificationHeight + 5;
        }
    }



    private static CopyOnWriteArrayList<Notification> getNotifications() {
        return notifications;
    }
}
