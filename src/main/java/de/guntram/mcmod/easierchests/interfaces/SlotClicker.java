package de.guntram.mcmod.easierchests.interfaces;

import net.minecraft.screen.slot.SlotActionType;

public interface SlotClicker {

    void clickSlot(int slotId, int mouseButton, SlotActionType actionType);
}