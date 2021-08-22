package com.shaybox.durability101;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

public class CustomItemRenderer {
	public static void renderDurability(TextRenderer tr, ItemStack stack, int xPosition, int yPosition) {
		if (!stack.isEmpty()) {
			if (stack.isDamaged()) {
				// Unbreaking
				int unbreaking = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, stack);

				// Damage
				float damage = (float) stack.getDamage();
				float maxDamage = (float) stack.getMaxDamage();

				// String
				String string = format(((maxDamage - damage) * (unbreaking + 1)));
				int stringWidth = tr.getWidth(string);

				// Position
				int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
				int y = (yPosition * 2) + 18;

				// Color
				float hue = Math.max(0.0F, (maxDamage - damage) / maxDamage);
				int color = MathHelper.hsvToRgb(hue / 3.0F, 1.0F, 1.0F);

				// Draw
				MatrixStack matrices = new MatrixStack();
				matrices.translate(0.0D, 0.0D, 500.0F);
				matrices.scale(0.5F, 0.5F, 0.5F);
				tr.draw(matrices, string, x, y, color);
			}
		}
	}

	private static String format(float number) {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");

		if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
		if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
		if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

		return Float.toString(number).replaceAll("\\.?0*$", "");
	}
}