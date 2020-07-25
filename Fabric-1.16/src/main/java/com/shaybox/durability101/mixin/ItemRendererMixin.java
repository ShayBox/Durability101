package com.shaybox.durability101.mixin;

import com.shaybox.durability101.CustomItemRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(at = @At("HEAD"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	private void init(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
		CustomItemRenderer.renderDurability(renderer, stack, x, y);
	}

}
