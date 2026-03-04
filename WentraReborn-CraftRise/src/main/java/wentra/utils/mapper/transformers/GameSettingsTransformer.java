package wentra.utils.mapper.transformers;

import org.objectweb.asm.*;
import wentra.utils.mapper.Mapper;
import wentra.utils.mapper.transformers.etc.impl.utils.Reflector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static wentra.utils.mapper.Mapper.*;

public class GameSettingsTransformer {

    public static Class<?> getTargetClass() {
        return Mapper.GameSettings;
    }

    public static byte[] transform(byte[] classBytes) {
        ClassReader cr = new ClassReader(classBytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        List<FieldInfo> keyBindFields = new ArrayList<>();

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                // public GameSettings(Minecraft, File) constructor'ını bul
                if (name.equals("<init>") && desc.contains(Minecraft.getName().replace('.', '/')) && desc.contains("Ljava/io/File;")) {
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        private int keyBindCount = 0;

                        @Override
                        public void visitTypeInsn(int opcode, String type) {
                            if (opcode == Opcodes.NEW && type.equals(Mapper.KeyBinding.getName().replace('.', '/'))) {
                                keyBindCount++;
                            }
                            super.visitTypeInsn(opcode, type);
                        }

                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                            if (opcode == Opcodes.PUTFIELD &&
                                    owner.equals(Type.getInternalName(getTargetClass())) &&
                                    desc.equals("L" + Mapper.KeyBinding.getName().replace('.', '/') + ";") &&
                                    keyBindCount <= 7 && keyBindCount > 0) {
                                // İlk 7 KeyBinding atamasını sırayla yakala
                                keyBindFields.add(new FieldInfo(name, desc));
                            }
                            super.visitFieldInsn(opcode, owner, name, desc);
                        }

                        @Override
                        public void visitEnd() {
                            try {
                                // Key binding alanlarını Mapper'a ata
                                if (keyBindFields.size() >= 7) {
                                    // 1. keyBindForward
                                    FieldInfo forward = keyBindFields.get(0);
                                    Mapper.keyBindForward = Reflector.findField(getTargetClass(), forward.name, forward.desc);
                                    if (Mapper.keyBindForward != null) {
                                    }

                                    // 2. keyBindLeft
                                    FieldInfo left = keyBindFields.get(1);
                                    Mapper.keyBindLeft = Reflector.findField(getTargetClass(), left.name, left.desc);
                                    if (Mapper.keyBindLeft != null) {
                                    }

                                    // 3. keyBindBack
                                    FieldInfo back = keyBindFields.get(2);
                                    Mapper.keyBindBack = Reflector.findField(getTargetClass(), back.name, back.desc);
                                    if (Mapper.keyBindBack != null) {
                                    }

                                    // 4. keyBindRight
                                    FieldInfo right = keyBindFields.get(3);
                                    Mapper.keyBindRight = Reflector.findField(getTargetClass(), right.name, right.desc);
                                    if (Mapper.keyBindRight != null) {
                                    }

                                    // 5. keyBindJump
                                    FieldInfo jump = keyBindFields.get(4);
                                    Mapper.keyBindJump = Reflector.findField(getTargetClass(), jump.name, jump.desc);
                                    if (Mapper.keyBindJump != null) {
                                    }

                                    // 6. keyBindSneak
                                    FieldInfo sneak = keyBindFields.get(5);
                                    Mapper.keyBindSneak = Reflector.findField(getTargetClass(), sneak.name, sneak.desc);
                                    if (Mapper.keyBindSneak != null) {
                                    }

                                    // 7. keyBindSprint
                                    FieldInfo sprint = keyBindFields.get(6);
                                    Mapper.keyBindSprint = Reflector.findField(getTargetClass(), sprint.name, sprint.desc);
                                    if (Mapper.keyBindSprint != null) {
                                    }
                                } else {
                                    Mapper.log("[GameSettingsTransformer] [ERROR] Insufficient key binding fields found: " + keyBindFields.size());
                                }
                            } catch (Exception e) {
                                Mapper.log("[GameSettingsTransformer] [ERROR] Failed to map key bindings: " + e.getMessage());
                            }
                            super.visitEnd();
                        }
                    };
                }
                return mv;
            }

            @Override
            public void visitEnd() {
                try {
                    // Classes
                    log("[Class Minecraft - net/minecraft/client/Minecraft --> " + (Mapper.Minecraft != null ? Mapper.Minecraft.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/chat/ChatComponentStyle --> " + (Mapper.ChatComponentStyle != null ? Mapper.ChatComponentStyle.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/chat/ChatComponentText --> " + (Mapper.ChatComponentText != null ? Mapper.ChatComponentText.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/chat/IChatComponent --> " + (Mapper.IChatComponent != null ? Mapper.IChatComponent.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/gui/FontRenderer --> " + (Mapper.FontRenderer != null ? Mapper.FontRenderer.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/gui/Gui --> " + (Mapper.Gui != null ? Mapper.Gui.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/gui/GuiIngame --> " + (Mapper.GuiIngame != null ? Mapper.GuiIngame.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/gui/GuiScreen --> " + (Mapper.GuiScreen != null ? Mapper.GuiScreen.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/gui/inventory/GuiChest --> " + (Mapper.GuiChest != null ? Mapper.GuiChest.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/gui/GuiContainer --> " + (GuiContainer != null ? GuiContainer.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/gui/Container --> " + (Container != null ? Container.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/gui/ScaledResolution --> " + (Mapper.ScaledResolution != null ? Mapper.ScaledResolution.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/particle/EffectRenderer --> " + (Mapper.EffectRenderer != null ? Mapper.EffectRenderer.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/renderer/RenderManager --> " + (Mapper.RenderManager != null ? Mapper.RenderManager.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/renderer/texture/AbstractTexture --> " + (Mapper.AbstractTexture != null ? Mapper.AbstractTexture.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/renderer/texture/DynamicTexture --> " + (Mapper.DynamicTexture != null ? Mapper.DynamicTexture.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/renderer/texture/TextureManager --> " + (Mapper.TextureManager != null ? Mapper.TextureManager.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/settings/GameSettings --> " + (Mapper.GameSettings != null ? Mapper.GameSettings.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/settings/KeyBinding --> " + (Mapper.KeyBinding != null ? Mapper.KeyBinding.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/entity/AbstractClientPlayer --> " + (Mapper.AbstractClientPlayer != null ? Mapper.AbstractClientPlayer.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/item/ItemBlock --> " + (Mapper.ItemBlock != null ? Mapper.ItemBlock.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/item/Item --> " + (Mapper.Item != null ? Mapper.Item.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/block/Block --> " + (Mapper.Block != null ? Mapper.Block.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/entity/Entity --> " + (Mapper.EntityClass != null ? Mapper.EntityClass.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/entity/EntityLivingBase --> " + (Mapper.EntityLivingBase != null ? Mapper.EntityLivingBase.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/entity/EntityPlayer --> " + (Mapper.EntityPlayer != null ? Mapper.EntityPlayer.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/entity/EntityPlayerSP --> " + (Mapper.EntityPlayerSP != null ? Mapper.EntityPlayerSP.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/multiplayer/PlayerControllerMP --> " + (Mapper.PlayerControllerMP != null ? Mapper.PlayerControllerMP.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/network/NetworkHandler --> " + (Mapper.NetworkHandler != null ? Mapper.NetworkHandler.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/network/NetworkManager --> " + (Mapper.NetworkManager != null ? Mapper.NetworkManager.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/network/NetworkPlayerInfo --> " + (Mapper.NetworkPlayerInfo != null ? Mapper.NetworkPlayerInfo.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/network/packet/C02PacketUseEntity --> " + (Mapper.C02PacketUseEntity != null ? Mapper.C02PacketUseEntity.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/network/packet/C02PacketUseEntityAction --> " + (Mapper.C02PacketUseEntityAction != null ? Mapper.C02PacketUseEntityAction.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/network/packet/C0APacketAnimation --> " + (Mapper.C0APacketAnimation != null ? Mapper.C0APacketAnimation.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/network/packet/Packet --> " + (Mapper.PacketClass != null ? Mapper.PacketClass.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/network/packet/S12PacketEntityVelocity --> " + (Mapper.S12PacketEntityVelocity != null ? Mapper.S12PacketEntityVelocity.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/network/packet/S18PacketEntityTeleport --> " + (Mapper.S18PacketEntityTeleport != null ? Mapper.S18PacketEntityTeleport.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/client/resources/ResourceLocation --> " + (Mapper.ResourceLocation != null ? Mapper.ResourceLocation.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/world/World --> " + (Mapper.World != null ? Mapper.World.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/item/ItemStack --> " + (Mapper.ItemStack != null ? Mapper.ItemStack.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/inventory/Slot --> " + (Mapper.Slot != null ? Mapper.Slot.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/inventory/IInventory --> " + (Mapper.IInventory != null ? Mapper.IInventory.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/inventory/InventoryPlayer --> " + (Mapper.InventoryPlayer != null ? Mapper.InventoryPlayer.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/util/BlockPos --> " + (Mapper.BlockPos != null ? Mapper.BlockPos.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/util/EnumFacing --> " + (Mapper.EnumFacing != null ? Mapper.EnumFacing.getName() : "null"));
                    log("[Class Minecraft - net/minecraft/util/Vec3 --> " + (Mapper.Vec3 != null ? Mapper.Vec3.getName() : "null"));
                    log("[Class CraftRise - craftrise/Config --> " + (Mapper.Config != null ? Mapper.Config.getName() : "null"));
                    log("[Class CraftRise - craftrise/FloatContainer --> " + (Mapper.FloatContainer != null ? Mapper.FloatContainer.getName() : "null"));
                    log("[Class CraftRise - craftrise/MotionContainer --> " + (Mapper.MotionContainer != null ? Mapper.MotionContainer.getName() : "null"));
                    log("[Class CraftRise - craftrise/BooleanContainer --> " + (Mapper.BooleanContainer != null ? Mapper.BooleanContainer.getName() : "null"));

                    // Methods
                    log("[Method Minecraft - net/minecraft/util/BlockPos.getBlock --> " + (Mapper.BlockPos != null ? BlockPos.getName() + "." + (Mapper.getBlock != null ? Mapper.getBlock.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/particle/EffectRenderer.renderParticles --> " + (Mapper.EffectRenderer != null ? Mapper.EffectRenderer.getName() + "." + (Mapper.renderParticles != null ? Mapper.renderParticles.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/FontRenderer.getStringWidth --> " + (Mapper.FontRenderer != null ? Mapper.FontRenderer.getName() + "." + (getStringWidth != null ? getStringWidth.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/Slot.getStack --> " + (getStack != null ? getStack.getDeclaringClass().getName().replace('/', '.') + "." + getStack.getName() : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/Container.getSlot --> " + (getSlot != null ? getSlot.getDeclaringClass().getName().replace('/', '.') + "." + getSlot.getName() : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/Gui.drawModalRectWithCustomSizedTexture --> " + (Mapper.Gui != null ? Mapper.Gui.getName() + "." + (drawModalRectWithCustomSizedTexture != null ? drawModalRectWithCustomSizedTexture.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/Gui.drawRect --> " + (Mapper.Gui != null ? Mapper.Gui.getName() + "." + (drawRect != null ? drawRect.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/Gui.drawScaledCustomSizeModalRect --> " + (Mapper.Gui != null ? Mapper.Gui.getName() + "." + (drawScaledCustomSizeModalRect != null ? drawScaledCustomSizeModalRect.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/GuiIngame.renderGameOverlay --> " + (Mapper.GuiIngame != null ? Mapper.GuiIngame.getName() + "." + (renderGameOverlay != null ? renderGameOverlay.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/GuiScreen.drawScreen --> " + (Mapper.GuiScreen != null ? Mapper.GuiScreen.getName() + "." + (drawScreen != null ? drawScreen.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/GuiScreen.keyTyped --> " + (Mapper.GuiScreen != null ? Mapper.GuiScreen.getName() + "." + (keyTyped != null ? keyTyped.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/GuiScreen.mouseClicked --> " + (Mapper.GuiScreen != null ? Mapper.GuiScreen.getName() + "." + (mouseClicked != null ? mouseClicked.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/GuiScreen.mouseReleased --> " + (Mapper.GuiScreen != null ? Mapper.GuiScreen.getName() + "." + (mouseReleased != null ? mouseReleased.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/Minecraft.displayGuiScreen --> " + (Mapper.Minecraft != null ? Mapper.Minecraft.getName() + "." + (displayGuiScreen != null ? displayGuiScreen.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/Minecraft.getTextureManager --> " + (Mapper.Minecraft != null ? Mapper.Minecraft.getName() + "." + (getTextureManager != null ? getTextureManager.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/Minecraft.getThePlayer --> " + (Mapper.Minecraft != null ? Mapper.Minecraft.getName() + "." + (getThePlayer != null ? getThePlayer.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/renderer/texture/AbstractTexture.getGlTextureId --> " + (Mapper.AbstractTexture != null ? Mapper.AbstractTexture.getName() + "." + (getGlTextureId != null ? getGlTextureId.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/renderer/texture/TextureManager.bindTexture --> " + (Mapper.TextureManager != null ? Mapper.TextureManager.getName() + "." + (bindTexture != null ? bindTexture.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/settings/KeyBinding.isKeyDown --> " + (Mapper.KeyBinding != null ? Mapper.KeyBinding.getName() + "." + (isKeyDown != null ? isKeyDown.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/world/World.isAirBlock --> " + (Mapper.World != null ? Mapper.World.getName() + "." + (isAirBlock != null ? isAirBlock.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/settings/KeyBinding.onTick --> " + (Mapper.KeyBinding != null ? Mapper.KeyBinding.getName() + "." + (KeyBinding_onTick != null ? KeyBinding_onTick.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/settings/KeyBinding.setKeyBindState --> " + (Mapper.KeyBinding != null ? Mapper.KeyBinding.getName() + "." + (setKeyBindState != null ? setKeyBindState.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/entity/Entity.getDistanceToEntity --> " + (Mapper.EntityClass != null ? Mapper.EntityClass.getName() + "." + (getDistanceToEntity != null ? getDistanceToEntity.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/entity/Entity.getEyeHeight --> " + (Mapper.EntityClass != null ? Mapper.EntityClass.getName() + "." + (getEyeHeight != null ? getEyeHeight.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/entity/Entity.getFlag --> " + (Mapper.EntityClass != null ? Mapper.EntityClass.getName() + "." + (getFlag != null ? getFlag.getName() : "null") : "null"));
                    //log("[Method Minecraft - net/minecraft/entity/Entity.setPositionAndRotation --> " + (Mapper.EntityClass != null ? Mapper.EntityClass.getName() + "." + (setPositionAndRotation != null ? setPositionAndRotation.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/entity/Entity.setPositionAndUpdate --> " + (Mapper.EntityClass != null ? Mapper.EntityClass.getName() + "." + (setPositionAndUpdate != null ? setPositionAndUpdate.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/entity/AbstractClientPlayer.getLocationSkin --> " + (Mapper.AbstractClientPlayer != null ? Mapper.AbstractClientPlayer.getName() + "." + (getLocationSkin != null ? getLocationSkin.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/entity/AbstractClientPlayer.getLocationCape --> " + (Mapper.AbstractClientPlayer != null ? Mapper.AbstractClientPlayer.getName() + "." + (getLocationCape != null ? getLocationCape.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/renderer/entity/RenderManager.setRenderPosition --> " + (Mapper.RenderManager != null ? Mapper.RenderManager.getName() + "." + (setRenderPosition != null ? setRenderPosition.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/renderer/entity/RenderManager.renderWitherSkull --> " + (Mapper.RenderManager != null ? Mapper.RenderManager.getName() + "." + (renderWitherSkull != null ? renderWitherSkull.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/renderer/entity/RenderManager.getDistanceToCamera --> " + (Mapper.RenderManager != null ? Mapper.RenderManager.getName() + "." + (getDistanceToCamera != null ? getDistanceToCamera.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/entity/EntityLivingBase.getEntityHealth --> " + (Mapper.EntityLivingBase != null ? Mapper.EntityLivingBase.getName() + "." + (getEntityHealth != null ? getEntityHealth.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/entity/EntityPlayer.getHeldItem --> " + (Mapper.EntityPlayer != null ? Mapper.EntityPlayer.getName() + "." + (getHeldItem != null ? getHeldItem.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/entity/EntityPlayerSP.addChatMessage --> " + (Mapper.EntityPlayerSP != null ? Mapper.EntityPlayerSP.getName() + "." + (addChatMessage != null ? addChatMessage.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/network/NetHandlerPlayClient.sendPacket --> " + (Mapper.NetHandlerPlayClient != null ? Mapper.NetHandlerPlayClient.getName() + "." + (sendPacket != null ? sendPacket.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/ScaledResolution.scaleFactorMethod --> " + (Mapper.ScaledResolution != null ? Mapper.ScaledResolution.getName() + "." + (scaleFactorMethod != null ? scaleFactorMethod.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/gui/ScaledResolution.widthMethod --> " + (Mapper.ScaledResolution != null ? Mapper.ScaledResolution.getName() + "." + (widthMethod != null ? widthMethod.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/multiplayer/PlayerControllerMP.windowClick --> " + (Mapper.PlayerControllerMP != null ? PlayerControllerMP.getName() + "." + (windowClick != null ? windowClick.getName() : "null") : "null"));
                    log("[Method Minecraft - net/minecraft/client/multiplayer/PlayerControllerMP.onPlayerRightClick --> " + (Mapper.PlayerControllerMP != null ? Mapper.PlayerControllerMP.getName() + "." + (onPlayerRightClick != null ? onPlayerRightClick.getName() : "null") : "null"));
                    log("[Method CraftRise - craftrise/FloatContainer.getValueFloat --> " + (Mapper.FloatContainer != null ? Mapper.FloatContainer.getName() + "." + (getValueFloat != null ? getValueFloat.getName() : "null") : "null"));
                    log("[Method CraftRise - craftrise/MotionContainer.getValueMotion --> " + (Mapper.MotionContainer != null ? Mapper.MotionContainer.getName() + "." + (getValueMotion != null ? getValueMotion.getName() : "null") : "null"));
                    log("[Method CraftRise - craftrise/BooleanContainer.getValueBoolean --> " + (Mapper.BooleanContainer != null ? Mapper.BooleanContainer.getName() + "." + (getValueBoolean != null ? getValueBoolean.getName() : "null") : "null"));

                    // Fields with full class path
                    log("[Field Minecraft - net/minecraft/client/network/NetworkPlayerInfo.locationSkin --> " + (locationSkin != null ? locationSkin.getDeclaringClass().getName().replace('/', '.') + "." + locationSkin.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/network/NetworkPlayerInfo.locationCape --> " + (locationCape != null ? locationCape.getDeclaringClass().getName().replace('/', '.') + "." + locationCape.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/network/NetworkPlayerInfo.CRlocationCape --> " + (CRlocationCape != null ? CRlocationCape.getDeclaringClass().getName().replace('/', '.') + "." + CRlocationCape.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/network/NetworkPlayerInfo.playerInfo --> " + (playerInfo != null ? playerInfo.getDeclaringClass().getName().replace('/', '.') + "." + playerInfo.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/gui/Slot.inventorySlots --> " + (inventorySlots != null ? inventorySlots.getDeclaringClass().getName().replace('/', '.') + "." + inventorySlots.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/gui/Container.inventorySlots --> " + (Container_inventorySlots != null ? Container_inventorySlots.getDeclaringClass().getName().replace('/', '.') + "." + Container_inventorySlots.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/gui/Container.windowId --> " + (windowId != null ? windowId.getDeclaringClass().getName().replace('/', '.') + "." + windowId.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/Minecraft.currentScreen --> " + (currentScreen != null ? currentScreen.getDeclaringClass().getName().replace('/', '.') + "." + currentScreen.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/Minecraft.fontRendererObj --> " + (fontRendererObj != null ? fontRendererObj.getDeclaringClass().getName().replace('/', '.') + "." + fontRendererObj.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/Minecraft.gameSettings --> " + (gameSettings != null ? gameSettings.getDeclaringClass().getName().replace('/', '.') + "." + gameSettings.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/Minecraft.renderEngine --> " + (renderEngine != null ? renderEngine.getDeclaringClass().getName().replace('/', '.') + "." + renderEngine.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/Minecraft.renderManager --> " + (renderManager != null ? renderManager.getDeclaringClass().getName().replace('/', '.') + "." + renderManager.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/Minecraft.playerController --> " + (playerController != null ? playerController.getDeclaringClass().getName().replace('/', '.') + "." + playerController.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/Minecraft.theMinecraft --> " + (theMinecraft != null ? theMinecraft.getDeclaringClass().getName().replace('/', '.') + "." + theMinecraft.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.isInWeb --> " + (isInWeb != null ? isInWeb.getDeclaringClass().getName().replace('/', '.') + "." + isInWeb.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.fallDistance --> " + (fallDistance != null ? fallDistance.getDeclaringClass().getName().replace('/', '.') + "." + fallDistance.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.isSwingInProgress --> " + (isSwingInProgress != null ? isSwingInProgress.getDeclaringClass().getName().replace('/', '.') + "." + isSwingInProgress.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.onGround --> " + (onGround != null ? onGround.getDeclaringClass().getName().replace('/', '.') + "." + onGround.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.ticksExisted --> " + (ticksExisted != null ? ticksExisted.getDeclaringClass().getName().replace('/', '.') + "." + ticksExisted.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.rotationPitch --> " + (rotationPitch != null ? rotationPitch.getDeclaringClass().getName().replace('/', '.') + "." + rotationPitch.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.rotationYaw --> " + (rotationYaw != null ? rotationYaw.getDeclaringClass().getName().replace('/', '.') + "." + rotationYaw.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/entity/Entity.prevRotationYaw --> " + (prevRotationYaw != null ? prevRotationYaw.getDeclaringClass().getName().replace('/', '.') + "." + prevRotationYaw.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/entity/Entity.prevRotationPitch --> " + (prevRotationPitch != null ? prevRotationPitch.getDeclaringClass().getName().replace('/', '.') + "." + prevRotationPitch.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.posX --> " + (posX != null ? posX.getDeclaringClass().getName().replace('/', '.') + "." + posX.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.posY --> " + (posY != null ? posY.getDeclaringClass().getName().replace('/', '.') + "." + posY.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.posZ --> " + (posZ != null ? posZ.getDeclaringClass().getName().replace('/', '.') + "." + posZ.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.lastTickPosX --> " + (lastTickPosX != null ? lastTickPosX.getDeclaringClass().getName().replace('/', '.') + "." + lastTickPosX.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.lastTickPosY --> " + (lastTickPosY != null ? lastTickPosY.getDeclaringClass().getName().replace('/', '.') + "." + lastTickPosY.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.lastTickPosZ --> " + (lastTickPosZ != null ? lastTickPosZ.getDeclaringClass().getName().replace('/', '.') + "." + lastTickPosZ.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/renderer/entity/RenderManager.viewerPosX --> " + (viewerPosX != null ? viewerPosX.getDeclaringClass().getName().replace('/', '.') + "." + viewerPosX.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/renderer/entity/RenderManager.viewerPosY --> " + (viewerPosY != null ? viewerPosY.getDeclaringClass().getName().replace('/', '.') + "." + viewerPosY.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/renderer/entity/RenderManager.viewerPosZ --> " + (viewerPosZ != null ? viewerPosZ.getDeclaringClass().getName().replace('/', '.') + "." + viewerPosZ.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/renderer/entity/RenderManager.renderPosX --> " + (renderPosX != null ? renderPosX.getDeclaringClass().getName().replace('/', '.') + "." + renderPosX.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/renderer/entity/RenderManager.renderPosY --> " + (renderPosY != null ? renderPosY.getDeclaringClass().getName().replace('/', '.') + "." + renderPosY.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/renderer/entity/RenderManager.renderPosZ --> " + (renderPosZ != null ? renderPosZ.getDeclaringClass().getName().replace('/', '.') + "." + renderPosZ.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/renderer/entity/RenderManager.renderManagerF --> " + (renderManagerF != null ? renderManagerF.getDeclaringClass().getName().replace('/', '.') + "." + renderManagerF.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/EntityLivingBase.moveStrafing --> " + (moveStrafing != null ? moveStrafing.getDeclaringClass().getName().replace('/', '.') + "." + moveStrafing.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/EntityLivingBase.moveForward --> " + (moveForward != null ? moveForward.getDeclaringClass().getName().replace('/', '.') + "." + moveForward.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.motionX --> " + (motionX != null ? motionX.getDeclaringClass().getName().replace('/', '.') + "." + motionX.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.motionY --> " + (motionY != null ? motionY.getDeclaringClass().getName().replace('/', '.') + "." + motionY.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.motionZ --> " + (motionZ != null ? motionZ.getDeclaringClass().getName().replace('/', '.') + "." + motionZ.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/EntityPlayerSP.sendQueue --> " + (sendQueue != null ? sendQueue.getDeclaringClass().getName().replace('/', '.') + "." + sendQueue.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/network/NetworkHandler.channel --> " + (channel != null ? channel.getDeclaringClass().getName().replace('/', '.') + "." + channel.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/world/World.playerEntitiesList --> " + (playerEntitiesList != null ? playerEntitiesList.getDeclaringClass().getName().replace('/', '.') + "." + playerEntitiesList.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/entity/Entity.worldObj --> " + (worldObj != null ? worldObj.getDeclaringClass().getName().replace('/', '.') + "." + worldObj.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/settings/GameSettings.thirdPersonView --> " + (thirdPersonView != null ? thirdPersonView.getDeclaringClass().getName().replace('/', '.') + "." + thirdPersonView.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/gui/GuiIngame.partialTicks --> " + (partialTicks != null ? partialTicks.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/settings/KeyBinding.KeyBindPressed --> " + (KeyBindPressed != null ? KeyBindPressed.getDeclaringClass().getName().replace('/', '.') + "." + KeyBindPressed.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/settings/GameSettings.keyBindForward --> " + (keyBindForward != null ? keyBindForward.getDeclaringClass().getName().replace('/', '.') + "." + keyBindForward.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/settings/GameSettings.keyBindLeft --> " + (keyBindLeft != null ? keyBindLeft.getDeclaringClass().getName().replace('/', '.') + "." + keyBindLeft.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/settings/GameSettings.keyBindBack --> " + (keyBindBack != null ? keyBindBack.getDeclaringClass().getName().replace('/', '.') + "." + keyBindBack.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/settings/GameSettings.keyBindRight --> " + (keyBindRight != null ? keyBindRight.getDeclaringClass().getName().replace('/', '.') + "." + keyBindRight.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/settings/GameSettings.keyBindJump --> " + (keyBindJump != null ? keyBindJump.getDeclaringClass().getName().replace('/', '.') + "." + keyBindJump.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/settings/GameSettings.keyBindSneak --> " + (keyBindSneak != null ? keyBindSneak.getDeclaringClass().getName().replace('/', '.') + "." + keyBindSneak.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/client/settings/GameSettings.keyBindSprint --> " + (keyBindSprint != null ? keyBindSprint.getDeclaringClass().getName().replace('/', '.') + "." + keyBindSprint.getName() : "null"));
                    log("[Field Minecraft - net/minecraft/network/packet/C02PacketUseEntity.entityId --> " + (Reflector.findPrivateIntField(Mapper.C02PacketUseEntity) != null ? Mapper.C02PacketUseEntity.getName() + "." + Reflector.findPrivateIntField(Mapper.C02PacketUseEntity).getName() : "null"));
                    log("[Field Minecraft - net/minecraft/network/packet/C02PacketUseEntity.action --> " + (Reflector.getFieldByType(Mapper.C02PacketUseEntity, Mapper.C02PacketUseEntityAction) != null ? Mapper.C02PacketUseEntity.getName() + "." + Reflector.getFieldByType(Mapper.C02PacketUseEntity, Mapper.C02PacketUseEntityAction).getName() : "null"));
                    log("[Field Minecraft - net/minecraft/network/packet/C02PacketUseEntityAction.ATTACK --> " + (Mapper.C02PacketUseEntityAction != null ? Mapper.C02PacketUseEntityAction.getName() + ".ATTACK" : "null"));

                } catch (Exception e) {
                    Mapper.log("[GameSettingsTransformer] [ERROR] Failed to log mappings: " + e.getMessage());
                }

                super.visitEnd();
            }
        };

        cr.accept(cv, 0);
        byte[] transformed = cw.toByteArray();

        try {
            String path = "C:\\Users\\" + System.getenv("username") +
                    "\\AppData\\Roaming\\.craftrise\\libraries\\GameSettings_Transformed.class";
            File file = new File(path);
            file.getParentFile().mkdirs();
            Files.write(Paths.get(file.getAbsolutePath()), transformed);
        } catch (IOException e) {
            Mapper.log("[GameSettingsTransformer] [ERROR] Failed to write transformed class: " + e.getMessage());
        }

        return transformed;
    }

    private static class FieldInfo {
        String name;
        String desc;

        FieldInfo(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }
}