package wentra.utils;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundUtils {
    public static void playSound(String url, float volume) {
        (new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                InputStream audioSrc = SoundUtils.class.getResourceAsStream("/assets/minecraft/wentra/sounds/" + url);
                BufferedInputStream bufferedIn = new BufferedInputStream(audioSrc);
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
                clip.open(inputStream);

                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                gainControl.setValue(volume);

                clip.start();
            } catch (Exception var6) {
                var6.printStackTrace();
            }

        })).start();
    }
}
