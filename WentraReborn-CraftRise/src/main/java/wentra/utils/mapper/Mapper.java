package wentra.utils.mapper;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import wentra.Main;
import wentra.event.EventBus;
import wentra.utils.mapper.transformers.etc.impl.filters.FieldFilter;
import wentra.utils.mapper.transformers.etc.impl.filters.MethodFilter;
import wentra.utils.mapper.transformers.etc.impl.matchers.SimpleClassMatcher;
import wentra.utils.mapper.transformers.etc.impl.utils.*;

public class Mapper {

    public static Class<?> EntityClass, InventoryPlayer, IBlockState, EntityLivingBase, PlayerControllerMP, EntityPlayer, EntityPlayerSP, AbstractClientPlayer, NetworkPlayerInfo, World, MotionContainer,
            Minecraft, GameSettings, KeyBinding, Config, BooleanContainer, Block, IInventory, FloatContainer,
            RenderManager, EffectRenderer, TextureManager, ItemBlock, Item, GuiChest, Slot, AbstractTexture, FontRenderer, DynamicTexture, ScaledResolution, ResourceLocation,
            GuiScreen, Gui, GuiIngame, Container, GuiContainer,
            NetworkManager, NetHandlerPlayClient, NetworkHandler, C0APacketAnimation, S18PacketEntityTeleport, C02PacketUseEntityAction, C02PacketUseEntity,
            S12PacketEntityVelocity, PacketClass,
            IChatComponent, ChatComponentText, ChatComponentStyle, ItemStack, BlockPos, EnumFacing, Vec3;

    public static Method setPositionAndRotation, isAirBlock, getBlock, getSlot, getValueMotion, setPositionAndUpdate, setLocationAndAngles, getLocationSkin, getLocationCape, getDistanceToEntity, getDistanceToCamera,
            getEntityHealth, getEyeHeight, getFlag, getHeldItem, getValueFloat, getValueBoolean, setRenderPosition, renderParticles, renderWitherSkull, renderGameOverlay,
            getTextureManager, getGlTextureId, getStack, drawScaledCustomSizeModalRect, drawModalRectWithCustomSizedTexture, bindTexture, drawRect,
            displayGuiScreen, mouseClicked, keyTyped, drawScreen, mouseReleased,
            setKeyBindState, isKeyDown, KeyBinding_onTick,
            scaleFactorMethod, heightMethod, widthMethod,
            getThePlayer, sendPacket, addChatMessage,
            getStringWidth, windowClick, onPlayerRightClick;

    public static Field keyBindForward, playerController, keyBindLeft, keyBindBack, keyBindRight, keyBindJump, keyBindSneak, keyBindSprint, KeyBindPressed, moveStrafing, moveForward, isInWeb, onGround, locationSkin, locationCape, CRlocationCape, playerInfo, isSwingInProgress, fallDistance,
            motionX, motionY, motionZ, posX, posY, posZ, prevPosX, prevPosY, prevPosZ, lastTickPosX, lastTickPosY, lastTickPosZ,
            rotationYaw, rotationPitch, prevRotationYaw, windowId, Container_inventorySlots, prevRotationPitch,
            viewerPosX, viewerPosY, viewerPosZ, renderPosX, renderPosY, renderPosZ, renderManagerF, renderManager, fontRendererObj,
            theMinecraft, gameSettings, renderEngine, currentScreen, inventorySlots, partialTicks, thirdPersonView, ticksExisted,
            worldObj, sendQueue, playerEntitiesList, channel;

    private static final String LOG_PATH = "C:\\Wentra\\javalog.txt";
    private static final String ERR_PATH = "C:\\Wentra\\errors.txt";
    private static final StringBuilder logBuffer = new StringBuilder();


    public static void Starter() throws Exception {
        ClearLogs();

        Minecraft();
        EntityPlayerSP();
        AbstractClientPlayer();
        NetworkPlayerInfo();
        EntityPlayer();
        EntityLivingBase();
        Entity();
        theMinecraft();
        getThePlayer();
        MotionContainer();
        worldObj();
        getDistanceToEntity();
        PlayerEntitiesList();
        SendQueue();
        C02PacketUseEntityAction();
        C02PacketUseEntityAndPacketClass();
        C0APacketAnimation();
        SendPacket();
        IChatComponent();
        ChatComponentStyle();
        ChatComponentText();
        AddChatMessage();
        GuiIngame();
        renderGameOverlay();
        ResourceLocation();
        FontRenderer();
        GameSettings();
        KeyBinding();
        KeyBindingOnTick();
        GuiScreenMethods();
        displayGuiScreen();
        rectMethods();
        currentScreen();
        DynamicTexture();
        AbstractTexture();
        NetworkHandler();
        S18PacketEntityTeleport();
        TextureManager();
        RenderManager();
        Config();
        EffectRenderer();
        getFlag();
        FloatContainer();
        PlayerControllerMP();
        GuiChest();
        ItemBlock();
        getHeldItem();
        InventoryPlayer();
        playerController();

        Others();
    }

    private static Method findMethod(Class<?> clazz, int modifiers, Class<?> returnType, Class<?>... paramTypes) {
        if (clazz == null) return null;
        Method method = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> (m.getModifiers() & modifiers) == (modifiers & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.STATIC)) &&
                        m.getReturnType() == returnType &&
                        Arrays.equals(m.getParameterTypes(), paramTypes))
                .findFirst()
                .orElse(null);
        return method;
    }

    public static Field findField(Class<?> clazz, Class<?> type, int modifiers) {
        if (clazz == null) return null;
        Field field = Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.getType() == type && (f.getModifiers() & modifiers) == modifiers)
                .findFirst()
                .orElse(null);
        return field;
    }

    private static Field getFieldOfType(Class<?> clazz, Class<?> type) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.getType() == type) return f;
        }
        return null;
    }

    public static void Minecraft() {
        if (Minecraft != null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (containsMinecraftFields(clazz)) {
                Minecraft = clazz;
                return;
            }
        }
        log("[-] Error -> Minecraft class not found!");
    }

    public static void theMinecraft() {
        if (theMinecraft != null || Minecraft == null) return;
        theMinecraft = findField(Minecraft, Minecraft, Modifier.PUBLIC | Modifier.STATIC);
        if (theMinecraft == null) {
            log("[-] Error -> theMinecraft field not found!");
        }
    }

    public static void playerController() {
        if (theMinecraft == null || Minecraft == null || PlayerControllerMP == null) return;

        playerController = findField(Minecraft, PlayerControllerMP, Modifier.PUBLIC);

        if (playerController == null) {
            log("[-] Error -> playerController field not found!");
        }
    }

    public static void worldObj() {
        if (worldObj != null || EntityClass == null) return;
        worldObj = findField(EntityClass, World, Modifier.PUBLIC);
        if (worldObj == null) {
            log("[-] Error -> worldObj field not found!");
        }
    }

    public static void getThePlayer() {
        if (getThePlayer != null || Minecraft == null || EntityPlayerSP == null) return;
        getThePlayer = findMethod(Minecraft, Modifier.PUBLIC, EntityPlayerSP);
        if (getThePlayer == null) {
            log("[-] Error -> getThePlayer method not found!");
        }
    }

    public static void EntityPlayerSP() {
        if (EntityClass != null || Minecraft == null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Check_EntityPlayerSP(clazz)) {
                EntityPlayerSP = clazz;
                return;
            }
        }
        log("[-] Error -> EntityPlayerSP classes not found!");
    }

    public static void AbstractClientPlayer() {
        if (EntityClass != null || Minecraft == null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Check_EntityPlayerSP(clazz)) {
                AbstractClientPlayer = clazz.getSuperclass();
                return;
            }
        }
        log("[-] Error -> AbstractClientPlayer classes not found!");
    }

    public static void EntityPlayer() {
        if (EntityClass != null || Minecraft == null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Check_EntityPlayerSP(clazz)) {
                EntityPlayer = AbstractClientPlayer.getSuperclass();
                return;
            }
        }
        log("[-] Error -> EntityPlayer classes not found!");
    }

    public static void EntityLivingBase() {
        if (EntityClass != null || Minecraft == null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Check_EntityPlayerSP(clazz)) {
                EntityLivingBase = EntityPlayer.getSuperclass();
                return;
            }
        }
        log("[-] Error -> EntityPlayer classes not found!");
    }

    public static void Entity() {
        if (EntityClass != null || Minecraft == null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Check_EntityPlayerSP(clazz)) {
                EntityClass = EntityLivingBase.getSuperclass();
                return;
            }
        }
        log("[-] Error -> Entity classes not found!");
    }

    public static void MotionContainer() {
        if (MotionContainer != null || EntityClass == null) return;
        for (Field f : EntityClass.getFields()) {
            Class<?> type = f.getType();
            if (!type.isPrimitive() && Arrays.stream(type.getDeclaredFields())
                    .filter(inner -> inner.getType() == double.class)
                    .count() >= 2) {
                MotionContainer = type;
                return;
            }
        }
        log("[-] Error -> MotionContainer class not found!");
    }

    public static void getDistanceToEntity() {
        getDistanceToEntity = Arrays.stream(ReflectionUtils.getAllMethods(EntityClass))
                .filter(m -> m.toString().contains("public float") && m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(EntityClass))
                .findFirst().orElse(null);
    }

    public static void PlayerEntitiesList() {
        playerEntitiesList = Reflector.findListUsingParam(World, EntityPlayer);
    }

    public static void SendQueue() {
        sendQueue = Reflector.getFieldByType(EntityPlayerSP, NetHandlerPlayClient);
    }

    public static void NetworkPlayerInfo() {
        Field playerInfoField = Arrays.stream(ReflectionUtils.getAllFields(AbstractClientPlayer))
                .filter(field -> !field.getType().equals(String.class))
                .filter(field -> !field.getType().equals(int.class))
                .filter(field -> !field.toString().contains("static"))
                .filter(field -> Modifier.isPrivate(field.getModifiers()))
                .filter(field -> {
                    Set<Class<?>> publicStaticTypes = Arrays.stream(ReflectionUtils.getAllFields(AbstractClientPlayer))
                            .filter(f -> Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers()))
                            .map(Field::getType)
                            .collect(Collectors.toSet());

                    return !publicStaticTypes.contains(field.getType());
                })
                .findFirst()
                .orElse(null);
        playerInfo = playerInfoField;
        NetworkPlayerInfo = playerInfo.getType();
    }

    public static void C02PacketUseEntityAction() {
        C02PacketUseEntityAction = Reflector.findClassUsingFieldNames("ATTACK", "INTERACT_AT");
    }

    public static void C02PacketUseEntityAndPacketClass() {
        if (C02PacketUseEntityAction != null) {
            C02PacketUseEntity = Reflector.findClassByName(Reflector.getOuterClassName(C02PacketUseEntityAction.getName()));
            Class<?>[] interfaces = C02PacketUseEntity.getInterfaces();
            PacketClass = interfaces.length > 0 ? interfaces[0] : null;
        }
    }

    public static void SendPacket() {
        sendPacket = Reflector.findSpecificMethod(NetHandlerPlayClient, PacketClass);
    }

    public static void C0APacketAnimation() {
        C0APacketAnimation = Reflector.findSwingPacket();
    }

    public static void IChatComponent() {
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Check_IChatComponent(clazz)) {
                IChatComponent = clazz;
                break;
            }
        }
    }

    public static void ChatComponentStyle() {
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Check_ChatComponentStyle(clazz)) {
                ChatComponentStyle = clazz;
                break;
            }
        }
    }

    public static void ChatComponentText() {
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Check_ChatComponentText(clazz)) {
                ChatComponentText = clazz;
                break;
            }
        }
    }

    public static void AddChatMessage() {
        for (Method method : EntityPlayerSP.getDeclaredMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 1 && params[0] == IChatComponent) {
                addChatMessage = method;
                break;
            }
        }
    }

    public static Class<?> S18PacketEntityTeleport() {
        List<Class<?>> classes = Reflector.getLoadedClasses();
        Class<?> packetClass = Mapper.PacketClass;


        for (Class<?> clazz : classes) {
            if (packetClass.isAssignableFrom(clazz)) {
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                if(constructors.length < 3)continue;

                for (Constructor<?> constructor : constructors) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    if (parameterTypes.length == 7 && parameterTypes[0] == int.class && parameterTypes[1] == int.class && parameterTypes[2] == int.class && parameterTypes[3] == int.class && parameterTypes[4] == byte.class && parameterTypes[5] == byte.class && parameterTypes[6] == boolean.class) {
                        Mapper.S18PacketEntityTeleport = clazz;
                        return clazz;
                    }
                }
            }
        }
        return null;
    }

    public static void GuiIngame() {
        if (GuiIngame != null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Arrays.stream(clazz.getDeclaredFields())
                    .anyMatch(f -> Modifier.isPrivate(f.getModifiers()) &&
                            Modifier.isStatic(f.getModifiers()) &&
                            Modifier.isFinal(f.getModifiers()) &&
                            java.util.Map.class.isAssignableFrom(f.getType())) &&
                    Arrays.stream(clazz.getDeclaredFields())
                            .anyMatch(f -> Modifier.isProtected(f.getModifiers()) &&
                                    Modifier.isFinal(f.getModifiers()) &&
                                    f.getType() == Minecraft) &&
                    Arrays.stream(clazz.getDeclaredMethods())
                            .anyMatch(m -> Modifier.isPublic(m.getModifiers()) &&
                                    m.getParameterTypes().length == 1 &&
                                    m.getParameterTypes()[0] == float.class)) {
                GuiIngame = clazz;
                return;
            }
        }
        log("[-] Error -> GuiIngame class not found!");
    }

    public static void renderGameOverlay() {
        renderGameOverlay = findMethod(GuiIngame, Modifier.PUBLIC, void.class, float.class);
        if (renderGameOverlay == null) {
            log("[-] Error -> renderGameOverlay method not found!");
        }
    }

    public static void ResourceLocation() {
        if (ResourceLocation != null || GuiIngame == null) return;
        Class<?> foundClass = null;
        int matchingFieldCount = 0;
        try {
            for (Field field : GuiIngame.getDeclaredFields()) {
                if (Modifier.isPrivate(field.getModifiers()) &&
                        Modifier.isStatic(field.getModifiers()) &&
                        Modifier.isFinal(field.getModifiers())) {
                    if (foundClass == null) foundClass = field.getType();
                    if (field.getType() == foundClass) matchingFieldCount++;
                }
            }
            if (matchingFieldCount == 3 && foundClass != null) {
                ResourceLocation = foundClass;
            } else {
                log("[-] Error -> ResourceLocation not found. Found: " + matchingFieldCount);
            }
        } catch (SecurityException e) {
            log("[-] Error -> ResourceLocation - Security restriction: " + e.getMessage());
        }
    }

    public static void FontRenderer() {
        if (FontRenderer != null) return;
        SimpleClassMatcher fontRenderer = new SimpleClassMatcher(
                new MethodFilter[]{},
                new FieldFilter[]{
                        new FieldFilter("private int[]"),
                        new FieldFilter("public byte[]"),
                        new FieldFilter("public float[]"),
                        new FieldFilter("public float"),
                        new FieldFilter("public float"),
                        new FieldFilter("public float"),
                        new FieldFilter("public float")
                },
                -1,
                "craftrise"
        );
        Class<?>[] matched = fontRenderer.matchFrom(ClassUtils.getClasses().toArray(new Class<?>[0]));
        FontRenderer = matched.length > 0 ? matched[0] : null;
        if (FontRenderer == null) {
            log("[-] Error -> FontRenderer not found!");
        }
    }

    public static void GameSettings() {
        if (gameSettings != null || FontRenderer == null || Minecraft == null) return;
        Constructor<?> targetConstructor = null;
        for (Constructor<?> constructor : FontRenderer.getDeclaredConstructors()) {
            Class<?>[] params = constructor.getParameterTypes();
            if (params.length == 4 && params[3] == boolean.class) {
                targetConstructor = constructor;
                break;
            }
        }
        if (targetConstructor != null) {
            GameSettings = targetConstructor.getParameterTypes()[0];
            gameSettings = findField(Minecraft, GameSettings, Modifier.PUBLIC);
            if (gameSettings != null) {
                gameSettings.setAccessible(true);
            } else {
                log("[-] Error -> gameSettings field not found!");
            }
        } else {
            log("[-] Error -> GameSettings not found! No suitable constructor.");
        }
    }

    public static void KeyBinding() {
        if (KeyBinding != null || GameSettings == null) return;
        Map<Class<?>, Long> fieldTypeCounts = Arrays.stream(GameSettings.getDeclaredFields())
                .filter(f -> Modifier.isPublic(f.getModifiers()) && !f.getType().isPrimitive())
                .collect(Collectors.groupingBy(Field::getType, Collectors.counting()));
        for (Map.Entry<Class<?>, Long> entry : fieldTypeCounts.entrySet()) {
            if (entry.getValue() >= 23) {
                KeyBinding = entry.getKey();
                break;
            }
        }
        if (KeyBinding == null) {
            log("[-] Error -> KeyBinding not found!");
        }
    }

    public static void KeyBindingOnTick() {
        if (setKeyBindState != null || KeyBinding == null) return;
        setKeyBindState = findMethod(KeyBinding, Modifier.PUBLIC | Modifier.STATIC, void.class, Minecraft, int.class, boolean.class);
        isKeyDown = findMethod(KeyBinding, Modifier.PUBLIC, boolean.class);
        KeyBinding_onTick = findMethod(KeyBinding, Modifier.PUBLIC | Modifier.STATIC, void.class, int.class);
        if (setKeyBindState == null) log("[-] Error -> setKeyBindState not found!");
        if (isKeyDown == null) log("[-] Error -> isKeyDown not found!");
        if (KeyBinding_onTick == null) log("[-] Error -> KeyBinding_onTick not found!");
    }

    public static void GuiScreenMethods() {
        if (GuiScreen != null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Modifier.isAbstract(clazz.getModifiers()) && clazz.getSuperclass() != null && clazz.getInterfaces().length == 1) {
                int fieldCount = 0, listFieldCount = 0, publicIntFieldCount = 0;
                boolean hasFontRenderer = false, hasMinecraft = false, hasSetString = false, hasSplitter = false;
                for (Field field : clazz.getDeclaredFields()) {
                    int mods = field.getModifiers();
                    if (Modifier.isProtected(mods) || Modifier.isPublic(mods)) {
                        fieldCount++;
                        if (List.class.isAssignableFrom(field.getType())) listFieldCount++;
                        if (field.getType().equals(FontRenderer)) hasFontRenderer = true;
                        if (field.getType().equals(Minecraft)) hasMinecraft = true;
                    }
                    if (Modifier.isPrivate(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods) &&
                            field.getType().equals(Set.class) && field.getGenericType().toString().contains("java.lang.String")) {
                        hasSetString = true;
                    }
                    if (Modifier.isPrivate(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods) &&
                            field.getType().getName().contains("Splitter")) {
                        hasSplitter = true;
                    }
                    if (Modifier.isPublic(mods) && field.getType() == int.class) publicIntFieldCount++;
                }
                if (fieldCount > 2 && listFieldCount >= 2 && hasFontRenderer && hasMinecraft && hasSetString && hasSplitter && publicIntFieldCount >= 2) {
                    GuiScreen = clazz;
                    mouseClicked = findMethod(GuiScreen, Modifier.PROTECTED, void.class, int.class, int.class, int.class);
                    keyTyped = findMethod(GuiScreen, Modifier.PROTECTED, void.class, char.class, int.class);
                    mouseReleased = findMethod(GuiScreen, Modifier.PROTECTED, void.class, int.class, int.class, int.class);
                    drawScreen = findMethod(GuiScreen, Modifier.PUBLIC, void.class, int.class, int.class, float.class);
                    if (mouseClicked == null) log("[-] Error -> mouseClicked not found!");
                    if (keyTyped == null) log("[-] Error -> keyTyped not found!");
                    if (mouseReleased == null) log("[-] Error -> mouseReleased not found!");
                    if (drawScreen == null) log("[-] Error -> drawScreen not found!");
                    return;
                }
            }
        }
        log("[-] Error -> GuiScreen not found!");
    }

    public static void displayGuiScreen() {
        if (displayGuiScreen != null || GuiScreen == null || Minecraft == null) return;
        displayGuiScreen = findMethod(Minecraft, Modifier.PUBLIC, void.class, GuiScreen);
        if (displayGuiScreen == null) {
            log("[-] Error -> displayGuiScreen not found!");
        }
    }

    public static void rectMethods() {
        if (Gui == null && GuiScreen != null) Gui = GuiScreen.getSuperclass();
        if (Gui == null) {
            log("[-] Error -> Gui class not found!");
            return;
        }
        drawRect = findMethod(Gui, Modifier.PUBLIC | Modifier.STATIC, void.class, int.class, int.class, int.class, int.class, int.class);
        drawModalRectWithCustomSizedTexture = findMethod(Gui, Modifier.PUBLIC | Modifier.STATIC, void.class,
                int.class, int.class, float.class, float.class, int.class, int.class, float.class, float.class);
        drawScaledCustomSizeModalRect = findMethod(Gui, Modifier.PUBLIC | Modifier.STATIC, void.class,
                float.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class);
        if (drawRect == null) log("[-] Error -> drawRect not found!");
        if (drawModalRectWithCustomSizedTexture == null)
            log("[-] Error -> drawModalRectWithCustomSizedTexture not found!");
        if (drawScaledCustomSizeModalRect == null) log("[-] Error -> drawScaledCustomSizeModalRect not found!");
    }

    public static void currentScreen() {
        if (currentScreen != null || Minecraft == null) return;
        currentScreen = findField(Minecraft, GuiScreen, Modifier.PUBLIC);
        if (currentScreen == null) {
            log("[-] Error -> currentScreen not found!");
        }
    }

    public static void DynamicTexture() {
        if (DynamicTexture != null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            boolean hasBufferedImageCtor = false, hasIntIntCtor = false;
            for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
                Class<?>[] p = ctor.getParameterTypes();
                if (p.length == 1 && p[0] == java.awt.image.BufferedImage.class) hasBufferedImageCtor = true;
                if (p.length == 2 && p[0] == int.class && p[1] == int.class) hasIntIntCtor = true;
            }
            if (hasBufferedImageCtor && hasIntIntCtor) {
                int intCount = 0, boolCount = 0;
                for (Field f : clazz.getDeclaredFields()) {
                    if (f.getType() == int.class) intCount++;
                    if (f.getType() == boolean.class) boolCount++;
                }
                if (intCount >= 3 && boolCount >= 1) {
                    DynamicTexture = clazz;
                    return;
                }
            }
        }
        log("[-] Error -> DynamicTexture not found!");
    }

    public static void AbstractTexture() {
        if (AbstractTexture != null || DynamicTexture == null) return;
        AbstractTexture = DynamicTexture.getSuperclass();
        if (AbstractTexture != null) {
            getGlTextureId = findMethod(AbstractTexture, Modifier.PUBLIC, int.class);
            if (getGlTextureId == null) {
                log("[-] Error -> getGlTextureId not found!");
            }
        } else {
            log("[-] Error -> AbstractTexture not found!");
        }
    }

    public static void NetworkHandler() {
        if (NetworkHandler != null || PacketClass == null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            Type superType = clazz.getGenericSuperclass();
            if (superType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) superType;
                if (pt.getRawType() == io.netty.channel.SimpleChannelInboundHandler.class &&
                        pt.getActualTypeArguments().length == 1 &&
                        pt.getActualTypeArguments()[0] == PacketClass) {
                    boolean hasLogger = false, hasMarkerO = false, hasMarkerE = false;
                    Field channelField = null;
                    for (Field f : clazz.getDeclaredFields()) {
                        int mods = f.getModifiers();
                        if (Modifier.isPrivate(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods) &&
                                f.getType() == org.apache.logging.log4j.Logger.class) hasLogger = true;
                        if (Modifier.isPublic(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods) &&
                                f.getType() == org.apache.logging.log4j.Marker.class) {
                            if (!hasMarkerO) hasMarkerO = true;
                            else hasMarkerE = true;
                        }
                        if (f.getType() == io.netty.channel.Channel.class) channelField = f;
                    }
                    if (hasLogger && hasMarkerO && hasMarkerE) {
                        NetworkHandler = clazz;
                        channel = channelField;
                        if (channel == null) log("[-] Error -> channel field not found!");
                        return;
                    }
                }
            }
        }
        log("[-] Error -> NetworkHandler not found!");
    }

    public static void TextureManager() {
        if (TextureManager != null) return;
        SimpleClassMatcher matcher = new SimpleClassMatcher(
                new MethodFilter[]{
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public boolean")
                },
                new FieldFilter[]{
                        new FieldFilter("private static final org.apache.logging.log4j.Logger"),
                        new FieldFilter("public final java.util.Map"),
                        new FieldFilter("private final java.util.List"),
                        new FieldFilter("private final java.util.Map")
                },
                -1,
                "craftrise"
        );
        Class<?>[] matched = matcher.matchFrom(Main.classesR);
        TextureManager = matched.length > 0 ? matched[0] : null;
        if (TextureManager != null) {
            getTextureManager = findMethod(Minecraft, Modifier.PUBLIC, TextureManager);
            renderEngine = findField(Minecraft, TextureManager, Modifier.PUBLIC);
            if (renderEngine != null) renderEngine.setAccessible(true);
            if (getTextureManager == null) log("[-] Error -> getTextureManager not found!");
            if (renderEngine == null) log("[-] Error -> renderEngine not found!");
        } else {
            log("[-] Error -> TextureManager not found!");
        }

        for (Method mt : ReflectionUtils.getAllMethods(TextureManager)) {
            if (mt.getParameterCount() == 3 && mt.getParameterTypes()[0].equals(ResourceLocation) && mt.getParameterTypes()[1].equals(int.class) && mt.getParameterTypes()[2].equals(int.class)) {
                bindTexture = mt;
            }
        }
    }

    public static void RenderManager() {
        if (RenderManager != null) return;
        SimpleClassMatcher matcher = new SimpleClassMatcher(
                new MethodFilter[]{
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public void"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public void")
                },
                new FieldFilter[]{
                        new FieldFilter("public java.util.Map"),
                        new FieldFilter("public java.util.Map"),
                        new FieldFilter("private static final java.util.Map"),
                        new FieldFilter("private static final java.lang.String[]"),
                        new FieldFilter("private static final java.lang.String[]"),
                        new FieldFilter("private boolean"),
                        new FieldFilter("private boolean"),
                        new FieldFilter("private boolean"),
                        new FieldFilter("public double"),
                        new FieldFilter("public double"),
                        new FieldFilter("public double"),
                        new FieldFilter("public double"),
                        new FieldFilter("public double"),
                        new FieldFilter("public double"),
                        new FieldFilter("public float"),
                        new FieldFilter("public float")
                },
                -1,
                "craftrise"
        );
        Class<?>[] matched = matcher.matchFrom(Main.classesR);
        RenderManager = matched.length > 0 ? matched[0] : null;
        if (RenderManager != null) {
            renderManager = findField(Minecraft, RenderManager, Modifier.PRIVATE);
            renderManagerF = renderManager;
            if (renderManager == null) log("[-] Error -> renderManager not found!");
        } else {
            log("[-] Error -> RenderManager not found!");
        }
    }

    public static void Config() {
        if (partialTicks != null) return;
        for (Class<?> clazz : ClassUtils.getClasses()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals("renderPartialTicks") && field.getType() == float.class) {
                    partialTicks = field;
                    Config = clazz;
                    return;
                }
            }
        }
        log("[-] Error -> partialTicks not found!");
    }

    public static void EffectRenderer() {
        if (EffectRenderer != null) return;
        SimpleClassMatcher matcher = new SimpleClassMatcher(
                new MethodFilter[]{},
                new FieldFilter[]{
                        new FieldFilter(String.format("protected %s", World.getName())),
                        new FieldFilter("private java.util.List"),
                        new FieldFilter(String.format("private %s", TextureManager.getName())),
                        new FieldFilter("private java.util.Map")
                },
                -1,
                "craftrise"
        );
        Class<?>[] matched = matcher.matchFrom(Main.classesR);
        EffectRenderer = matched.length > 0 ? matched[0] : null;
        if (EffectRenderer == null) {
            log("[-] Error -> EffectRenderer not found!");
        }
    }

    public static void getFlag() {
        if (getFlag != null || EntityClass == null) return;
        getFlag = findMethod(EntityClass, Modifier.PROTECTED, boolean.class, int.class);
        if (getFlag == null) {
            log("[-] Error -> getFlag not found!");
        }
    }

    public static void FloatContainer() {
        if (FloatContainer != null) return;
        SimpleClassMatcher matcher = new SimpleClassMatcher(
                new MethodFilter[]{
                        new MethodFilter("public float"),
                        new MethodFilter("public float"),
                        new MethodFilter("public float"),
                        new MethodFilter("public float"),
                        new MethodFilter("public float"),
                        new MethodFilter("public float"),
                        new MethodFilter("public float")
                },
                new FieldFilter[]{
                        new FieldFilter("private float"),
                        new FieldFilter("private final Object")
                },
                -1,
                "craftrise"
        );
        Class<?>[] matched = matcher.matchFrom(Main.classesR);
        FloatContainer = matched.length > 0 ? matched[0] : null;
        if (FloatContainer == null) {
            log("[-] Error -> FloatContainer not found!");
        }
    }



    public static void Others() {
        try {
            getEntityHealth = Arrays.stream(ReflectionUtils.getAllMethods(EntityLivingBase))
                    .filter(m -> m.toString().contains("public final float")).skip(1).findFirst().orElse(null);

            fontRendererObj = Reflector.getFieldByType(Minecraft, FontRenderer);

            for (Constructor<?> ctor : NetHandlerPlayClient.getDeclaredConstructors()) {
                if (ctor.getParameterCount() == 4) {
                    NetworkManager = ctor.getParameterTypes()[2];
                    break;
                }
            }

            S12PacketEntityVelocity = Check_S12PacketVelocity();

            getStringWidth = Arrays.stream(ReflectionUtils.getAllMethods(FontRenderer))
                    .filter(m -> m.getReturnType() == int.class &&
                            m.getParameterCount() == 1 &&
                            m.getParameterTypes()[0] == String.class &&
                            Modifier.isPublic(m.getModifiers()))
                    .findFirst().orElse(null);

            for (Method method : GuiIngame.getDeclaredMethods()) {
                if (method.getParameterCount() == 2) {
                    Class<?> p1 = method.getParameterTypes()[0];
                    Class<?> p2 = method.getParameterTypes()[1];
                    if (!p1.isPrimitive() && !p2.isPrimitive()) {
                        ScaledResolution = p2;
                        break;
                    }
                }
            }

            try {
                Constructor<?> constructor = Mapper.ScaledResolution.getConstructor(Mapper.Minecraft);
                Object scaledInstance = constructor.newInstance(Entity.getMinecraft());
                detectMethods(Mapper.ScaledResolution, scaledInstance);
            } catch (Exception e) {
                Mapper.log("[ScaledResolution Error] " + e.getMessage());
                e.printStackTrace();
            }

            Object player = Entity.getThePlayer();
            Method[] methods = EntityClass.getMethods();
            Method getEyeHeightMatched = null;
            int matchedCount = 0;

            for (Method method : methods) {
                if (Modifier.isPublic(method.getModifiers()) &&
                        method.getParameterCount() == 0 &&
                        method.getReturnType() == float.class) {
                    try {
                        float val = (float) method.invoke(player);
                        if (val > 0.0f && val < 3.0f && val != 1.0f && val != 0.1f) {
                            matchedCount++;
                            getEyeHeightMatched = method;
                        }
                    } catch (Exception e) {
                        Mapper.log("[getEyeHeight Error] " + e.getMessage());
                    }
                }
            }

            if (matchedCount == 1) {
                getEyeHeight = getEyeHeightMatched;
            } else {
                Mapper.log("[getEyeHeight] Match count invalid: " + matchedCount);
            }


        } catch (Exception e) {
            Mapper.log("[Others() Fatal Error] " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void GuiChest() {
        for(Class<?> clazz : ClassUtils.getClasses()){
            if(clazz.getSuperclass() != null){
                if(!clazz.getSuperclass().getName().equals("java.lang.Object")){
                    Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                    for (Constructor<?> ctor : constructors) {
                        Class<?>[] params = ctor.getParameterTypes();
                        if(constructors.length == 2){
                            if (params.length == 2 &&
                                    params[0] == params[1] &&
                                    !params[0].isPrimitive() &&
                                    !params[0].getName().startsWith("java.")) {
                                GuiChest = clazz;
                                break;
                            }
                        }
                    }
                    if(GuiChest == clazz) break;
                }
            }
        }
        GuiContainer = GuiChest.getSuperclass();
        GuiContainer();
    }

    public static void GuiContainer() {
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (Modifier.isAbstract(clazz.getModifiers()) && clazz.getSuperclass() == GuiScreen) {
                Field publicField = Arrays.stream(clazz.getDeclaredFields())
                        .filter(f -> Modifier.isPublic(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
                        .findFirst()
                        .orElse(null);
                Container = findContainerClassFromEntityPlayer(EntityPlayer);

                Field windowIdField = Arrays.stream(Container.getDeclaredFields())
                        .filter(f -> Modifier.isPublic(f.getModifiers()) && f.getType() == int.class)
                        .findFirst()
                        .orElse(null);
                if (windowIdField != null) {
                    windowId = windowIdField;
                } else {
                    log("[-] Error -> windowId field not found in Container!");
                }

                Field containerInventorySlots = null;
                for (Field field : Container.getDeclaredFields()) {
                    if (Modifier.isPublic(field.getModifiers()) && List.class.isAssignableFrom(field.getType())) {
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) genericType;
                            if (pt.getActualTypeArguments().length == 1) {
                                Type listType = pt.getActualTypeArguments()[0];
                                if (listType instanceof Class<?> && !listType.equals(ItemStack)) {
                                    containerInventorySlots = field;
                                    Slot = (Class<?>) listType;
                                    Field slotsField = getListFieldOfType(Container, Slot);
                                    inventorySlots = slotsField;
                                }
                            }
                        }
                    }
                }

                Field inventorySlots = getFieldOfType(GuiContainer, Container);
                Container_inventorySlots = inventorySlots;

                if (Slot != null) {
                    getStack = Arrays.stream(Slot.getDeclaredMethods())
                            .filter(m -> Modifier.isPublic(m.getModifiers()) &&
                                    m.getReturnType() == ItemStack &&
                                    m.getParameterCount() == 0)
                            .findFirst()
                            .orElse(null);
                    if (getStack == null) {
                        log("[-] Error -> getStack method not found in Slot!");
                    }

                    Constructor<?> slotConstructor = Arrays.stream(Slot.getDeclaredConstructors())
                            .filter(c -> c.getParameterCount() >= 1)
                            .findFirst()
                            .orElse(null);
                    if (slotConstructor != null) {
                        IInventory = slotConstructor.getParameterTypes()[0];
                    } else {
                        log("[-] Error -> Suitable constructor not found in Slot!");
                    }
                }

                getSlot = findGetSlot();
            }
        }
    }

    public static void getHeldItem() throws Exception {
        Class<?> itemStackClass = ItemStack;
        Class<?> playerClass = EntityPlayer;
        Object playerInstance = getPlayer();

        List<Method> itemStackMethods = new ArrayList<>();
        for (Method m : playerClass.getMethods()) {
            if (m.getReturnType() == itemStackClass && m.getParameterCount() == 0) {
                itemStackMethods.add(m);
            }
        }

        List<Set<Method>> groups = new ArrayList<>();

        outer:
        for (Method m : itemStackMethods) {
            Object resultM = m.invoke(playerInstance);

            for (Set<Method> group : groups) {
                Method representative = group.iterator().next();
                Object resultR = representative.invoke(playerInstance);

                boolean equal = (resultM == resultR) ||
                        (resultM != null && resultM.equals(resultR));

                if (equal) {
                    group.add(m);
                    continue outer;
                }
            }

            Set<Method> newGroup = new HashSet<>();
            newGroup.add(m);
            groups.add(newGroup);
        }

        boolean getHeldItemSet = false;
        for (Set<Method> group : groups) {
            Method chosen = group.iterator().next();

            if (!getHeldItemSet) {
                getHeldItem = chosen;
                getHeldItemSet = true;
            }
        }
    }

    public static void ItemBlock() {
        SimpleClassMatcher matcher = new SimpleClassMatcher(
                new MethodFilter[]{
                        new MethodFilter("public static boolean"),
                        new MethodFilter("public java.lang.String"),
                        new MethodFilter("public java.lang.String"),
                        new MethodFilter("public java.lang.String"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("private static"),
                        new MethodFilter("private static"),
                        new MethodFilter("private static"),
                        new MethodFilter("private static"),
                        new MethodFilter("private static"),
                        new MethodFilter("public void")
                },
                new FieldFilter[]{
                        new FieldFilter("protected final"),
                        new FieldFilter("private static final java.lang.String[]"),
                        new FieldFilter("private static final java.lang.String[]"),
                        new FieldFilter("private static final java.util.Map")
                },
                -1,
                "craftrise"
        );

        Class<?>[] matched = matcher.matchFrom(ClassUtils.getClasses().toArray(new Class<?>[0]));

        for (Class<?> clazz : matched) {
            if (clazz.getSuperclass() != null) {
                Field protectedFinalField = null;
                for (Field field : clazz.getDeclaredFields()) {
                    int modifiers = field.getModifiers();
                    if (Modifier.isProtected(modifiers) && Modifier.isFinal(modifiers)) {
                        protectedFinalField = field;
                        break;
                    }
                }

                if (protectedFinalField != null) {
                    ItemBlock = clazz;
                    Item = clazz.getSuperclass();
                    Block = protectedFinalField.getType();

                    for (Method method : ItemBlock.getDeclaredMethods()) {
                        if (Modifier.isPublic(method.getModifiers()) &&
                                method.getReturnType().equals(Block) &&
                                method.getParameterCount() == 0) {
                            getBlock = method;
                            break;
                        }
                    }

                    if (getBlock != null) {
                    } else {
                        log("[-] Error -> getBlock method not found in ItemBlock!");
                    }
                }
            }
        }
    }

    public static void InventoryPlayer() {
        SimpleClassMatcher matcher = new SimpleClassMatcher(
                new MethodFilter[]{
                        new MethodFilter("public static int"),
                        new MethodFilter("private int"),
                        new MethodFilter("private int"),
                        new MethodFilter("private int"),
                        new MethodFilter("private int"),
                },
                new FieldFilter[]{
                        new FieldFilter("public int"),
                        new FieldFilter("public boolean"),
                        new FieldFilter(String.format("public %s", EntityPlayer.getName())),
                        new FieldFilter(String.format("private %s", ItemStack.getName())),
                },
                -1,
                "craftrise"
        );
        Class<?>[] matched = matcher.matchFrom(Main.classesR);
        InventoryPlayer = matched.length > 0 ? matched[0] : null;
    }

    public static void PlayerControllerMP() {
        if (PlayerControllerMP != null) return;
        SimpleClassMatcher matcher = new SimpleClassMatcher(
                new MethodFilter[]{
                        new MethodFilter("public static void"),
                        new MethodFilter("public static void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public void"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                        new MethodFilter("public boolean"),
                },
                new FieldFilter[]{
                        new FieldFilter("private final " + Minecraft.getName()),
                        new FieldFilter("private static final java.util.Map"),
                        new FieldFilter("private static final java.lang.String[]"),
                        new FieldFilter("private static final java.lang.String[]"),
                        new FieldFilter("public static final java.lang.String"),
                        new FieldFilter("public static final java.lang.String"),
                        new FieldFilter("public static final double"),
                        new FieldFilter("public static final double"),
                        new FieldFilter("private boolean"),
                        new FieldFilter("public float"),
                        new FieldFilter("public float")
                },
                -1,
                "craftrise"
        );
        Class<?>[] matched = matcher.matchFrom(Main.classesR);
        PlayerControllerMP = matched.length > 0 ? matched[0] : null;

        for (Method mt : ReflectionUtils.getAllMethods(PlayerControllerMP)) {
            if (mt.getParameterCount() == 5 && mt.getParameterTypes()[0].equals(int.class) && mt.getParameterTypes()[1].equals(int.class) && mt.getParameterTypes()[2].equals(int.class) && mt.getParameterTypes()[3].equals(int.class) && mt.getParameterTypes()[4].equals(EntityPlayer)) {
                windowClick = mt;
            }
        }

        ItemStack = windowClick.getReturnType();

        onPlayerRightClick = Arrays.stream(PlayerControllerMP.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> m.getReturnType() == boolean.class)
                .filter(m -> m.getParameterCount() == 6)
                .findFirst()
                .orElse(null);

        if (onPlayerRightClick != null) {
            Class<?>[] params = onPlayerRightClick.getParameterTypes();
            if (params.length >= 6) {
                BlockPos = params[3];  // 4th parameter
                EnumFacing = params[4]; // 5th parameter
                Vec3 = params[5];      // 6th parameter
                Field[] fields = Mapper.EnumFacing.getDeclaredFields();
            } else {
                log("[-] Error -> onPlayerRightClick parameters do not match expected types!");
            }
        } else {
            log("[-] Error -> onPlayerRightClick method not found!");
        }
    }

    public static void printMappings() {
        for (Field field : Mapper.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object value = field.get(null);
                if (field.getType() == Method.class && value instanceof Method) {
                    Method m = (Method) value;
                    log("" + field.getName() + ": " + m.getDeclaringClass().getName() + "." + m.getName() + "()");
                } else if (field.getType() == Field.class && value instanceof Field) {
                    Field f = (Field) value;
                    log("" + field.getName() + ": " + f.getDeclaringClass().getName() + "." + f.getName());
                } else if (field.getType() == Class.class && value instanceof Class<?>) {
                    log("" + field.getName() + ": " + ((Class<?>) value).getName());
                }
            } catch (Exception e) {
                log("[-] Error -> Reflection error on field: " + field.getName());
            }
        }
    }

    public static boolean Check_ChatComponentText(Class<?> clazz) {
        if (!ChatComponentStyle.isAssignableFrom(clazz)) return false;
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isPrivate(field.getModifiers()) && Modifier.isFinal(field.getModifiers()) && field.getType() == String.class) {
                for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                    if (constructor.getParameterTypes().length == 1 && constructor.getParameterTypes()[0] == String.class) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean Check_ChatComponentStyle(Class<?> clazz) {
        if (!Modifier.isAbstract(clazz.getModifiers()) || !IChatComponent.isAssignableFrom(clazz)) return false;
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isProtected(field.getModifiers()) && List.class.isAssignableFrom(field.getType())) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericType;
                    return pt.getActualTypeArguments().length == 1 && pt.getActualTypeArguments()[0].equals(IChatComponent);
                }
            }
        }
        return false;
    }

    public static boolean Check_IChatComponent(Class<?> clazz) {
        if (!clazz.isInterface()) return false;
        for (Type iface : clazz.getGenericInterfaces()) {
            if (iface instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) iface;
                if (pt.getRawType() == Iterable.class && pt.getActualTypeArguments().length == 1 && pt.getActualTypeArguments()[0] == clazz) {
                    for (Method method : clazz.getMethods()) {
                        if (method.getReturnType() == clazz && method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == String.class) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Class<?> Check_S12PacketVelocity() {
        for (Class<?> clazz : ClassUtils.getClasses()) {
            if (PacketClass.isAssignableFrom(clazz)) {
                for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                    Class<?>[] params = constructor.getParameterTypes();
                    if (params.length == 4 && params[0] == int.class && params[1] == double.class && params[2] == double.class && params[3] == double.class) {
                        return clazz;
                    }
                }
            }
        }
        return null;
    }

    public static boolean Check_EntityPlayerSP(Class<?> cls) {
        boolean hasField = false, hasConstructor = false;
        for (Field f : cls.getDeclaredFields()) {
            if (Modifier.isProtected(f.getModifiers()) && f.getType().equals(Minecraft)) hasField = true;
        }
        for (Constructor<?> c : cls.getDeclaredConstructors()) {
            Class<?>[] p = c.getParameterTypes();
            if (p.length == 4 && p[0].equals(Minecraft)) {
                World = p[1];
                NetHandlerPlayClient = p[2];
                hasConstructor = true;
            }
        }
        return hasField && hasConstructor;
    }

    public static void detectMethods(Class<?> scaledClass, Object scaledInstance) {
        for (Method method : scaledClass.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0 && method.getReturnType() == int.class) {
                try {
                    method.setAccessible(true);
                    int value = (int) method.invoke(scaledInstance);
                    if (value >= 300 && value <= 2000) {
                         widthMethod = method;
                    } else if (value >= 1 && value <= 4) {
                        scaleFactorMethod = method;
                    } if (value >= 300 && value <= 2000) {
                        if (method.getName().contains(widthMethod.getName()))
                            continue;
                        heightMethod = method;
                    }
                } catch (Exception e) {
                    log("[-] Error -> detectMethods: " + e.getMessage());
                }
            }
        }
    }


    public static void SetMotionFields(Field field, double value) throws Exception {
        Object motionObj = field.get(getPlayer());
        Constructor<?> ctor = motionObj.getClass().getDeclaredConstructor(double.class);
        ctor.setAccessible(true);
        field.set(getPlayer(), ctor.newInstance(value));
    }

    public static Class<?> findContainerClassFromEntityPlayer(Class<?> entityPlayerClass) {
        Field[] fields = entityPlayerClass.getDeclaredFields();

        Map<Class<?>, List<Field>> typeToFields = new HashMap<>();

        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            if (fieldType.getName().contains("java.") || fieldType.getName().contains("javax.")) {
                continue;
            }
            if (!fieldType.isPrimitive()) {
                typeToFields.computeIfAbsent(fieldType, k -> new ArrayList<>()).add(field);
            }
        }

        for (Map.Entry<Class<?>, List<Field>> entry : typeToFields.entrySet()) {
            List<Field> fieldList = entry.getValue();
            if (fieldList.size() == 2) {
                return entry.getKey();
            }
        }

        return null;
    }

    public static boolean containsMinecraftFields(Class<?> cls) {
        boolean hasLogger = false, hasProxy = false, hasDisplay = false;
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            String name = f.getType().getName();
            int mod = f.getModifiers();
            if (name.equals("org.apache.logging.log4j.Logger") && Modifier.isStatic(mod) && Modifier.isFinal(mod))
                hasLogger = true;
            if (name.equals("java.net.Proxy") && Modifier.isFinal(mod)) hasProxy = true;
            if (name.equals("java.util.List") && f.getGenericType().getTypeName().contains("DisplayMode") && Modifier.isStatic(mod) && Modifier.isFinal(mod))
                hasDisplay = true;
        }
        return hasLogger && hasProxy && hasDisplay;
    }

    public static Object getPlayer() {
        try {
            Object mc = theMinecraft.get(null);
            return mc != null ? getThePlayer.invoke(mc) : null;
        } catch (Exception e) {
            log("[-] Error -> getPlayer: " + e.getMessage());
            return null;
        }
    }

    public static void ClearLogs() {
        try {
            File log = new File(LOG_PATH);
            if (!log.exists()) log.getParentFile().mkdirs();
            new FileOutputStream(log, false).close();
            File err = new File(ERR_PATH);
            if (!err.exists()) err.getParentFile().mkdirs();
            new FileOutputStream(err, false).close();
            logBuffer.setLength(0);
        } catch (IOException e) {
            System.err.println("Log clear error: " + e.getMessage());
        }
    }

    public static Method findGetSlot() {
        try {
            Class<?> containerClass = Container;
            Class<?> slotClass = Slot;

            for (Method method : containerClass.getMethods()) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1 || params[0] != int.class) continue;

                if (method.getReturnType() != slotClass) continue;

                if (!Modifier.isPublic(method.getModifiers())) continue;

                return method;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Field getListFieldOfType(Class<?> clazz, Class<?> type) {
        for (Field f : clazz.getDeclaredFields()) {
            if (List.class.isAssignableFrom(f.getType())) {
                java.lang.reflect.Type genType = f.getGenericType();
                if (genType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genType;
                    java.lang.reflect.Type[] args = pt.getActualTypeArguments();
                    if (args.length == 1 && args[0] instanceof Class<?>) {
                        if (args[0] == type) {
                            f.setAccessible(true);
                            return f;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Method findGetStackInSlotMethod(byte[] classBytes, Class<?> clazz) {
        ClassReader reader = new ClassReader(classBytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        Class<?> itemStackClass = ItemStack;
        String itemStackInternalName = itemStackClass.getName().replace('.', '/');

        for (MethodNode method : classNode.methods) {
            if (!method.desc.equals("(I)L" + itemStackInternalName + ";")) continue;

            if ((method.access & Opcodes.ACC_PUBLIC) == 0) continue;

            boolean hasArrayLoad = false;
            boolean hasArrayStore = false;
            boolean hasIndexCheck = false;
            boolean returnsArrayElement = false;

            for (AbstractInsnNode insn : method.instructions.toArray()) {
                int opcode = insn.getOpcode();

                if (opcode == Opcodes.AALOAD) hasArrayLoad = true;
                if (opcode == Opcodes.AASTORE) hasArrayStore = true;
                if (opcode == Opcodes.IF_ICMPLT || opcode == Opcodes.IF_ICMPGE ||
                        opcode == Opcodes.IF_ICMPGT || opcode == Opcodes.IF_ICMPLE) hasIndexCheck = true;
                if (opcode == Opcodes.ARETURN) returnsArrayElement = true;
            }

            if (hasArrayLoad && !hasArrayStore && hasIndexCheck && returnsArrayElement) {
                try {
                    Method reflMethod = clazz.getDeclaredMethod(method.name, int.class);
                    if (!java.lang.reflect.Modifier.isPublic(reflMethod.getModifiers())) {
                        continue;
                    }
                    reflMethod.setAccessible(true);
                    return reflMethod;
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static double getFieldValue(Field field, Object obj, String name) {
        try {
            field.setAccessible(true);
            return field.getDouble(obj);
        } catch (Exception e) {
            log("Error getting " + name + ": " + e.getMessage());
            return 0.0;
        }
    }

    public static void log(Object msg) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(LOG_PATH, true), StandardCharsets.UTF_8)) {
            LocalDateTime now = LocalDateTime.now();
            writer.write(msg + "\n");
        } catch (IOException e) {
            System.err.println("Log write error: " + e.getMessage());
        }
    }
}