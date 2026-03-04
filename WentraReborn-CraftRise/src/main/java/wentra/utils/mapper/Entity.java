package wentra.utils.mapper;

import org.lwjgl.util.vector.Vector3f;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static wentra.utils.mapper.Mapper.*;

public class Entity {

    private static final Pattern POSITION_PATTERN = Pattern.compile("x=([-\\d,\\.]+), y=([-\\d,\\.]+), z=([-\\d,\\.]+)");

    public static void addChatMessage(String message, boolean prefix) {
        try {
            String formatted = prefix ? "[-] WENTRA: " + message : message;
            Constructor<?> constructor = ChatComponentText.getConstructor(String.class);
            Object chatComponent = constructor.newInstance(formatted);
            addChatMessage.invoke(getPlayer(), chatComponent);
        } catch (Exception e) {
            log("Error adding chat message: " + e.getMessage());
        }
    }

    public static void addChatMessage(String message) {
        addChatMessage(message, true);
    }

    public static Object getFontRendererObj() {
        try {
            Field field = fontRendererObj;
            field.setAccessible(true);
            return field.get(getMinecraft());
        } catch (Exception e) {
            log("Error getting font renderer: " + e.getMessage());
            return null;
        }
    }

    public static int getId(Object player) {
        if (player == null) return -1;
        try {
            Method hashCodeMethod = player.getClass().getMethod("hashCode");
            return (int) hashCodeMethod.invoke(player);
        } catch (Exception e) {
            log("Error getting player ID: " + e.getMessage());
            return -1;
        }
    }

    public static Object getLocationSkin(Object player) {
        try {
            return getLocationSkin.invoke(player);
        } catch (Exception e) {
            log("Error getting location skin: " + e.getMessage());
            return null;
        }
    }

    public static Object getMinecraft() {
        try {
            return theMinecraft.get(null);
        } catch (Exception e) {
            log("Error getting Minecraft instance: " + e.getMessage());
            return null;
        }
    }

    public static String getName(Object entity) {
        try {
            String s = String.valueOf(entity);
            int start = s.indexOf('\'') + 1;
            int end = s.indexOf('\'', start);
            return (start > 0 && end > start) ? s.substring(start, end) : "Unknown";
        } catch (Exception e) {
            log("Error parsing name: " + e.getMessage());
            return "Unknown";
        }
    }

    public static Object getThePlayer() {
        return getPlayer();
    }

    public static List<Object> getPlayerEntitiesInWorld() {
        try {
            Object player = getThePlayer();
            if (player == null) return logAndReturnEmpty("thePlayer is null");

            Object worldObj = Mapper.worldObj.get(player);
            if (worldObj == null) return logAndReturnEmpty("worldObj is null");

            playerEntitiesList.setAccessible(true);
            Object entityList = playerEntitiesList.get(worldObj);
            if (!(entityList instanceof List)) return logAndReturnEmpty("Entity list is not a List");

            List<Object> entities = new ArrayList<>();
            for (Object entity : (List<?>) entityList) {
                if (entity != player) entities.add(entity);
            }
            return entities;
        } catch (Exception e) {
            log("Error getting player entities: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static Object getTextureManager() {
        try {
            return renderEngine.get(getMinecraft());
        } catch (Exception e) {
            log("Error getting texture manager: " + e.getMessage());
            return null;
        }
    }

    public static float getMoveForward() {
        return getMovementInput(moveForward, "moveForward");
    }

    public static float getMoveStrafe() {
        return getMovementInput(moveStrafing, "moveStrafing");
    }

    public static double getMotionX() {
        return getMotion(motionX, "motionX");
    }

    public static double getMotionY() {
        return getMotion(motionY, "motionY");
    }

    public static double getMotionZ() {
        return getMotion(motionZ, "motionZ");
    }

    public static boolean isMoving() {
        return getMoveForward() != 0.0f || getMoveStrafe() != 0.0f;
    }

    public static boolean onGround() {
        try {
            Object container = onGround.get(getThePlayer());
            getValueBoolean.setAccessible(true);
            return container != null && (boolean) getValueBoolean.invoke(container);
        } catch (Exception e) {
            log("Error checking onGround: " + e.getMessage());
            return false;
        }
    }

    public static void resetMotion(boolean resetY) {
        setMotionX(0);
        if (resetY) setMotionY(0);
        setMotionZ(0);
    }

    public static void setMotionX(double value) {
        setMotion(motionX, value, "motionX");
    }

    public static void setMotionY(double value) {
        setMotion(motionY, value, "motionY");
    }

    public static void setMotionZ(double value) {
        setMotion(motionZ, value, "motionZ");
    }

    public static void setSpeed(double moveSpeed) {
        setSpeed(moveSpeed, getRotationYaw(), getMoveStrafe(), getMoveForward());
    }

    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0) {
            if (strafe > 0.0) yaw += (forward > 0.0 ? -45 : 45);
            else if (strafe < 0.0) yaw += (forward > 0.0 ? 45 : -45);
            strafe = 0.0;
            forward = forward > 0.0 ? 1.0 : (forward < 0.0 ? -1.0 : forward);
        }
        strafe = strafe > 0.0 ? 1.0 : (strafe < 0.0 ? -1.0 : strafe);
        double mx = Math.cos(Math.toRadians(yaw + 90.0f));
        double mz = Math.sin(Math.toRadians(yaw + 90.0f));
        setMotionX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
        setMotionZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    public static double getDistance(Vector3f in, double x, double y, double z) {
        double diffX = in.x - x;
        double diffY = in.y - y;
        double diffZ = in.z - z;
        return Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
    }

    public static double getLastTickPosX(Object entity) {
        return getFieldValue(lastTickPosX, entity, "lastTickPosX");
    }

    public static double getLastTickPosY(Object entity) {
        return getFieldValue(lastTickPosY, entity, "lastTickPosY");
    }

    public static double getLastTickPosZ(Object entity) {
        return getFieldValue(lastTickPosZ, entity, "lastTickPosZ");
    }

    public static double getPosX(Object entity) {
        return parsePosition(entity != null ? entity : getThePlayer(), 1, "X");
    }

    public static double getPosX() {
        return getPosX(null);
    }

    public static double getPosY(Object entity) {
        return parsePosition(entity != null ? entity : getThePlayer(), 2, "Y");
    }

    public static double getPosY() {
        return getPosY(null);
    }

    public static double getPosZ(Object entity) {
        return parsePosition(entity != null ? entity : getThePlayer(), 3, "Z");
    }

    public static double getPosZ() {
        return getPosZ(null);
    }

    public static double getRenderPosX() {
        return getFieldValue(renderPosX, getRenderManager(), "renderPosX");
    }

    public static double getRenderPosY() {
        return getFieldValue(renderPosY, getRenderManager(), "renderPosY");
    }

    public static double getRenderPosZ() {
        return getFieldValue(renderPosZ, getRenderManager(), "renderPosZ");
    }

    public static double getViewerPosX() {
        return getFieldValue(viewerPosX, getRenderManager(), "viewerPosX");
    }

    public static double getViewerPosY() {
        return getFieldValue(viewerPosY, getRenderManager(), "viewerPosY");
    }

    public static double getViewerPosZ() {
        return getFieldValue(viewerPosZ, getRenderManager(), "viewerPosZ");
    }

    public static float getPrevRotationPitch() {
        return getRotationValue(prevRotationPitch, getThePlayer(), "PrevRotationPitch");
    }

    public static float getPrevRotationPitch(Object entity) {
        return getRotationValue(prevRotationPitch, entity, "PrevRotationPitch");
    }

    public static float getPrevRotationYaw() {
        return getRotationValue(prevRotationYaw, getThePlayer(), "PrevRotationYaw");
    }

    public static float getPrevRotationYaw(Object entity) {
        return getRotationValue(prevRotationYaw, entity, "PrevRotationYaw");
    }

    public static float getRotationPitch() {
        return getRotationValue(rotationPitch, getThePlayer(), "RotationPitch");
    }

    public static float getRotationPitch(Object entity) {
        return getRotationValue(rotationPitch, entity, "RotationPitch");
    }

    public static float getRotationYaw() {
        return getRotationValue(rotationYaw, getThePlayer(), "RotationYaw");
    }

    public static float getRotationYaw(Object entity) {
        return getRotationValue(rotationYaw, entity, "RotationYaw");
    }

    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - getPosX();
        double zDiff = z - getPosZ();
        double yDiff = y - getPosY() - 1.2;
        double dist = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-Math.atan2(yDiff, dist) * 180.0 / Math.PI);
        return new float[]{yaw, pitch};
    }

    public static String getCurrentScreen() {
        try {
            Object mc = theMinecraft.get(null);
            Object screen = currentScreen.get(mc);
            return screen != null ? screen.getClass().getSimpleName() : "null";
        } catch (Exception e) {
            addChatMessage("Error getting current screen: " + e.getMessage());
            return "error";
        }
    }

    public static Object getCurrentScreenObject() {
        try {
            Object mc = theMinecraft.get(null);
            Object screen = currentScreen.get(mc);
            return screen;
        } catch (Exception e) {
            addChatMessage("Error getting current screen: " + e.getMessage());
            return "error";
        }
    }

    public static Object getRenderManager() {
        try {
            renderManagerF.setAccessible(true);
            return renderManagerF.get(getMinecraft());
        } catch (Exception e) {
            log("Error getting render manager: " + e.getMessage());
            return null;
        }
    }

    public static Object getPlayerController() {
        try {
            playerController.setAccessible(true);
            Object mc = theMinecraft.get(null);
            return playerController.get(mc);
        } catch (Exception e) {
            log("Error getting playerController: " + e.getMessage());
            return null;
        }
    }

    public static Object getResourceLocation(String namespace, String path) {
        try {
            Constructor<?> constructor = ResourceLocation.getConstructor(String.class, String.class);
            return constructor.newInstance(namespace, path);
        } catch (NoSuchMethodException e) {
            try {
                Constructor<?> constructor = ResourceLocation.getConstructor(String.class);
                return constructor.newInstance(namespace + ":" + path);
            } catch (Exception ex) {
                log("Error creating ResourceLocation: " + namespace + ":" + path);
                return null;
            }
        } catch (Exception e) {
            log("Error creating ResourceLocation: " + namespace + ":" + path);
            return null;
        }
    }

    public static int getTicksExisted() {
        try {
            ticksExisted.setAccessible(true);
            return ticksExisted.getInt(getThePlayer());
        } catch (Exception e) {
            log("Error getting ticks existed: " + e.getMessage());
            return -1;
        }
    }

    public static String cleanDisplayName(String displayName) {
        return displayName.replaceAll("§.", "");
    }

    public static Float getEntityHealth(Object entity) {
        try {
            getEntityHealth.setAccessible(true);
            Object result = getEntityHealth.invoke(entity);
            return result != null ? (Float) result : 20.0f;
        } catch (Exception e) {
            log("Error getting entity health: " + e.getMessage());
            return 20.0f;
        }
    }

    public static double getEyeHeight(Object target) {
        try {
            return (float) getEyeHeight.invoke(target);
        } catch (Exception e) {
            log("Error getting eye height: " + e.getMessage());
            return 0.0f;
        }
    }

    public static boolean getIsInWeb(Object player) {
        try {
            isInWeb.setAccessible(true);
            return isInWeb.getBoolean(player);
        } catch (Exception e) {
            log("Error checking isInWeb: " + e.getMessage());
            return false;
        }
    }

    public static void setCurrentScreen(Object screen) {
        try {
            Object mc = theMinecraft.get(null);
            currentScreen.setAccessible(true);
            currentScreen.set(mc, screen);
        } catch (Exception e) {
            addChatMessage("Error setting screen: " + e.getMessage());
        }
    }

    public static void setIsInWeb(Object player, boolean status) {
        try {
            isInWeb.setAccessible(true);
            isInWeb.setBoolean(player, status);
        } catch (Exception e) {
            log("Error setting isInWeb: " + e.getMessage());
        }
    }

    public static void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        try {
            Object player = getPlayer();
            if (player != null && setPositionAndRotation != null) {
                setPositionAndRotation.setAccessible(true);
                setPositionAndRotation.invoke(player, x, y, z, yaw, pitch);
            } else {
                log("Error in setPositionAndRotation: Player or method null");
            }
        } catch (Exception e) {
            log("Error setting position and rotation: " + e.getMessage());
        }
    }

    public static void setRotations(float yaw, float pitch) {
        setPositionAndRotation(getPosX(), getPosY(), getPosZ(), yaw, pitch);
    }

    public static void setSwingProgress(Object player, boolean status) {
        try {
            isSwingInProgress.setAccessible(true);
            isSwingInProgress.setBoolean(player, status);
        } catch (Exception e) {
            log("Error setting swing progress: " + e.getMessage());
        }
    }

    public static void displayGuiScreen(Object gui) {
        try {
            if (Minecraft == null || displayGuiScreen == null || theMinecraft == null) {
                log("Error: Minecraft, displayGuiScreen, or theMinecraft not initialized");
                return;
            }
            displayGuiScreen.setAccessible(true);
            displayGuiScreen.invoke(theMinecraft, gui);
        } catch (Exception e) {
            log("Error displaying GUI screen: " + e.getMessage());
        }
    }

    private static List<Object> logAndReturnEmpty(String message) {
        log("Error: " + message);
        return Collections.emptyList();
    }

    private static double parsePosition(Object entity, int group, String coord) {
        try {
            String parseString = String.valueOf(entity);
            if (parseString != null && !parseString.isEmpty()) {
                Matcher matcher = POSITION_PATTERN.matcher(parseString);
                if (matcher.find()) {
                    return Double.parseDouble(matcher.group(group).replace(",", "."));
                }
                log("Error parsing " + coord + ": Pattern not matched");
            } else {
                log("Error parsing " + coord + ": parseString is empty or null");
            }
        } catch (Exception e) {
            log("Error parsing " + coord + ": " + e.getMessage());
        }
        return 0.0;
    }

    private static float getMovementInput(Field field, String name) {
        try {
            Object movementInputObj = getThePlayer();
            Field moveField = EntityLivingBase.getField(field.getName());
            Object floatContainerObj = moveField.get(movementInputObj);
            return (float) getValueFloat.invoke(floatContainerObj);
        } catch (Exception e) {
            log("Error getting " + name + ": " + e.getMessage());
            return 0.0f;
        }
    }

    private static double getMotion(Field field, String name) {
        try {
            Object motionObj = field.get(getThePlayer());
            getValueMotion.setAccessible(true);
            return (double) getValueMotion.invoke(motionObj);
        } catch (Exception e) {
            log("Error getting " + name + ": " + e.getMessage());
            return 0.0;
        }
    }

    private static void setMotion(Field field, double value, String name) {
        try {
            SetMotionFields(field, value);
        } catch (Exception e) {
            log("Error setting " + name + ": " + e.getMessage());
        }
    }

    private static double getFieldValue(Field field, Object obj, String name) {
        try {
            field.setAccessible(true);
            return field.getDouble(obj);
        } catch (Exception e) {
            log("Error getting " + name + ": " + e.getMessage());
            return 0.0;
        }
    }

    private static float getRotationValue(Field field, Object obj, String name) {
        if (field == null || obj == null) return 0.0f;
        try {
            field.setAccessible(true);
            Object val = field.get(obj);
            if (val instanceof Float) return (Float) val;
            if (val instanceof Double) return ((Double) val).floatValue();
        } catch (Exception e) {
            log("Error getting " + name + ": " + e.getMessage());
        }
        return 0.0f;
    }
}