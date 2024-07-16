package cn.mlus.portality.gui;

import cn.mlus.portality.Portality;
import cn.mlus.portality.tile.ControllerTile;
import cn.mlus.portality.gui.button.PortalSaveButton;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.ScreenAddonScreen;
import com.hrznstudio.titanium.client.screen.addon.color.ColorPickerAddon;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ChangeColorScreen extends ScreenAddonScreen {

    private ControllerTile controllerTile;
    private EditBox textField;
    private int color;
    private ColorPickerAddon colorPickerAddon;
    private boolean textChangedManually;

    public ChangeColorScreen(ControllerTile tile) {
        super(PortalityAssetProvider.PROVIDER, false);
        this.controllerTile = tile;
        this.color = tile.getColor();
        this.textChangedManually = false;
    }

    @Override
    public void init() {
        super.init();
        textField = new EditBox(Minecraft.getInstance().font, this.x + 14, this.y + 120, 80, 16, Component.literal(""));
        //textField.setFocused2(true);
        textField.setVisible(true);
        textField.setBordered(true);
        textField.setMaxLength(6);
        textField.setResponder(s -> {
            if (textChangedManually) {
                textChangedManually = false;
                return;
            }
            if (!s.isEmpty()) {
                try {
                    int tempColor = 0xff000000 | Integer.parseInt(s, 16);
                    if (tempColor != color) {
                        color = tempColor;
                        colorPickerAddon.setColor(color);
                    }
                } catch (NumberFormatException e) {
                }
            }
        });
        updateColor(color);
        addWidget(textField);
        this.getAddons().add(new PortalSaveButton(this.x + 110, this.y + 116, controllerTile, "Save", this));
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics);
        ResourceLocation color_change =  new ResourceLocation(Portality.MOD_ID, "textures/gui/color_change.png");
        RenderSystem.setShaderTexture(0,color_change);
        guiGraphics.blit(color_change,x, y, 0, 0, 175, 146);
        guiGraphics.fill(x + 13, y + 9, x + 15 + 100, y + 91, -16739073);
        guiGraphics.fill( x + 123, y + 9, x + 121 + 40, y + 91, -16739073);
        guiGraphics.fill(x + 13, y + 99, x + 13 + 148, y + 109, -16739073);
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
        textField.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        AssetUtil.drawHorizontalLine(guiGraphics, textField.getX() - 1, textField.getX() + textField.getWidth(), textField.getY() - 1, -16739073);
        AssetUtil.drawHorizontalLine(guiGraphics, textField.getX() - 1, textField.getX() + textField.getWidth(), textField.getY() + textField.getHeight(), -16739073);
        AssetUtil.drawVerticalLine(guiGraphics, textField.getX() - 1, textField.getY() - 1, textField.getY() + textField.getHeight(), -16739073);
        AssetUtil.drawVerticalLine(guiGraphics, textField.getX() + textField.getWidth(), textField.getY() - 1, textField.getY() + textField.getHeight(), -16739073);
    }

    @Override
    public void tick() {
        super.tick();
        textField.tick();
    }

    @Override
    public List<IFactory<IScreenAddon>> guiAddons() {
        List<IFactory<IScreenAddon>> addons = new ArrayList<>();
        addons.add(() -> this.colorPickerAddon = new ColorPickerAddon(14, 10, this.color, this::updateColor));
        return addons;
    }

    public void updateColor(int color) {
        this.color = color;
        if (textField != null) {
            this.textChangedManually = true;
            textField.setValue(Integer.toHexString(color).substring(2));
        }
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return textField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
