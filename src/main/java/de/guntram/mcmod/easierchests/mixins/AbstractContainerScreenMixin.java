package de.guntram.mcmod.easierchests.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import de.guntram.mcmod.easierchests.ConfigurationHandler;
import de.guntram.mcmod.easierchests.EasierChests;
import de.guntram.mcmod.easierchests.ExtendedGuiChest;
import de.guntram.mcmod.easierchests.FrozenSlotDatabase;
import de.guntram.mcmod.easierchests.interfaces.SlotClicker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen implements SlotClicker {

    @Shadow @Final protected ScreenHandler handler;
    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;
    @Shadow protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    private ExtendedGuiChest easierChests;
    private TextFieldWidget searchField;
    private String searchFilter = "";

    protected AbstractContainerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        if (this.handler instanceof GenericContainerScreenHandler || this.handler instanceof ShulkerBoxScreenHandler) {
            this.easierChests = new ExtendedGuiChest(this, this.handler, this.textRenderer);
            this.easierChests.init(this.x, this.y, this.backgroundWidth, this.backgroundHeight);

            if (ConfigurationHandler.enableSearch()) {
                int searchX = this.x + this.backgroundWidth - 80;
                int searchY = this.y - 14;
                this.searchField = new TextFieldWidget(this.textRenderer, searchX, searchY, 78, 12, Text.literal(""));
                this.searchField.setMaxLength(30);
                this.searchField.setText(this.searchFilter);
                this.searchField.setChangedListener(text -> this.searchFilter = text.toLowerCase().trim());
                this.addSelectableChild(this.searchField);
            }
        } else {
            this.easierChests = null;
            this.searchField = null;
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.easierChests != null) {
            this.easierChests.render(context, mouseX, mouseY, delta);

            if (this.searchField != null && ConfigurationHandler.enableSearch()) {
                this.searchField.render(context, mouseX, mouseY, delta);
                highlightMatchedSlots(context);
            }
        }
    }

    private void highlightMatchedSlots(DrawContext context) {
        if (this.searchFilter == null || this.searchFilter.isEmpty()) {
            return;
        }

        for (Slot slot : this.handler.slots) {
            if (slot.hasStack()) {
                ItemStack stack = slot.getStack();
                String itemName = I18n.translate(stack.getTranslationKey()).toLowerCase();
                if (itemName.contains(this.searchFilter)) {
                    int slotX = this.x + slot.x;
                    int slotY = this.y + slot.y;
                    context.fill(slotX, slotY, slotX + 16, slotY + 16, 0x5500FF00);
                }
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (this.searchField != null && this.searchField.mouseClicked(mouseX, mouseY, button)) {
            cir.setReturnValue(true);
            return;
        }

        if (this.easierChests != null && this.easierChests.mouseClicked(mouseX, mouseY, button, this)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.searchField != null && this.searchField.isActive() && this.searchField.keyPressed(keyCode, scanCode, modifiers)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void onCharTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.searchField != null && this.searchField.isActive() && this.searchField.charTyped(chr, modifiers)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public void clickSlot(int slotId, int mouseButton, SlotActionType actionType) {
        this.onMouseClick(null, slotId, mouseButton, actionType);
    }
}