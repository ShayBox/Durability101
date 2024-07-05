package com.shaybox.durability101.mixin.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(at = @At("RETURN"), method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
	public void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack itemStack, int x, int y, @Nullable String countLabel, CallbackInfo ci) {
		renderDurability101(textRenderer, itemStack, x, y);
	}

	@Unique
	public void renderDurability101(TextRenderer textRenderer, ItemStack itemStack, int xPosition, int yPosition) {
		if (!itemStack.isEmpty() && itemStack.isDamaged()) {
			// ItemStack information
			int unbreaking = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, itemStack);
			int maxDamage = itemStack.getMaxDamage();
			int damage = itemStack.getDamage();

			// Create string, position, and color
			String string = format(((maxDamage - damage) * (unbreaking + 1)));
			int stringWidth = textRenderer.getWidth(string);
			int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
			int y = (yPosition * 2) + 18;
			int color = itemStack.getItem().getItemBarColor(itemStack);

			MatrixStack matrixStack = new MatrixStack();
			matrixStack.push();
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.translate(0.0F, 0.0F, 701.0F);

			// Draw string
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			textRenderer.draw(string, x, y, color, true, matrixStack.peek().getPositionMatrix(), immediate, false, 0, 15728880, false);
			immediate.draw();

			matrixStack.pop();
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
