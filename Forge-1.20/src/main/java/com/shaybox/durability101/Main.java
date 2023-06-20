package com.shaybox.durability101;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.text.DecimalFormat;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "durability101";

    @Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {

        @SubscribeEvent
        public static void onRegisterItemDecorations(final RegisterItemDecorationsEvent event) {
            Durability101 durability101 = new Durability101();
            ForgeRegistries.ITEMS.getValues().stream().filter(Item::canBeDepleted).forEach(item -> event.register(item, durability101));
        }

    }

    private static class Durability101 implements IItemDecorator {

        public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xPosition, int yPosition) {
            if (!stack.isEmpty() && stack.isDamaged()) {
                PoseStack poseStack = guiGraphics.pose();

                // ItemStack information
                int damage = stack.getDamageValue();
                int maxDamage = stack.getMaxDamage();
                int unbreaking = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.UNBREAKING, stack);

                // Create string, position, and color
                String string = format(((maxDamage - damage) * (unbreaking + 1)));
                int stringWidth = font.width(string);
                int x = ((xPosition + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                int y = (yPosition * 2) + 18;
                int color = stack.getItem().getBarColor(stack);

                // Draw string
                poseStack.pushPose();
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.translate(0.0D, 0.0D, 750.0F);
                MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                font.drawInBatch(string, x, y, color, true, poseStack.last().pose(), multibuffersource$buffersource, Font.DisplayMode.NORMAL, 0, 15728880, false);
                multibuffersource$buffersource.endBatch();
                poseStack.popPose();
            }

            return true;
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
