package com.shaybox.durability101;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.text.DecimalFormat;

public class CustomItemRenderer {
    public static void renderDurability(Font font, ItemStack stack, int xPosition, int yPosition) {
        System.out.println(stack.getItem());
        if (!stack.isEmpty()) {
            if (stack.getItem().equals(Items.DIAMOND_SHOVEL)) System.out.println("Test1");
            if (stack.isDamaged()) {
                if (stack.getItem().equals(Items.DIAMOND_SHOVEL)) System.out.println("Test2");
                // Unbreaking
                int unbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack);

                // Damage
                float damage = (float) stack.getDamageValue();
                float maxDamage = (float) stack.getMaxDamage();

                // String
                String string = format(((maxDamage - damage) * (unbreaking + 1)));
                int stringWidth = font.width(string);

                // Position
                int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                int y = (yPosition * 2) + 18;

                // Color
                int color = stack.getItem().getRGBDurabilityForDisplay(stack);

                // Draw
                PoseStack posestack = new PoseStack();
                posestack.translate(0.0D, 0.0D, 500.0F);
                posestack.scale(0.5F, 0.5F, 0.5F);
                MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                font.drawInBatch(string, x, y, color, true, posestack.last().pose(), multibuffersource$buffersource, false, 0, 15728880);
                multibuffersource$buffersource.endBatch();

                if (stack.getItem().equals(Items.DIAMOND_SHOVEL)) System.out.println("Test3");
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