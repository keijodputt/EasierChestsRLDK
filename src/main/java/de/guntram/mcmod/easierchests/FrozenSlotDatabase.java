package de.guntram.mcmod.easierchests;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class FrozenSlotDatabase {

    private static final Set<Integer> FROZEN_SLOTS = new HashSet<>();
    private static File storageFile;

    public static void init(File file) {
        storageFile = file;
        load();
    }

    public static boolean isSlotFrozen(int slotIndex) {
        return FROZEN_SLOTS.contains(slotIndex);
    }

    public static void toggleSlot(int slotIndex) {
        if (FROZEN_SLOTS.contains(slotIndex)) {
            FROZEN_SLOTS.remove(slotIndex);
        } else {
            FROZEN_SLOTS.add(slotIndex);
        }
        save();
    }

    public static void freezeSlot(int slotIndex) {
        if (FROZEN_SLOTS.add(slotIndex)) {
            save();
        }
    }

    public static void unfreezeSlot(int slotIndex) {
        if (FROZEN_SLOTS.remove(slotIndex)) {
            save();
        }
    }

    public static void clearAll() {
        FROZEN_SLOTS.clear();
        save();
    }

    private static void load() {
        FROZEN_SLOTS.clear();
        if (storageFile == null || !storageFile.exists()) {
            return;
        }

        try (DataInputStream in = new DataInputStream(new FileInputStream(storageFile))) {
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                FROZEN_SLOTS.add(in.readInt());
            }
        } catch (IOException e) {
            System.err.println("[EasierChests] Failed to load frozen slots: " + e.getMessage());
        }
    }

    private static void save() {
        if (storageFile == null) {
            return;
        }

        try {
            File parent = storageFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(storageFile))) {
                out.writeInt(FROZEN_SLOTS.size());
                for (int slotIndex : FROZEN_SLOTS) {
                    out.writeInt(slotIndex);
                }
            }
        } catch (IOException e) {
            System.err.println("[EasierChests] Failed to save frozen slots: " + e.getMessage());
        }
    }
}