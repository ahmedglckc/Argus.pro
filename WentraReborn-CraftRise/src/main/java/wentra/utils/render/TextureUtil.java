package wentra.utils.render;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;
import wentra.utils.render.glutils.GLAllocation;
import wentra.utils.render.glutils.GlStateManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.IntBuffer;

public class TextureUtil {
    private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
    private static int[] dataArray = new int[4194304];


    public static void allocateTexture(int p_110991_0_, int p_110991_1_, int p_110991_2_) throws InvocationTargetException, IllegalAccessException {
        allocateTextureImpl(p_110991_0_, 0, p_110991_1_, p_110991_2_);
    }

    public static int glGenTextures() {
        return GL11.glGenTextures();
    }

    public static void allocateTextureImpl(int p_180600_0_, int p_180600_1_, int p_180600_2_, int p_180600_3_) throws InvocationTargetException, IllegalAccessException {
        Object object = TextureUtil.class;

        synchronized (object) {
            GL11.glDeleteTextures(p_180600_0_);
            bindTexture(p_180600_0_);
        }

        if (p_180600_1_ >= 0) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, p_180600_1_);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MIN_LOD, 0.0F);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LOD, (float) p_180600_1_);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0.0F);
        }

        for (int i = 0; i <= p_180600_1_; ++i) {
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, GL11.GL_RGBA, p_180600_2_ >> i, p_180600_3_ >> i, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) ((IntBuffer) null));
        }
    }

    public static int uploadTextureImageAllocate(int p_110989_0_, BufferedImage p_110989_1_, boolean p_110989_2_, boolean p_110989_3_) throws InvocationTargetException, IllegalAccessException {
        allocateTexture(p_110989_0_, p_110989_1_.getWidth(), p_110989_1_.getHeight());
        return uploadTextureImageSub(p_110989_0_, p_110989_1_, 0, 0, p_110989_2_, p_110989_3_);
    }
    public static void uploadTexture(int textureId, int[] p_110988_1_, int p_110988_2_, int p_110988_3_) throws InvocationTargetException, IllegalAccessException {
        bindTexture(textureId);
        uploadTextureSub(0, p_110988_1_, p_110988_2_, p_110988_3_, 0, 0, false, false, false);
    }

    private static int blendColorComponent(int p_147944_0_, int p_147944_1_, int p_147944_2_, int p_147944_3_, int p_147944_4_) {
        float f = (float) Math.pow((double) ((float) (p_147944_0_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float f1 = (float) Math.pow((double) ((float) (p_147944_1_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float f2 = (float) Math.pow((double) ((float) (p_147944_2_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float f3 = (float) Math.pow((double) ((float) (p_147944_3_ >> p_147944_4_ & 255) / 255.0F), 2.2D);
        float f4 = (float) Math.pow((double) (f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D);
        return (int) ((double) f4 * 255.0D);
    }

    public static void uploadTextureMipmap(int[][] p_147955_0_, int p_147955_1_, int p_147955_2_, int p_147955_3_, int p_147955_4_, boolean p_147955_5_, boolean p_147955_6_) {
        for (int i = 0; i < p_147955_0_.length; ++i) {
            int[] aint = p_147955_0_[i];
            uploadTextureSub(i, aint, p_147955_1_ >> i, p_147955_2_ >> i, p_147955_3_ >> i, p_147955_4_ >> i, p_147955_5_, p_147955_6_, p_147955_0_.length > 1);
        }
    }

    private static void uploadTextureSub(int p_147947_0_, int[] p_147947_1_, int p_147947_2_, int p_147947_3_, int p_147947_4_, int p_147947_5_, boolean p_147947_6_, boolean p_147947_7_, boolean p_147947_8_) {
        int i = 4194304 / p_147947_2_;
        setTextureBlurMipmap(p_147947_6_, p_147947_8_);
        setTextureClamped(p_147947_7_);
        int j;

        for (int k = 0; k < p_147947_2_ * p_147947_3_; k += p_147947_2_ * j) {
            int l = k / p_147947_2_;
            j = Math.min(i, p_147947_3_ - l);
            int i1 = p_147947_2_ * j;
            copyToBufferPos(p_147947_1_, k, i1);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, p_147947_0_, p_147947_4_, p_147947_5_ + l, p_147947_2_, j, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) dataBuffer);
        }
    }

    public static int uploadTextureImageSub(int textureId, BufferedImage p_110995_1_, int p_110995_2_, int p_110995_3_, boolean p_110995_4_, boolean p_110995_5_) throws InvocationTargetException, IllegalAccessException {
        bindTexture(textureId);
        uploadTextureImageSubImpl(p_110995_1_, p_110995_2_, p_110995_3_, p_110995_4_, p_110995_5_);
        return textureId;
    }

    private static void uploadTextureImageSubImpl(BufferedImage p_110993_0_, int p_110993_1_, int p_110993_2_, boolean p_110993_3_, boolean p_110993_4_) {
        int i = p_110993_0_.getWidth();
        int j = p_110993_0_.getHeight();
        int k = 4194304 / i;
        int[] aint = dataArray;
        setTextureBlurred(p_110993_3_);
        setTextureClamped(p_110993_4_);

        for (int l = 0; l < i * j; l += i * k) {
            int i1 = l / i;
            int j1 = Math.min(k, j - i1);
            int k1 = i * j1;
            p_110993_0_.getRGB(0, i1, i, j1, aint, 0, i);
            copyToBuffer(aint, k1);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, p_110993_1_, p_110993_2_ + i1, i, j1, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer) dataBuffer);
        }
    }

    public static void setTextureClamped(boolean p_110997_0_) {
        if (p_110997_0_) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        }
    }

    private static void setTextureBlurred(boolean p_147951_0_) {
        setTextureBlurMipmap(p_147951_0_, false);
    }

    public static void setTextureBlurMipmap(boolean p_147954_0_, boolean p_147954_1_) {
        if (p_147954_0_) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, p_147954_1_ ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        } else {
            int i = 9986;
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, p_147954_1_ ? i : GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }
    }

    private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_) {
        copyToBufferPos(p_110990_0_, 0, p_110990_1_);
    }

    private static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_) {
        int[] aint = p_110994_0_;


        dataBuffer.clear();
        dataBuffer.put(aint, p_110994_1_, p_110994_2_);
        dataBuffer.position(0).limit(p_110994_2_);
    }

    static void bindTexture(int p_94277_0_) throws InvocationTargetException, IllegalAccessException {
        Mapper.bindTexture.invoke(Entity.getTextureManager(), p_94277_0_, 1, 1);
    }


    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
        if (imageStream == null) {
            return null;
        } else {
            BufferedImage bufferedimage;

            try {
                bufferedimage = ImageIO.read(imageStream);
            } finally {
                IOUtils.closeQuietly(imageStream);
            }

            return bufferedimage;
        }
    }

    public static int[] updateAnaglyph(int[] p_110985_0_) {
        int[] aint = new int[p_110985_0_.length];

        for (int i = 0; i < p_110985_0_.length; ++i) {
            aint[i] = anaglyphColor(p_110985_0_[i]);
        }

        return aint;
    }

    public static int anaglyphColor(int p_177054_0_) {
        int i = p_177054_0_ >> 24 & 255;
        int j = p_177054_0_ >> 16 & 255;
        int k = p_177054_0_ >> 8 & 255;
        int l = p_177054_0_ & 255;
        int i1 = (j * 30 + k * 59 + l * 11) / 100;
        int j1 = (j * 30 + k * 70) / 100;
        int k1 = (j * 30 + l * 70) / 100;
        return i << 24 | i1 << 16 | j1 << 8 | k1;
    }
}