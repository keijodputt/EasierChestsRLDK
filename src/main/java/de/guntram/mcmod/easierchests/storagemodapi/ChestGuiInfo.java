package de.guntram.mcmod.easierchests.storagemodapi;

import net.minecraft.screen.ScreenHandler;

public interface ChestGuiInfo {

    int getRows(ScreenHandler handler);

    int getColumns(ScreenHandler handler);
}