package de.guntram.mcmod.easierchests;

import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import de.guntram.mcmod.easierchests.storagemodapi.ChestGuiInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExtendedGuiChest {

    private final HandledScreen<?> screen;
    private final SlotClicker slotClicker;
    private static final Identifier ICONS = new Identifier("easierchests", "textures/icons.png");

    private int inventoryRows;
    private int containerSlots;
    private boolean isShulker;

    public ExtendedGuiChest(HandledScreen<?> screen, SlotClicker slotClicker) {
        this.screen = screen;
        this.slotClicker = slotClicker;
        initDimensions();
    }

    private void initDimensions() {
        ScreenHandler handler = screen.getScreenHandler();
        ChestGuiInfo helper = EasierChests.getHelperForHandler(handler);

        if (helper != null) {
            this.inventoryRows = helper.getRows(handler);
            this.containerSlots = this.inventoryRows * helper.getColumns(handler);
            this.isShulker = false;
        } else if (handler instanceof GenericContainerScreenHandler) {
            this.inventoryRows = ((GenericContainerScreenHandler) handler).getRows();
            this.containerSlots = this.inventoryRows * 9;
            this.isShulker = false;
        } else if (handler instanceof ShulkerBoxScreenHandler) {
            this.inventoryRows = 3;
            this.containerSlots = 27;
            this.isShulker = true;
        } else {
            this.inventoryRows = 0;
            this.containerSlots = 0;
            this.isShulker = false;
        }
    }

    public boolean isSupportedContainer() {
        return this.containerSlots > 0;
    }

    public int getContainerSlots() {
        return this.containerSlots;
    }

    public int getInventoryRows() {
        return this.inventoryRows;
    }

    public boolean isShulker() {
        return this.isShulker;
    }
}
