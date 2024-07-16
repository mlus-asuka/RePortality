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
import cn.mlus.portality.data.PortalInformation;
import cn.mlus.portality.gui.button.PortalCallButton;
import cn.mlus.portality.tile.ControllerTile;
import cn.mlus.portality.gui.button.GuiButtonImagePortal;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.ScreenAddonScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PortalsScreen extends ScreenAddonScreen {

    private final int guiHeight;
    private final int guiWidth;
    private List<PortalInformation> informationList;
    private EditBox textField;
    private double scrolling;
    private double lastScrolling;
    private boolean isDragging;
    private int visiblePortalInformations;
    private List<GuiButtonImagePortal> portalButtons;
    private List<PortalInformation> currentlyShowing;
    private PortalInformation selectedPortal;
    private final ControllerTile controller;

    public PortalsScreen(ControllerTile controller) {
        super(PortalityAssetProvider.PROVIDER, false);
        this.guiWidth = 200;
        this.guiHeight = 186;
        this.scrolling = 0;
        this.lastScrolling = 0;
        this.portalButtons = new ArrayList<>();
        this.currentlyShowing = new ArrayList<>();
        this.controller = controller;
    }

    @Override
    public void init() {
        super.init();
        this.x = this.width / 2 - guiWidth / 2;
        this.y = this.height / 2 - guiWidth / 2;
        if (informationList != null && !informationList.isEmpty()) addPortalButtons();
        textField = new EditBox(Minecraft.getInstance().font, this.x + guiWidth - 131, this.y + 3, 100, 10, Component.literal(""));
        textField.setFocused(true);
        textField.setVisible(true);
        textField.setBordered(true);
        //this.setFocused(textField);
        getAddons().add(new PortalCallButton(x + 9, y + guiHeight + 2, controller, PortalCallButton.CallAction.OPEN, this));
        getAddons().add(new PortalCallButton(x + 53 + 9, y + guiHeight + 2, controller, PortalCallButton.CallAction.ONCE, this));
        getAddons().add(new PortalCallButton(x + 53 * 2 + 9, y + guiHeight + 2, controller, PortalCallButton.CallAction.FORCE, this));
    }

    private void addPortalButtons() {
        if (this.informationList == null) return;
        List<PortalInformation> tempInformations = new ArrayList<>(this.informationList);
        tempInformations.removeIf(information -> information.isPrivate() && !information.getOwner().equals(Minecraft.getInstance().player.getUUID()));
        tempInformations.sort((o1, o2) -> Boolean.compare(o2.isPrivate(), o1.isPrivate()));
        if (!textField.getValue().isEmpty())
            tempInformations.removeIf(portalInformation -> !portalInformation.getName().toLowerCase().contains(textField.getValue().toLowerCase()));
        this.portalButtons.forEach(this::removeWidget);
        this.portalButtons.clear();
        this.visiblePortalInformations = tempInformations.size();
        int pointer = (int) (((tempInformations.size() - 7) * scrolling));
        currentlyShowing = tempInformations;
        for (int i = pointer; i < pointer + 7; i++) {
            if (tempInformations.size() > i && i >= 0) {
                int finalI = i;
                GuiButtonImagePortal buttonImage = new GuiButtonImagePortal(this, tempInformations.get(finalI), this.x + 9, this.y + 19 + 23 * (finalI - pointer), 157, 22, 0, 234, 0, new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png")) {
                    @Override
                    public void onPress() {
                        selectedPortal = currentlyShowing.get(finalI + (int) (((currentlyShowing.size() - 7) * scrolling)));
                    }
                };
                this.addRenderableWidget(buttonImage);
                this.portalButtons.add(buttonImage);
            }
        }
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        boolean pressed = super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        Minecraft.getInstance().tell(() -> {
            this.scrolling = 0;
            this.lastScrolling = 0;
            addPortalButtons();
        });
        return pressed;
    }

    public void refresh(List<PortalInformation> informationList) {
        this.informationList = informationList;
        addPortalButtons();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics stack, int mouseX, int mouseY, float partialTicks) {
        checkForScrolling(mouseX, mouseY);
        this.renderBackground(stack);
        ResourceLocation background = new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png");
        RenderSystem.setShaderTexture(0, background);
        stack.blit(background, x, y, 0, 0, guiWidth, guiHeight);
        stack.blit(background,this.x + guiWidth - 22, (int) (this.y + 10 + 140 * scrolling), 200, 9, 18, 23);
        super.renderBackground(stack, mouseX, mouseY, partialTicks);
        textField.renderWidget(stack, mouseX, mouseY, partialTicks);
    }


    @Override
    public List<IFactory<IScreenAddon>> guiAddons() {
        return Collections.emptyList();
    }

    public PortalInformation getSelectedPortal() {
        return selectedPortal;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void checkForScrolling(int mouseX, int mouseY) {
        if (GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
            if (!isDragging && mouseX > this.x + guiWidth - 22 && mouseX < this.x + guiWidth - 22 + 18 && mouseY > this.y + 10 && mouseY < this.y + 10 + 151) {
                isDragging = true;
            }
            if (isDragging) {
                mouseY -= (7 + 18 + this.y);
                lastScrolling = scrolling;
                scrolling = Mth.clamp(mouseY / 128D, 0, 1);
                addPortalButtons();
            }
        } else {
            isDragging = false;
        }
    }

    @Override
    public boolean mouseScrolled(double x, double y, double z) {
        scrolling = Mth.clamp(scrolling -= z / (currentlyShowing.size() - 7D), 0, 1);
        addPortalButtons();
        return true;
    }
}
