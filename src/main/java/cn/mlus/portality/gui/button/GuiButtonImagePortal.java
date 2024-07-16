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
package cn.mlus.portality.gui.button;


import cn.mlus.portality.Portality;
import cn.mlus.portality.data.PortalInformation;
import cn.mlus.portality.gui.PortalsScreen;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GuiButtonImagePortal extends ImageButton {

    private final PortalInformation information;
    private PortalsScreen portals;

    public GuiButtonImagePortal(PortalsScreen guiPortals, PortalInformation information, int x, int y, int xSize, int ySize, int textureX, int textureY, int offset, ResourceLocation location) {
        super(x, y, xSize, ySize, textureX, textureY, offset, location, p_onPress_1_ -> {
        });
        this.information = information;
        this.portals = guiPortals;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics stack, int x, int y, float p_renderButton_3_) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        super.renderWidget(stack, this.getX(),this.getY(), p_renderButton_3_);
        stack.pose().pushPose();
        //RenderSystem.setupGui3DDiffuseLighting();
        stack.renderItem(information.getDisplay(),  this.getX() + 5, this.getY() + 3);
        Font fontRenderer = Minecraft.getInstance().font;
        ChatFormatting color = ChatFormatting.RESET;
        if (information.isPrivate()) color = ChatFormatting.GOLD;
        if (information.isActive()) color = ChatFormatting.RED;
        fontRenderer.drawInBatch(color + information.getName().substring(0, Math.min(information.getName().length(), 25)), this.getX() + 28,
                7 + this.getY(), isMouseOver(x, y) ? 16777120 : 0xFFFFFFFF,true,stack.pose().last().pose(),stack.bufferSource(), Font.DisplayMode.NORMAL,0,15728880);
        //fontRenderer.drawString(color + (information.isPrivate() ? I18n.format("portality.display.private") : I18n.format("portality.display.public")), x + 40, 10 + (fontRenderer.FONT_HEIGHT + 1) * 1 + y, 0xFFFFFF);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        ResourceLocation lock = new ResourceLocation(Portality.MOD_ID, "textures/gui/lock.png");
        if (information.isPrivate()) {
            RenderSystem.disableDepthTest();

            RenderSystem.setShaderTexture(0, lock);
            stack.blit(lock,this.getX() + 4, this.getY() + 14, 0, 0, 8, 8, 8, 8);
            RenderSystem.enableDepthTest();
        }
        Lighting.setupFor3DItems();
        if (portals.getSelectedPortal() == information) {
            RenderSystem.setShaderTexture(0, new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
            stack.blit(lock,this.getX(), this.getY(), 0, 210, 157, 22);
        }
        stack.pose().popPose();
    }


    public PortalInformation getInformation() {
        return information;
    }
}
