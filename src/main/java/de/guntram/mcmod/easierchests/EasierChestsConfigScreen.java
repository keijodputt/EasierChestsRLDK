package de.guntram.mcmod.easierchests;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class EasierChestsConfigScreen extends Screen {

    private final Screen parent;

    public EasierChestsConfigScreen(Screen parent) {
        super(Text.literal("EasierChests Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int buttonWidth = 150;
        int buttonHeight = 20;
        int leftCol = this.width / 2 - buttonWidth - 5;
        int rightCol = this.width / 2 + 5;
        int startY = 40;
        int spacing = 24;

        // Row and column toggle buttons
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ConfigurationHandler.enableRowButtons())
                .build(leftCol, startY, buttonWidth, buttonHeight, Text.literal("Row Buttons"), (button, value) -> {
                    ConfigurationHandler.setEnableRowButtons(value);
                }));

        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ConfigurationHandler.enableColumnButtons())
                .build(rightCol, startY, buttonWidth, buttonHeight, Text.literal("Column Buttons"), (button, value) -> {
                    ConfigurationHandler.setEnableColumnButtons(value);
                }));

        // Search box and compact button sizing
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ConfigurationHandler.enableSearch())
                .build(leftCol, startY + spacing, buttonWidth, buttonHeight, Text.literal("Search Box"), (button, value) -> {
                    ConfigurationHandler.setEnableSearch(value);
                }));

        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ConfigurationHandler.halfSizeButtons())
                .build(rightCol, startY + spacing, buttonWidth, buttonHeight, Text.literal("Half Size Buttons"), (button, value) -> {
                    ConfigurationHandler.setHalfSizeButtons(value);
                }));

        // Button transparency and large chest layouts
        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ConfigurationHandler.toneDownButtons())
                .build(leftCol, startY + spacing * 2, buttonWidth, buttonHeight, Text.literal("Transparent Buttons"), (button, value) -> {
                    ConfigurationHandler.setToneDownButtons(value);
                }));

        this.addDrawableChild(CyclingButtonWidget.onOffBuilder(ConfigurationHandler.allowExtraLargeChests())
                .build(rightCol, startY + spacing * 2, buttonWidth, buttonHeight, Text.literal("Extra Large Chests"), (button, value) -> {
                    ConfigurationHandler.setAllowExtraLargeChests(value);
                }));

        // Outgoing packet rate limiter throttle
        this.addDrawableChild(CyclingButtonWidget.<Integer>builder(ticks -> Text.literal(ticks + (ticks == 1 ? " tick" : " ticks")))
                .values(0, 1, 2, 3, 5)
                .initially(ConfigurationHandler.getClickDelayTicks())
                .build(this.width / 2 - 100, startY + spacing * 3, 200, buttonHeight, Text.literal("Packet Delay"), (button, value) -> {
                    ConfigurationHandler.setClickDelayTicks(value);
                }));

        // Bottom confirmation button
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close())
                .dimensions(this.width / 2 - 100, this.height - 30, 200, buttonHeight)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFF);
    }

    @Override
    public void close() {
        ConfigurationHandler.getInstance().save();
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }
}