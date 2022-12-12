package com.shaybox.durability101.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(at = @At("RETURN"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	public void renderGuiItemOverlay(TextRenderer fontRenderer, ItemStack stack, int x, int y, String amountText, CallbackInfo ci) {
		renderDurability101(fontRenderer, stack, x, y);
	}

	public void renderDurability101(TextRenderer fontRenderer, ItemStack stack, int xPosition, int yPosition) {
		if (!stack.isEmpty() && stack.isDamaged()) {
			// ItemStack information
			float damage = stack.getDamage();
			float maxDamage = stack.getMaxDamage();
			int unbreaking = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);

			// Create string, position, and color
			String string = format(((maxDamage - damage) * (unbreaking + 1)));
			int stringWidth = fontRenderer.getStringWidth(string);
			int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
			int y = (yPosition * 2) + 18;
			float hue = Math.max(0.0F, (maxDamage - damage) / maxDamage);
			int color = MathHelper.hsvToRgb(hue / 3.0F, 1.0F, 1.0F);

			// Draw string
			MatrixStack matrixStack = new MatrixStack();
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.translate(0.0D, 0.0D, 750.0F);
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			fontRenderer.draw(string, x, y, color, true, matrixStack.peek().getModel(), immediate, false, 0, 15728880);
			immediate.draw();
		}
	}

	public String format(float number) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");

		if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
		if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
		if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

		return Float.toString(number).replaceAll("\\.?0*$", "");
	}

}
