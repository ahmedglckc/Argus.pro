package wentra.module.impl.world;

import com.google.common.eventbus.Subscribe;
import org.lwjgl.input.Keyboard;
import wentra.event.impl.RenderEvent;
import wentra.module.Module;
import wentra.module.setting.BooleanSetting;
import wentra.module.setting.DoubleSetting;
import wentra.module.setting.ModuleCategory;
import wentra.utils.mapper.Entity;
import wentra.utils.mapper.Mapper;
import wentra.utils.time.TimerUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChestStealer extends Module {

    private final DoubleSetting delay = new DoubleSetting("Delay", 110, 1, 300);
    private final BooleanSetting randomOrder = new BooleanSetting("Random", true);
    private final BooleanSetting reverseOrder = new BooleanSetting("Reverse", false);
    private final BooleanSetting freeLook = new BooleanSetting("Free Look", true);

    private final TimerUtil timer = new TimerUtil();

    public ChestStealer() {
        super("Stealer", ModuleCategory.WORLD, Keyboard.KEY_I);
        settings.add(delay);
        settings.add(randomOrder);
        settings.add(reverseOrder);
        settings.add(freeLook);
    }

    static boolean isFoundWindowId = false;
    static List<Integer> randomizedFilledSlots = new ArrayList<>();
    static int currentSlotIndex = 0;
    static int lastScreenHash = -1;

    @Subscribe
    public void render(RenderEvent event) {
        Object screen = Entity.getCurrentScreenObject();
        if (screen == null || !isObfuscatedChestGui(screen)) return;

        int currentScreenHash = screen.hashCode();
        if (currentScreenHash != lastScreenHash) {
            randomizedFilledSlots.clear();
            currentSlotIndex = 0;
            lastScreenHash = currentScreenHash;
            isFoundWindowId = false;
            timer.reset();
        }
        try {
            Object container = Mapper.Container_inventorySlots.get(screen);
            Object inventorySlots = Mapper.inventorySlots.get(container);
            List<?> slots = (List<?>) inventorySlots;
            Method getStackMethod = Mapper.getStack;
            Class<?> Container = Mapper.Container;
            int chestSlotCount = slots.size() - 36;

            if (container != null && !isFoundWindowId) {
                for (Field field : Container.getFields()) {
                    if (field.getType() == int.class) {
                        try {
                            int value = field.getInt(container);
                            if (value == 636 || value == -1) {
                                continue;
                            }
                            isFoundWindowId = true;
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (randomizedFilledSlots.isEmpty()) {
                for (int i = 0; i < chestSlotCount; i++) {
                    Object slot = slots.get(i);
                    Object stack = getStackMethod.invoke(slot);
                    if (stack != null) {
                        randomizedFilledSlots.add(i);
                    }
                }

                if (randomOrder.isToggled()) Collections.shuffle(randomizedFilledSlots);
                if (reverseOrder.isToggled()) Collections.reverse(randomizedFilledSlots);
                currentSlotIndex = 0;
            }

            if (!timer.hasTimeElapsed((long) delay.getNumber(), false)) return;

            if (currentSlotIndex < randomizedFilledSlots.size()) {
                int slotId = randomizedFilledSlots.get(currentSlotIndex);
                windowClick(getWindowId(container), slotId, 0, 1);
                currentSlotIndex++;
                timer.reset();
            }

            if (freeLook.isToggled()) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getWindowId(Object container) {
        try {
            Field windowIdField = Mapper.windowId;
            return windowIdField.getInt(container);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Object windowClick(int windowId, int slotId, int mouseButtonClicked, int mode) {
        Object playerController = Entity.getPlayerController();
        try {
            Method windowClick = Mapper.windowClick;
            return windowClick.invoke(playerController, windowId, slotId, mouseButtonClicked, mode, Entity.getThePlayer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isObfuscatedChestGui(Object screen) {
        if (screen == null) return false;

        Constructor<?>[] constructors = screen.getClass().getDeclaredConstructors();
        for (Constructor<?> ctor : constructors) {
            Class<?>[] params = ctor.getParameterTypes();
            if (params.length == 2 &&
                    params[0] == params[1] &&
                    !params[0].isPrimitive() &&
                    !params[0].getName().startsWith("java.")) {
                return true;
            }
        }

        return false;
    }
}