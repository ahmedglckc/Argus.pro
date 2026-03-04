package wentra.utils.render.notification;


import wentra.utils.render.RenderUtil;
import wentra.utils.render.animations.Animation;
import wentra.utils.render.animations.Direction;
import wentra.utils.render.animations.impl.DecelerateAnimation;
import wentra.utils.render.font.FontChars;
import wentra.utils.render.font.FontUtil;
import wentra.utils.time.TimerUtil;

import java.awt.*;

public class Notification {

    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    private final TimerUtil timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new DecelerateAnimation(250, 1, Direction.FORWARDS);
    }


    public void drawExhi(float x, float y, float width, float height) {
        boolean lowerAlpha = false;
        RenderUtil.drawRect2(x, y, width, height, new Color(0.1F, 0.1F, 0.1F, lowerAlpha ? 0.4F : .75f).getRGB());
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);

        switch (notificationType) {
            case SUCCESS:
                RenderUtil.drawRect2(x + (width * percentage), y + height - 1, width - (width * percentage), 1, new Color(20, 250, 90).getRGB());
                RenderUtil.drawRect2((int) x, (int) y, (int) width, (int) height, new Color(0, 0, 0, 100).getRGB());
                FontUtil.drawStringFont2(FontChars.CHECKMARK, x + 3, (y + FontUtil.getMiddleOfBox(height) + 1), new Color(20, 250, 90).getRGB(), false);
                FontUtil.drawString(getTitle(), x + 7 + FontUtil.getStringWidth(FontChars.CHECKMARK), y + 4, Color.WHITE.getRGB(), false);
                FontUtil.drawString(getDescription(), x + 7 + FontUtil.getStringWidth(FontChars.CHECKMARK), y + 8.5f + FontUtil.getHeight(), Color.WHITE.getRGB(), false);
                break;
            case DISABLE:
                RenderUtil.drawRect2(x + (width * percentage), y + height - 1, width - (width * percentage), 1, new Color(255, 30, 30).getRGB());
                RenderUtil.drawRect2((int) x, (int) y, (int) width, (int) height, new Color(0, 0, 0, 100).getRGB());
                FontUtil.drawStringFont2(FontChars.XMARK, x + 3, (y + FontUtil.getMiddleOfBox(height) + 1), new Color(255, 30, 30).getRGB(), false);
                FontUtil.drawString(getTitle(), x + 7 + FontUtil.getStringWidth(FontChars.XMARK), y + 4, Color.WHITE.getRGB(), false);
                FontUtil.drawString(getDescription(), x + 7 + FontUtil.getStringWidth(FontChars.XMARK), y + 8.5f + FontUtil.getHeight(), Color.WHITE.getRGB(), false);
                break;
        }
    }

    public void drawWentra(float x, float y, float width, float height) {
        boolean lowerAlpha = false;
        RenderUtil.drawRect2(x, y, width, height, new Color(0.1F, 0.1F, 0.1F, lowerAlpha ? 0.4F : .75f).getRGB());
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);

        Color backgroundColor = new Color(0, 0, 0, 100);
        Color barColor = new Color(255, 255, 255);
        Color iconColor = Color.WHITE;

        float iconSize = FontUtil.getHeight();
        float textX = x + iconSize + 10;
        float textY = y + (height / 2) - (FontUtil.getHeight() / 2);

        FontUtil.drawStringFont2(notificationType == NotificationType.INFO ? FontChars.INFO : FontChars.INFO, x + 5, y + (height / 2) + (iconSize / 4) - 11, iconColor.getRGB(), false);

        RenderUtil.drawRect2(x, y, width, height, backgroundColor.getRGB());
        RenderUtil.drawRect2(x + (width * percentage), y + height - 1, width - (width * percentage), 1, barColor.getRGB());

        FontUtil.drawString(getTitle(), textX, textY - 1, Color.WHITE.getRGB(), false);
    }

    float getTime() {
        return time;
    }

    String getDescription() {
        return description;
    }

    String getTitle() {
        return title;
    }

    private NotificationType getNotificationType() {
        return notificationType;
    }

    public Animation getAnimation() {
        return animation;
    }

    public TimerUtil getTimerUtil() {
        return timerUtil;
    }

    public enum NotificationType {
        SUCCESS, INFO, WARNING, DISABLE, ERROR;
    }
}