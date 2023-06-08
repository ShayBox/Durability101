package com.shaybox.durability101.mixin;

import com.shaybox.durability101.DurabilityRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class HotbarItemRendererMixin {
	@Shadow public abstract MatrixStack getMatrices();

	@Inject(at = @At("RETURN"), method = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;II)V")
	    public void renderSlotItemOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, CallbackInfo ci){
			DurabilityRenderer.renderDurability101(this.getMatrices(),textRenderer,stack,x,y);
	}
}
