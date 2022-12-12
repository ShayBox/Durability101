package com.shaybox.durability101;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;

@Mod("durability101")
public class Main {

    public static void renderDurability101(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
        if (!stack.isEmpty() && stack.isDamaged()) {
            // ItemStack information
            int damage = stack.getDamageValue();
            int maxDamage = stack.getMaxDamage();
            int unbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);

            // Create string, position, and color
            String string = format(((maxDamage - damage) * (unbreaking + 1)));
            int stringWidth = fr.width(string);
            int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
            int y = (yPosition * 2) + 18;
            int color = stack.getItem().getRGBDurabilityForDisplay(stack);

            // Draw string
            MatrixStack matrixstack = new MatrixStack();
            matrixstack.scale(0.5F, 0.5F, 0.5F);
            matrixstack.translate(0.0D, 0.0D, 750.0F);
            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
            fr.drawInBatch(string, x, y, color, true, matrixstack.last().pose(), irendertypebuffer$impl, false, 0, 15728880);
            irendertypebuffer$impl.endBatch();
        }
    }

    public static String format(float number) {
        DecimalFormat decimalFormat = new DecimalFormat("0.#");

        if (number >= 1000000000) return decimalFormat.format(number / 1000000000) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }

}