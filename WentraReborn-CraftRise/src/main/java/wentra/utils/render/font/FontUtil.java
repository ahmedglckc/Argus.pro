package wentra.utils.render.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class FontUtil {
    private static FontUtil INSTANCE;
    private final Map<Character, CharData> charData = new HashMap<>();
    private final Map<Character, CharData> charData2 = new HashMap<>();
    private int fontHeight = -1;
    private int fontHeight2 = -1;
    private int textureID;
    private int textureID2;
    private Font font;
    private Font font2;

    // Minecraft-style color code mapping
    private static final Map<Character, int[]> COLOR_CODES = new HashMap<>();

    static {
        COLOR_CODES.put('0', new int[]{0, 0, 0});       // Black
        COLOR_CODES.put('1', new int[]{0, 0, 170});     // Dark Blue
        COLOR_CODES.put('2', new int[]{0, 170, 0});     // Dark Green
        COLOR_CODES.put('3', new int[]{0, 170, 170});   // Dark Aqua
        COLOR_CODES.put('4', new int[]{170, 0, 0});     // Dark Red
        COLOR_CODES.put('5', new int[]{170, 0, 170});   // Dark Purple
        COLOR_CODES.put('6', new int[]{255, 170, 0});   // Gold
        COLOR_CODES.put('7', new int[]{170, 170, 170}); // Gray
        COLOR_CODES.put('8', new int[]{85, 85, 85});    // Dark Gray
        COLOR_CODES.put('9', new int[]{85, 85, 255});   // Blue
        COLOR_CODES.put('a', new int[]{85, 255, 85});   // Green
        COLOR_CODES.put('b', new int[]{85, 255, 255});  // Aqua
        COLOR_CODES.put('c', new int[]{255, 85, 85});   // Red
        COLOR_CODES.put('d', new int[]{255, 85, 255});  // Light Purple
        COLOR_CODES.put('e', new int[]{255, 255, 85});  // Yellow
        COLOR_CODES.put('f', new int[]{255, 255, 255}); // White
        COLOR_CODES.put('r', new int[]{255, 255, 255}); // Reset to default (white)
    }

    public FontUtil() {
        INSTANCE = this;
        try {
            InputStream inputStream = FontUtil.class.getResourceAsStream("/assets/minecraft/wentra/wentra1.ttf");
            InputStream inputStream2 = FontUtil.class.getResourceAsStream("/assets/minecraft/wentra/icon.ttf");
            if (inputStream != null) {
                this.font = Font.createFont(0, inputStream).deriveFont(0, 20.0f);
                inputStream.close();
            } else {
                this.font = new Font("Arial", 0, 20);
            }
            if (inputStream2 != null) {
                this.font2 = Font.createFont(0, inputStream2).deriveFont(0, 30.0f);
                inputStream2.close();
            } else {
                this.font2 = new Font("Arial", 0, 30);
            }
            this.generateFontImage();
            this.generateFontImage2();
        } catch (Exception e) {
            this.font = new Font("Arial", 0, 21);
            this.font2 = new Font("Arial", 0, 30);
            this.generateFontImage();
            this.generateFontImage2();
        }
    }

    private void generateFontImage() {
        BufferedImage image = new BufferedImage(512, 512, 2);
        Graphics2D g = image.createGraphics();
        g.setFont(this.font);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics metrics = g.getFontMetrics();
        this.fontHeight = metrics.getHeight();
        int x = 0;
        int y = 0;
        for (int i = 0; i < 256; ++i) {
            char ch = (char) i;
            int charWidth = Math.max(1, metrics.charWidth(ch));
            int charHeight = Math.max(1, metrics.getHeight());
            if (x + charWidth >= 512) {
                x = 0;
                if ((y += charHeight) + charHeight >= 512) break;
            }
            g.setColor(new Color(255, 255, 255, 0));
            g.fillRect(x, y, charWidth, charHeight);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(ch), x, y + metrics.getAscent());
            this.charData.put(ch, new CharData(x, y, charWidth, charHeight));
            x += charWidth;
        }
        g.dispose();
        this.textureID = this.uploadTexture(image);
    }

    private void generateFontImage2() {
        BufferedImage image = new BufferedImage(512, 512, 2);
        Graphics2D g = image.createGraphics();
        g.setFont(this.font2);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        FontMetrics metrics = g.getFontMetrics();
        this.fontHeight2 = metrics.getHeight();
        int x = 0;
        int y = 0;
        for (int i = 0; i < 256; ++i) {
            char ch = (char) i;
            int charWidth = Math.max(1, metrics.charWidth(ch));
            int charHeight = Math.max(1, metrics.getHeight());
            if (x + charWidth >= 512) {
                x = 0;
                if ((y += charHeight) + charHeight >= 512) break;
            }
            g.setColor(new Color(255, 255, 255, 0));
            g.fillRect(x, y, charWidth, charHeight);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(ch), x, y + metrics.getAscent());
            this.charData2.put(ch, new CharData(x, y, charWidth, charHeight));
            x += charWidth;
        }
        g.dispose();
        this.textureID2 = this.uploadTexture(image);
    }

    private int uploadTexture(BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int i = 0; i < pixels.length; ++i) {
            int pixel = pixels[i];
            buffer.put((byte) (pixel >> 16 & 0xFF));
            buffer.put((byte) (pixel >> 8 & 0xFF));
            buffer.put((byte) (pixel & 0xFF));
            buffer.put((byte) (pixel >> 24 & 0xFF));
        }
        buffer.flip();
        int textureID = GL11.glGenTextures();
        GL11.glBindTexture(3553, textureID);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexImage2D(3553, 0, 6408, image.getWidth(), image.getHeight(), 0, 6408, 5121, buffer);
        return textureID;
    }

    private static String stripColorCodes(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder cleaned = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '§' && i + 1 < text.length()) {
                i++;
            } else {
                cleaned.append(text.charAt(i));
            }
        }
        return cleaned.toString();
    }

    public static void drawString(String text, float x, float y, int color, boolean shadow) {
        if (text == null) {
            return;
        }
        FontUtil instance = FontUtil.getInstance();
        int previousTexture = GL11.glGetInteger(32873);
        boolean previousBlend = GL11.glGetBoolean(3042);
        boolean previousTexture2D = GL11.glGetBoolean(3553);
        if (shadow) {
            float shadowAlpha = (float) (color >> 24 & 0xFF) / 255.0f * 0.15f;
            int shadowColor = color & 0xFFFFFF | (int) (shadowAlpha * 255.0f) << 24;
            FontUtil.drawStringInternal(stripColorCodes(text), x + 0.5f, y + 0.5f, shadowColor, instance.charData, instance.textureID);
        }
        FontUtil.drawStringInternal(text, x, y, color, instance.charData, instance.textureID); // Pass original text to handle color codes
        GL11.glBindTexture(3553, previousTexture);
        if (!previousBlend) {
            GL11.glDisable(3042);
        }
        if (!previousTexture2D) {
            GL11.glDisable(3553);
        }
    }

    public static void drawStringFont2(String text, float x, float y, int color, boolean shadow) {
        if (text == null) {
            return;
        }
        FontUtil instance = FontUtil.getInstance();
        int previousTexture = GL11.glGetInteger(32873);
        boolean previousBlend = GL11.glGetBoolean(3042);
        boolean previousTexture2D = GL11.glGetBoolean(3553);
        if (shadow) {
            float shadowAlpha = (float) (color >> 24 & 0xFF) / 255.0f * 0.15f;
            int shadowColor = color & 0xFFFFFF | (int) (shadowAlpha * 255.0f) << 24;
            FontUtil.drawStringInternal(stripColorCodes(text), x + 0.5f, y + 0.5f, shadowColor, instance.charData2, instance.textureID2);
        }
        FontUtil.drawStringInternal(text, x, y, color, instance.charData2, instance.textureID2); // Pass original text to handle color codes
        GL11.glBindTexture(3553, previousTexture);
        if (!previousBlend) {
            GL11.glDisable(3042);
        }
        if (!previousTexture2D) {
            GL11.glDisable(3553);
        }
    }

    private static void drawStringInternal(String text, float x, float y, int color, Map<Character, CharData> charDataMap, int textureID) {
        FontUtil instance = FontUtil.getInstance();
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, textureID);
        float defaultRed = (float) (color >> 16 & 0xFF) / 255.0f;
        float defaultGreen = (float) (color >> 8 & 0xFF) / 255.0f;
        float defaultBlue = (float) (color & 0xFF) / 255.0f;
        float defaultAlpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = defaultRed;
        float green = defaultGreen;
        float blue = defaultBlue;
        float alpha = defaultAlpha;
        GL11.glColor4f(red, green, blue, alpha);
        float startX = x;
        GL11.glScalef(0.5f, 0.5f, 1.0f);
        x *= 2.0f;
        y *= 2.0f;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '§' && i + 1 < text.length()) {
                char colorCode = Character.toLowerCase(text.charAt(i + 1));
                int[] rgb = COLOR_CODES.get(colorCode);
                if (rgb != null) {
                    red = rgb[0] / 255.0f;
                    green = rgb[1] / 255.0f;
                    blue = rgb[2] / 255.0f;
                    GL11.glColor4f(red, green, blue, alpha);
                }
                i++; // Skip the color code character
                continue;
            }
            if (ch == '\n') {
                y += (float) instance.fontHeight;
                x = startX * 2.0f;
                continue;
            }
            CharData charData = charDataMap.get(ch);
            if (charData == null) continue;
            float width = charData.width;
            float height = charData.height;
            float textureX = (float) charData.x / 512.0f;
            float textureY = (float) charData.y / 512.0f;
            float textureWidth = width / 512.0f;
            float textureHeight = height / 512.0f;
            GL11.glBegin(7);
            GL11.glTexCoord2f(textureX, textureY);
            GL11.glVertex2f(x, y);
            GL11.glTexCoord2f(textureX, textureY + textureHeight);
            GL11.glVertex2f(x, y + height);
            GL11.glTexCoord2f(textureX + textureWidth, textureY + textureHeight);
            GL11.glVertex2f(x + width, y + height);
            GL11.glTexCoord2f(textureX + textureWidth, textureY);
            GL11.glVertex2f(x + width, y);
            GL11.glEnd();
            x += width;
        }
        GL11.glScalef(2.0f, 2.0f, 1.0f);
        GL11.glPopMatrix();
    }

    public static void drawStringWithShadow(String text, float x, float y, int color) {
        if (text == null) {
            return;
        }
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        int shadowColor = (int) (alpha * 255.0f) << 24 | (int) (red * 0.25f * 255.0f) << 16 | (int) (green * 0.25f * 255.0f) << 8 | (int) (blue * 0.25f * 255.0f);
        FontUtil.drawString(stripColorCodes(text), x + 0.5f, y + 0.5f, shadowColor, false);
        FontUtil.drawString(text, x, y, color, false); // Pass original text to handle color codes
    }

    public static void drawCenteredString(String text, float x, float y, int color) {
        if (text == null) {
            return;
        }
        FontUtil.drawString(text, x - (float) (FontUtil.getStringWidth(stripColorCodes(text)) / 2), y, color, false); // Use original text for rendering
    }

    public static void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        if (text == null) {
            return;
        }
        FontUtil.drawString(text, x - (float) (FontUtil.getStringWidth(stripColorCodes(text)) / 2), y, color, true); // Use original text for rendering
    }

    public static int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }
        FontUtil instance = FontUtil.getInstance();
        int width = 0;
        String cleanedText = stripColorCodes(text);
        for (char ch : cleanedText.toCharArray()) {
            CharData data = instance.charData.get(ch);
            if (data == null) continue;
            width += data.width;
        }
        return width / 2;
    }

    public static int getHeight() {
        return FontUtil.getInstance().fontHeight / 2;
    }

    public static float getMiddleOfBox(float boxHeight) {
        return boxHeight / 2f - getHeight() / 2f;
    }

    public static FontUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FontUtil();
        }
        return INSTANCE;
    }

    private static class CharData {
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public CharData(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}