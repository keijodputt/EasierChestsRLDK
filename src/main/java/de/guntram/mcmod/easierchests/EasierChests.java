package de.guntram.mcmod.easierchests;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class EasierChests implements ClientModInitializer {

    public static final String MODID = "easierchests";
    public static final String MODNAME = "EasierChests";

    public static KeyBinding keySortChest;
    public static KeyBinding keySortPlayer;
    public static KeyBinding keyToggleSearch;

    @Override
    public void onInitializeClient() {
        File configDir = FabricLoader.getInstance().getConfigDir().toFile();
        File configFile = new File(configDir, "easierchests.json");
        ConfigurationHandler.getInstance().load(configFile);

        FrozenSlotDatabase.init(new File(configDir, "easierchests-frozen.dat"));

        keySortChest = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.easierchests.sortchest",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.easierchests"
        ));

        keySortPlayer = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.easierchests.sortplayer",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.easierchests"
        ));

        keyToggleSearch = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.easierchests.togglesearch",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.easierchests"
        ));
    }
}