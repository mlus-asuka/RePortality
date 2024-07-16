/**
 * MIT License
 *
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package cn.mlus.portality;

import cn.mlus.portality.block.ControllerBlock;
import cn.mlus.portality.block.FrameBlock;
import cn.mlus.portality.block.GeneratorBlock;
import cn.mlus.portality.block.InterdimensionalModuleBlock;
import cn.mlus.portality.block.module.CapabilityEnergyModuleBlock;
import cn.mlus.portality.block.module.CapabilityFluidModuleBlock;
import cn.mlus.portality.block.module.CapabilityItemModuleBlock;
import cn.mlus.portality.item.TeleportationTokenItem;
import cn.mlus.portality.network.*;
import cn.mlus.portality.proxy.CommonProxy;
import cn.mlus.portality.proxy.PortalitySoundHandler;
import cn.mlus.portality.proxy.client.ClientProxy;
import cn.mlus.portality.tile.BasicFrameTile;
import cn.mlus.portality.tile.ControllerTile;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("portality")
public class Portality extends ModuleController {

    public static final String MOD_ID = "portality";
    public static NetworkHandler NETWORK = new NetworkHandler(MOD_ID);

    public static final TitaniumTab TITANIUM_TAB = new TitaniumTab(new ResourceLocation(MOD_ID, "main"));

    public static CommonProxy proxy;

    public Portality() {
        NETWORK.registerMessage(PortalPrivacyToggleMessage.class);
        NETWORK.registerMessage(PortalPrivacyToggleMessage.class);
        NETWORK.registerMessage(PortalRenameMessage.class);
        NETWORK.registerMessage(PortalNetworkMessage.Response.class);
        NETWORK.registerMessage(PortalLinkMessage.class);
        NETWORK.registerMessage(PortalCloseMessage.class);
        NETWORK.registerMessage(PortalTeleportMessage.class);
        NETWORK.registerMessage(PortalDisplayToggleMessage.class);
        NETWORK.registerMessage(PortalChangeColorMessage.class);
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        EventManager.mod(FMLCommonSetupEvent.class).process(this::onCommon).subscribe();
        EventManager.mod(FMLClientSetupEvent.class).process(this::onClient).subscribe();
        EventManager.forge(PlayerInteractEvent.RightClickBlock.class).filter(event -> !event.getLevel().isClientSide && event.getEntity().isCrouching() && event.getLevel().getBlockEntity(event.getPos()) instanceof ControllerTile && !event.getEntity().getItemInHand(event.getHand()).isEmpty()).process(event -> {
            ControllerTile controllerTile = (ControllerTile) event.getLevel().getBlockEntity(event.getPos());
            ItemStack stack = event.getEntity().getItemInHand(event.getHand());
            if (!stack.is(controllerTile.getDisplay().getItem())) {
                if (stack.getItem() instanceof TeleportationTokenItem){
                    if (stack.hasTag()){
                        controllerTile.addTeleportationToken(stack);
                        event.getEntity().displayClientMessage(MutableComponent
                                .create(new LiteralContents(("portility.controller.info.added_token"))).withStyle(ChatFormatting.GREEN), true);
                    }
                    return;
                }
                event.getEntity().displayClientMessage(MutableComponent
                        .create(new LiteralContents(("portility.controller.info.icon_changed"))).withStyle(ChatFormatting.GREEN), true);
                controllerTile.setDisplayNameEnabled(stack);
            }
        }).subscribe();
    }

    @Override
    protected void initModules() {
        CommonProxy.BLOCK_CONTROLLER = getRegistries().registerBlockWithTile("controller", ControllerBlock::new, Portality.TITANIUM_TAB);
        CommonProxy.BLOCK_FRAME = getRegistries().registerBlockWithTile( "frame", () -> new FrameBlock<>("frame", BasicFrameTile.class), Portality.TITANIUM_TAB);
        CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE = getRegistries().registerBlockWithTile("module_energy", CapabilityEnergyModuleBlock::new, Portality.TITANIUM_TAB);
        CommonProxy.BLOCK_CAPABILITY_FLUID_MODULE = getRegistries().registerBlockWithTile("module_fluids", CapabilityFluidModuleBlock::new, Portality.TITANIUM_TAB);
        CommonProxy.BLOCK_CAPABILITY_ITEM_MODULE = getRegistries().registerBlockWithTile("module_items", CapabilityItemModuleBlock::new, Portality.TITANIUM_TAB);
        CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE = getRegistries().registerBlockWithTile("module_interdimensional", InterdimensionalModuleBlock::new, Portality.TITANIUM_TAB);
        CommonProxy.BLOCK_GENERATOR = getRegistries().registerBlockWithTile("generator", GeneratorBlock::new, Portality.TITANIUM_TAB);
        CommonProxy.TELEPORTATION_TOKEN_ITEM = getRegistries().registerGeneric(ForgeRegistries.ITEMS.getRegistryKey(), "teleportation_token", TeleportationTokenItem::new);
        addCreativeTab("main",()->CommonProxy.BLOCK_CONTROLLER.getLeft().get().asItem().getDefaultInstance(),MutableComponent.create(new LiteralContents("portality")).getString(),TITANIUM_TAB);

        PortalitySoundHandler.PORTAL = getRegistries().registerGeneric(ForgeRegistries.SOUND_EVENTS.getRegistryKey(), "portal", () ->  SoundEvent.createFixedRangeEvent(new ResourceLocation(Portality.MOD_ID, "portal"),8));
        PortalitySoundHandler.PORTAL_TP = getRegistries().registerGeneric(ForgeRegistries.SOUND_EVENTS.getRegistryKey(), "portal_teleport",  () ->  SoundEvent.createFixedRangeEvent(new ResourceLocation(Portality.MOD_ID, "portal_teleport"),8));
    }

    public void onCommon(FMLCommonSetupEvent event) {
        proxy.onCommon();
    }

    public void onClient(FMLClientSetupEvent event) {
        proxy.onClient(Minecraft.getInstance());
    }

    public enum AuraType {
        PORTAL(new ResourceLocation(Portality.MOD_ID, "textures/blocks/player_render.png"), true),
        FORCE_FIELD(new ResourceLocation("textures/misc/forcefield.png"), true),
        UNDERWATER(new ResourceLocation("textures/misc/underwater.png"), true),
        SPOOK(new ResourceLocation("textures/misc/pumpkinblur.png"), false),
        END(new ResourceLocation("textures/environment/end_sky.png"), true),
        CLOUDS(new ResourceLocation("textures/environment/clouds.png"), true),
        RAIN(new ResourceLocation("textures/environment/rain.png"), true),
        SGA(new ResourceLocation("textures/font/ascii_sga.png"), true),
        ENCHANTED(new ResourceLocation("textures/misc/enchanted_item_glint.png"), true),
        BARS(new ResourceLocation("textures/gui/bars.png"), true),
        RECIPE_BOOK(new ResourceLocation("textures/gui/recipe_book.png"), true),
        END_PORTAL(new ResourceLocation("textures/entity/end_portal.png"), true),
        MOON(new ResourceLocation("textures/environment/moon_phases.png"), true);

        private final ResourceLocation resourceLocation;
        private final boolean enableBlend;

        AuraType(ResourceLocation resourceLocation, boolean enableBlend) {
            this.resourceLocation = resourceLocation;
            this.enableBlend = enableBlend;
        }

        public ResourceLocation getResourceLocation() {
            return resourceLocation;
        }

        public boolean isEnableBlend() {
            return enableBlend;
        }
    }

}
