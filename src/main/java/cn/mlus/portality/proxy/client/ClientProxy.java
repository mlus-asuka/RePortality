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
package cn.mlus.portality.proxy.client;

import cn.mlus.portality.proxy.client.render.AuraRender;
import cn.mlus.portality.proxy.client.render.TESRPortal;
import cn.mlus.portality.proxy.CommonProxy;
import cn.mlus.portality.tile.ControllerTile;
import com.hrznstudio.titanium.event.handler.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        EventManager.mod(EntityRenderersEvent.RegisterRenderers.class).process(registerRenderers -> {
            registerRenderers.registerBlockEntityRenderer((BlockEntityType<? extends ControllerTile>) CommonProxy.BLOCK_CONTROLLER.getRight().get(), TESRPortal::new);
        }).subscribe();
        EventManager.mod(EntityRenderersEvent.AddLayers.class).process(registerRenderers -> {
            for (String skin : registerRenderers.getSkins()) {
                PlayerRenderer renderer = registerRenderers.getSkin(skin);
                renderer.addLayer(new AuraRender(renderer));
            }
        }).subscribe();

//        EventManager.mod(TextureStitchEvent.Pre.class).process(pre -> {
//            if (pre.getAtlas().equals(InventoryMenu.BLOCK_ATLAS)){
//                pre.addSprite(TESRPortal.TEXTURE);
//            }
//        }).subscribe();
    }

    @Override
    public void onClient(Minecraft instance) {
        super.onClient(instance);
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_CONTROLLER.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_FRAME.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_CAPABILITY_FLUID_MODULE.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE.getLeft().get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CommonProxy.BLOCK_CAPABILITY_ITEM_MODULE.getLeft().get(), RenderType.cutout());
        Minecraft.getInstance().getBlockColors().register((state, world, pos, index) -> {
            if (index == 0 && world != null) {
                BlockEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof IPortalColor) {
                    return ((IPortalColor) tileEntity).getColor();
                }
            }
            return -16739073;
        }, CommonProxy.BLOCK_FRAME.getLeft().get(), CommonProxy.BLOCK_CONTROLLER.getLeft().get(), CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE.getLeft().get(), CommonProxy.BLOCK_CAPABILITY_FLUID_MODULE.getLeft().get(), CommonProxy.BLOCK_CAPABILITY_ITEM_MODULE.getLeft().get(), CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE.getLeft().get());
    }
}
