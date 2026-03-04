package wentra.utils.render.glutils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class TextureUtil {
    private static final IntBuffer imageDataBuffer = GLAllocation.createDirectIntBuffer(4194304);
    private static int[] imageDataArray = new int[4194304];

    public static int generateTextureID() {
        return GL11.glGenTextures();
    }

    public static int uploadTextureImageAllocate(int p_110989_0_, BufferedImage p_110989_1_, boolean p_110989_2_, boolean p_110989_3_) {
        allocateTexture(p_110989_0_, p_110989_1_.getWidth(), p_110989_1_.getHeight());
        return uploadTextureImageSub(p_110989_0_, p_110989_1_, 0, 0, p_110989_2_, p_110989_3_);
    }

    public static void uploadTexture(int textureId, int[] pixels, int width, int height) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        uploadTextureSub(0, pixels, width, height, 0, 0, false, false, false);
    }

    private static void uploadTextureSub(int level, int[] pixels, int width, int height, int xOffset, int yOffset, boolean blur, boolean clamp, boolean mipmap) {
        int maxRows = 4194304 / width;
        setTextureBlurMipmap(blur, mipmap);
        setTextureClamp(clamp);
        for (int i = 0; i < width * height; i += width * maxRows) {
            int rowOffset = i / width;
            int rows = Math.min(maxRows, height - rowOffset);
            System.arraycopy(pixels, i, imageDataArray, 0, width * rows);
            imageDataBuffer.clear();
            imageDataBuffer.put(imageDataArray, 0, width * rows);
            imageDataBuffer.position(0).limit(width * rows);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, level, xOffset, yOffset + rowOffset, width, rows, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, imageDataBuffer);
        }
    }

    public static void uploadTextureMipmap(int[][] mipmapData, int width, int height, int xOffset, int yOffset, boolean blur, boolean clamp) {
        for (int level = 0; level < mipmapData.length; ++level) {
            uploadTextureSub(level, mipmapData[level], width >> level, height >> level, xOffset >> level, yOffset >> level, blur, clamp, mipmapData.length > 1);
        }
    }

    public static void allocateTexture(int textureId, int width, int height) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer) null);
    }

    public static int uploadTextureImageSub(int textureId, BufferedImage image, int xOffset, int yOffset, boolean blur, boolean clamp) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        int width = image.getWidth();
        int height = image.getHeight();
        int maxRows = 4194304 / width;
        setTextureBlur(blur);
        setTextureClamp(clamp);

        for (int i = 0; i < width * height; i += width * maxRows) {
            int rowOffset = i / width;
            int rows = Math.min(maxRows, height - rowOffset);
            image.getRGB(0, rowOffset, width, rows, imageDataArray, 0, width);
            imageDataBuffer.clear();
            imageDataBuffer.put(imageDataArray, 0, width * rows);
            imageDataBuffer.position(0).limit(width * rows);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, xOffset, yOffset + rowOffset, width, rows, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, imageDataBuffer);
        }

        return textureId;
    }

    private static void setTextureClamp(boolean clamp) {
        int mode = clamp ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, mode);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, mode);
    }

    private static void setTextureBlur(boolean blur) {
        setTextureBlurMipmap(blur, false);
    }

    private static void setTextureBlurMipmap(boolean blur, boolean mipmap) {
        int min = blur ? (mipmap ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR) : (mipmap ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_NEAREST);
        int mag = blur ? GL11.GL_LINEAR : GL11.GL_NEAREST;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, min);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mag);
    }
}
