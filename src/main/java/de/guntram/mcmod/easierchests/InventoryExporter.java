package de.guntram.mcmod.easierchests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class InventoryExporter {

    public static void exportInventory(ScreenHandler handler, File destinationFile) {
        if (handler == null || destinationFile == null) {
            return;
        }

        File parent = destinationFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(destinationFile))) {
            writer.println("Slot,Item,Count");
            for (int i = 0; i < handler.slots.size(); i++) {
                Slot slot = handler.slots.get(i);
                if (slot.hasStack()) {
                    ItemStack stack = slot.getStack();
                    writer.printf("%d,%s,%d%n", i, stack.getItem().toString(), stack.getCount());
                }
            }
        } catch (IOException e) {
            System.err.println("[EasierChests] Failed to export inventory: " + e.getMessage());
        }
    }
}