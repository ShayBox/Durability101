package com.shaybox.durability101.mixin;

import com.shaybox.durability101.DurabilityRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class ItemRendererMixin {
	@Inject(at = @At("RETURN"), method = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlot(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/screen/slot/Slot;)V")
	public void drawSlot(DrawContext context, Slot slot, CallbackInfo ci){
		DurabilityRenderer.renderDurability101(context.getMatrices(),MinecraftClient.getInstance().textRenderer, slot.getStack(),slot.x,slot.y);
	}


}
