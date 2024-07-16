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
package cn.mlus.portality.gui;

import cn.mlus.portality.Portality;
import cn.mlus.portality.network.PortalRenameMessage;
import cn.mlus.portality.tile.ControllerTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class RenameControllerScreen extends Screen {

    private final ControllerTile controller;
    private EditBox textFieldWidget;
    private Button confirm;

    public RenameControllerScreen(ControllerTile controller) {
        super(Component.literal(I18n.get("portality.gui.controller.rename")));
        this.controller = controller;
    }

    private class ConfirmButton extends Button{
        protected ConfirmButton(int p_259075_, int p_259271_, int p_260232_, int p_260028_, Component p_259351_) {
            super(p_259075_, p_259271_, p_260232_, p_260028_, p_259351_,button -> {}, supplier -> Component.empty());
        }

        @Override
        public void onPress() {
            Portality.NETWORK.get().sendToServer(new PortalRenameMessage(textFieldWidget.getValue(), controller.getBlockPos()));
            Minecraft.getInstance().setScreen(new ControllerScreen(controller));
        }
    }

    @Override
    protected void init() {
        super.init();
        int textFieldWidth = 140;
        textFieldWidget = new EditBox(Minecraft.getInstance().font, width / 2 - textFieldWidth / 2, height / 2 - 10, textFieldWidth, 18, Component.literal(""));
        textFieldWidget.setCanLoseFocus(false);
        textFieldWidget.setMaxLength(28);
        textFieldWidget.setHighlightPos(0);
        textFieldWidget.setValue(this.controller.getPortalDisplayName());
        textFieldWidget.setFocused(true);
        this.addRenderableWidget(textFieldWidget);
        //this.setFocused(this.textFieldWidget);

        confirm = new ConfirmButton(width / 2 + textFieldWidth / 2 + 5, height / 2 - 10, 50, 18, Component.literal("Confirm"));
        this.addRenderableWidget(confirm);
    }

    @Override
    public void render(@NotNull GuiGraphics matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(matrixStack);//draw tinted background
        super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
        //textFieldWidget.render(p_render_1_, p_render_2_, p_render_3_);
        String rename = I18n.get("portality.gui.controller.rename");
       matrixStack.drawString(Minecraft.getInstance().font,rename, width / 2 - Minecraft.getInstance().font.width(rename) / 2, height / 2 - 30, 0xFFFFFF);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() { //onClose

    }
}
