package wentra;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.stage.Stage;
import wentra.event.EventBus;
import wentra.module.Module;
import wentra.module.ModuleManager;
import wentra.module.impl.combat.KillAura;
import wentra.module.impl.combat.Velocity;
import wentra.module.impl.movement.*;
import wentra.module.impl.player.NoWeb;
import wentra.module.impl.render.*;
import wentra.module.impl.world.ChestStealer;
import wentra.module.impl.world.FastPlace;
import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.Entity;
import javafx.application.Platform;
import javafx.stage.Stage;

import static wentra.utils.mapper.Mapper.log;

public class Main {
    public static Main instance = new Main();

    public static List<Class<?>> jclasses;
    public static Class<?>[] classesR;

    public static void StartClient(List<Class<?>> classes) throws ClassNotFoundException, IOException {
        if (classes == null || classes.isEmpty()) {
            System.err.println("cannot start Mapper");
            return;
        }

        jclasses = classes;
        classesR = classes.toArray(new Class<?>[0]);

        try {
            PrintStream fileOut = new PrintStream(new FileOutputStream("C:\\Wentra\\errors.txt", true));
            System.setErr(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Reflector.initialize(jclasses);
            Mapper.Starter();

            EventBus.subscribe(instance);

            Module[] modules = {
                    new Flight(), new KillAura(), new FastPlace(),
                    new UpClip(), new DownClip(), new ClickTP(),
                    new Velocity(), new Hud(), new Notifications(),
                    new NoWeb(), new NoSwing(), new Strafe(),
                    new Speed(), new NoSlow(), new JumpCircle(),
                    new ESP2D(), new Radar(), new InventoryWalk(),
                    new ChestStealer()
            };

            for (Module m : modules) ModuleManager.addMod(m);

            Entity.addChatMessage("Loaded", true);

            getClasses(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Class> getClasses(boolean filterZelix) throws Exception {
        log("getClasses(): Başladı - filterZelix: " + filterZelix);

        Class<?> classCr;
        try {
            classCr = Class.forName("craftrise.lIlIIlIlllIIlAIbB");
            log("getClasses(): Class bulundu -> " + classCr.getName());
        } catch (ClassNotFoundException e) {
            log("getClasses(): ClassNotFoundException - craftrise.lIlIIlIlllIIlAIbB bulunamadı.");
            throw e;
        }

        Class<?> classCrReal = classCr.getSuperclass();
        log("getClasses(): Superclass -> " + classCrReal.getName());

        Field f;
        try {
            f = classCrReal.getDeclaredField("classes");
            f.setAccessible(true);
            log("getClasses(): Field bulundu ve erişim verildi -> " + f.getName());
        } catch (NoSuchFieldException e) {
            log("getClasses(): Field 'classes' bulunamadı!");
            throw e;
        }

        Vector<Class> allClasses;
        try {
            ClassLoader loader = classCr.getClassLoader();
            log("getClasses(): ClassLoader -> " + (loader != null ? loader.toString() : "null"));
            allClasses = (Vector<Class>) f.get(loader);
            log("getClasses(): ClassLoader'dan class listesi çekildi -> Toplam: " + allClasses.size());
        } catch (Exception e) {
            log("getClasses(): Field değerini alırken hata: " + e.toString());
            throw e;
        }

        if (filterZelix) {
            ArrayList<Class> filtered = new ArrayList<>();
            for (Class aClass : allClasses) {
                if (aClass.getSimpleName().length() < 3) {
                    filtered.add(aClass);
                    log("getClasses(): Filtre eklendi -> " + aClass.getName());
                }
            }
            log("getClasses(): Filtreli sınıf sayısı: " + filtered.size());
            return filtered;
        }

        return new ArrayList<>(allClasses);
    }


    public static void main(String[] args) {
    }
}
