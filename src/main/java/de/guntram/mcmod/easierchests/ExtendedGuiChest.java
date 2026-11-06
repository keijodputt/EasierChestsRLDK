package de.guntram.mcmod.easierchests;

import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;

public class ExtendedGuiChest {

    public static final Identifier ICONS = Identifier.of("easierchests", "textures/icons.png");

    private static class QueuedClick {
        final SlotClicker clicker;
        final int slotId;
        final int mouseButton;
        final SlotActionType actionType;

        QueuedClick(SlotClicker clicker, int slotId, int mouseButton, SlotActionType actionType) {
            this.clicker = clicker;
            this.slotId = slotId;
            this.mouseButton = mouseButton;
            this.actionType = actionType;
        }
    }

    private static final Queue<QueuedClick> CLICK_QUEUE = new ArrayDeque<>();
    private static int clickDelayCounter = 0;

    private final Screen screen;
    private final ScreenHandler handler;
    private final TextRenderer textRenderer;

    private int chestSlots;
    private int chestRows;
    private int containerX;
    private int containerY;
    private int containerWidth;
    private int containerHeight;

    public ExtendedGuiChest(Screen screen, ScreenHandler handler, TextRenderer textRenderer) {
        this.screen = screen;
        this.handler = handler;
        this.textRenderer = textRenderer;

        if (handler instanceof GenericContainerScreenHandler genericHandler) {
            this.chestRows = genericHandler.getRows();
            this.chestSlots = this.chestRows * 9;
        } else if (handler instanceof ShulkerBoxScreenHandler) {
            this.chestRows = 3;
            this.chestSlots = 27;
        } else {
            this.chestRows = 0;
            this.chestSlots = 0;
        }
    }

    public void init(int x, int y, int width, int height) {
        this.containerX = x;
        this.containerY = y;
        this.containerWidth = width;
        this.containerHeight = height;
        clearClickQueue();
    }

    public static void enqueueClick(SlotClicker clicker, int slotId, int mouseButton, SlotActionType actionType) {
        if (ConfigurationHandler.getClickDelayTicks() <= 0) {
            clicker.clickSlot(slotId, mouseButton, actionType);
        } else {
            CLICK_QUEUE.add(new QueuedClick(clicker, slotId, mouseButton, actionType));
        }
    }

    public static void processClickQueue() {
        if (CLICK_QUEUE.isEmpty()) {
            return;
        }

        int delay = ConfigurationHandler.getClickDelayTicks();
        if (delay <= 0) {
            while (!CLICK_QUEUE.isEmpty()) {
                QueuedClick click = CLICK_QUEUE.poll();
                if (click != null) {
                    click.clicker.clickSlot(click.slotId, click.mouseButton, click.actionType);
                }
            }
            return;
        }

        if (clickDelayCounter > 0) {
            clickDelayCounter--;
            return;
        }

        QueuedClick click = CLICK_QUEUE.poll();
        if (click != null) {
            click.clicker.clickSlot(click.slotId, click.mouseButton, click.actionType);
            clickDelayCounter = delay;
        }
    }

    public static void clearClickQueue() {
        CLICK_QUEUE.clear();
        clickDelayCounter = 0;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        processClickQueue();

        if (this.chestRows <= 0) {
            return;
        }

        int btnSize = ConfigurationHandler.halfSizeButtons() ? 9 : 18;
        int iconUSize = 18;
        int iconVSize = 18;

        // Draw top action buttons: Sort Chest, Move Matching, Dump to Chest
        drawIconButton(context, containerX - btnSize - 2, containerY + 17, 0, 0, btnSize, iconUSize, iconVSize);
        drawIconButton(context, containerX - btnSize - 2, containerY + 35, 18, 0, btnSize, iconUSize, iconVSize);
        drawIconButton(context, containerX - btnSize - 2, containerY + 53, 36, 0, btnSize, iconUSize, iconVSize);

        // Draw bottom action buttons: Sort Player, Move Matching to Player, Dump to Player
        int playerStartY = containerY + containerHeight - 96;
        drawIconButton(context, containerX - btnSize - 2, playerStartY + 17, 0, 18, btnSize, iconUSize, iconVSize);
        drawIconButton(context, containerX - btnSize - 2, playerStartY + 35, 18, 18, btnSize, iconUSize, iconVSize);
        drawIconButton(context, containerX - btnSize - 2, playerStartY + 53, 36, 18, btnSize, iconUSize, iconVSize);

        // Draw Row Transfer Buttons
        if (ConfigurationHandler.enableRowButtons()) {
            for (int row = 0; row < chestRows; row++) {
                int rowY = containerY + 18 + row * 18;
                drawIconButton(context, containerX + containerWidth + 2, rowY, 54, 0, btnSize, iconUSize, iconVSize);
            }
            for (int playerRow = 0; playerRow < 3; playerRow++) {
                int rowY = playerStartY + 18 + playerRow * 18;
                drawIconButton(context, containerX + containerWidth + 2, rowY, 54, 18, btnSize, iconUSize, iconVSize);
            }
        }
    }

    private void drawIconButton(DrawContext context, int x, int y, int u, int v, int size, int uSize, int vSize) {
        context.drawTexture(ICONS, x, y, u, v, size, size, 256, 256);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button, SlotClicker clicker) {
        if (this.chestRows <= 0 || button != 0) {
            return false;
        }

        int btnSize = ConfigurationHandler.halfSizeButtons() ? 9 : 18;
        int btnX = containerX - btnSize - 2;

        // Check Top Actions
        if (mouseX >= btnX && mouseX <= btnX + btnSize) {
            if (mouseY >= containerY + 17 && mouseY <= containerY + 17 + btnSize) {
                sortContainer(0, chestSlots, clicker);
                return true;
            }
            if (mouseY >= containerY + 35 && mouseY <= containerY + 35 + btnSize) {
                transferMatching(chestSlots, handler.slots.size(), 0, chestSlots, clicker);
                return true;
            }
            if (mouseY >= containerY + 53 && mouseY <= containerY + 53 + btnSize) {
                transferAll(chestSlots, handler.slots.size(), 0, chestSlots, clicker);
                return true;
            }

            int playerStartY = containerY + containerHeight - 96;
            if (mouseY >= playerStartY + 17 && mouseY <= playerStartY + 17 + btnSize) {
                sortContainer(chestSlots, handler.slots.size(), clicker);
                return true;
            }
            if (mouseY >= playerStartY + 35 && mouseY <= playerStartY + 35 + btnSize) {
                transferMatching(0, chestSlots, chestSlots, handler.slots.size(), clicker);
                return true;
            }
            if (mouseY >= playerStartY + 53 && mouseY <= playerStartY + 53 + btnSize) {
                transferAll(0, chestSlots, chestSlots, handler.slots.size(), clicker);
                return true;
            }
        }

        // Check Row Transfer Actions
        if (ConfigurationHandler.enableRowButtons()) {
            int rightBtnX = containerX + containerWidth + 2;
            if (mouseX >= rightBtnX && mouseX <= rightBtnX + btnSize) {
                for (int row = 0; row < chestRows; row++) {
                    int rowY = containerY + 18 + row * 18;
                    if (mouseY >= rowY && mouseY <= rowY + btnSize) {
                        transferRow(row * 9, chestSlots, handler.slots.size(), clicker);
                        return true;
                    }
                }
                int playerStartY = containerY + containerHeight - 96;
                for (int playerRow = 0; playerRow < 3; playerRow++) {
                    int rowY = playerStartY + 18 + playerRow * 18;
                    if (mouseY >= rowY && mouseY <= rowY + btnSize) {
                        transferRow(chestSlots + playerRow * 9, 0, chestSlots, clicker);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void transferAll(int sourceStart, int sourceEnd, int targetStart, int targetEnd, SlotClicker clicker) {
        for (int i = sourceStart; i < sourceEnd; i++) {
            Slot sourceSlot = handler.slots.get(i);
            if (sourceSlot.hasStack() && !FrozenSlotDatabase.isSlotFrozen(i)) {
                enqueueClick(clicker, i, 0, SlotActionType.QUICK_MOVE);
            }
        }
    }

    private void transferMatching(int sourceStart, int sourceEnd, int targetStart, int targetEnd, SlotClicker clicker) {
        List<Item> targetItems = new ArrayList<>();
        for (int i = targetStart; i < targetEnd; i++) {
            Slot slot = handler.slots.get(i);
            if (slot.hasStack()) {
                Item item = slot.getStack().getItem();
                if (!targetItems.contains(item)) {
                    targetItems.add(item);
                }
            }
        }

        for (int i = sourceStart; i < sourceEnd; i++) {
            Slot slot = handler.slots.get(i);
            if (slot.hasStack() && !FrozenSlotDatabase.isSlotFrozen(i)) {
                if (targetItems.contains(slot.getStack().getItem())) {
                    enqueueClick(clicker, i, 0, SlotActionType.QUICK_MOVE);
                }
            }
        }
    }

    private void transferRow(int rowStart, int targetStart, int targetEnd, SlotClicker clicker) {
        for (int i = 0; i < 9; i++) {
            int slotIndex = rowStart + i;
            if (slotIndex < handler.slots.size()) {
                Slot slot = handler.slots.get(slotIndex);
                if (slot.hasStack() && !FrozenSlotDatabase.isSlotFrozen(slotIndex)) {
                    enqueueClick(clicker, slotIndex, 0, SlotActionType.QUICK_MOVE);
                }
            }
        }
    }

    private void sortContainer(int startSlot, int endSlot, SlotClicker clicker) {
        List<Integer> slotsToSort = new ArrayList<>();
        for (int i = startSlot; i < endSlot; i++) {
            if (!FrozenSlotDatabase.isSlotFrozen(i)) {
                slotsToSort.add(i);
            }
        }

        List<ItemStack> stacks = new ArrayList<>();
        for (int index : slotsToSort) {
            stacks.add(handler.slots.get(index).getStack().copy());
        }

        stacks.sort(Comparator.comparing((ItemStack s) -> s.isEmpty() ? 1 : 0)
                .thenComparing(s -> s.isEmpty() ? "" : s.getItem().toString()));

        for (int i = 0; i < slotsToSort.size(); i++) {
            int sourceSlotId = slotsToSort.get(i);
            Slot slot = handler.slots.get(sourceSlotId);
            if (slot.hasStack()) {
                enqueueClick(clicker, sourceSlotId, 0, SlotActionType.PICKUP);
                enqueueClick(clicker, sourceSlotId, 0, SlotActionType.PICKUP);
            }
        }
    }
}