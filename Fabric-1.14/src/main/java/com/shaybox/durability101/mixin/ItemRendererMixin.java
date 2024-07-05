package com.shaybox.durability101.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(at = @At("RETURN"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	public void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack itemStack, int x, int y, String amountText, CallbackInfo ci) {
		renderDurability101(textRenderer, itemStack, x, y);
	}

	@Unique
	public void renderDurability101(TextRenderer textRenderer, ItemStack itemStack, int xPosition, int yPosition) {
		if (!itemStack.isEmpty() && itemStack.isDamaged()) {
			GlStateManager.disableLighting();
			GlStateManager.disableDepthTest();
			GlStateManager.disableBlend();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);

			// ItemStack information
			int unbreaking = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack);
			float maxDamage = itemStack.getMaxDamage();
			float damage = itemStack.getDamage();

			// Create string, position, and color
			String string = format(((maxDamage - damage) * (unbreaking + 1)));
			int stringWidth = textRenderer.getStringWidth(string);
			int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
			int y = (yPosition * 2) + 18;
			float hue = Math.max(0.0F, (maxDamage - damage) / maxDamage);
			int color = MathHelper.hsvToRgb(hue / 3.0F, 1.0F, 1.0F);

			// Draw string
			textRenderer.drawWithShadow(string, x, y, color);

			GlStateManager.scalef(2.0F, 2.0F, 2.0F);
			GlStateManager.enableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableDepthTest();
		}
	}

	@Unique
	public String format(float number) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");

		if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
		if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
		if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

		return Float.toString(number).replaceAll("\\.?0*$", "");
	}

}
