package com.buuz135.thaumicjei.category;

import com.buuz135.thaumicjei.AlphaDrawable;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.InfusionRecipe;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfusionCategory extends BlankRecipeCategory<InfusionCategory.InfusionWrapper> {

    public static final String UUID = "THAUMCRAFT_INFUSION";

    @Override
    public String getUid() {
        return UUID;
    }

    @Override
    public String getTitle() {
        return "Infusion crafting";
    }

    @Override
    public IDrawable getBackground() {
        return new AlphaDrawable(new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook_overlay.png"), 413, 154, 86, 86, 10, 40, 0, 0);
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        minecraft.renderEngine.bindTexture(new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook_overlay.png"));
        GL11.glEnable(3042);
        Gui.drawModalRectWithCustomSizedTexture(27, -35, 40, 6, 32, 32, 512, 512);
        GL11.glDisable(3042);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, InfusionWrapper recipeWrapper, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, false, 34, 7 - 35);
        recipeLayout.getItemStacks().set(0, ingredients.getOutputs(ItemStack.class).get(0));
        int slot = 1;
        float currentRotation = -90.0F;
        for (List<ItemStack> stacks : ingredients.getInputs(ItemStack.class)) {
            if (slot == 1) recipeLayout.getItemStacks().init(slot, true, 34, 45);
            else
                recipeLayout.getItemStacks().init(slot, true, (int) (MathHelper.cos(currentRotation / 180.0F * 3.1415927F) * 40.0F) + 34, (int) (MathHelper.sin(currentRotation / 180.0F * 3.1415927F) * 40.0F) + 45);
            recipeLayout.getItemStacks().set(slot, stacks);
            currentRotation += (360f / recipeWrapper.recipe.components.length);
            ++slot;
        }
    }

    public static class InfusionWrapper extends BlankRecipeWrapper implements IHasResearch {

        private static final int ASPECT_Y = 115;
        private static final int ASPECT_X = 46;
        private final InfusionRecipe recipe;

        public InfusionWrapper(InfusionRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void getIngredients(IIngredients ingredients) {
            List<List<ItemStack>> inputs = new ArrayList<>();
            if (recipe.recipeInput instanceof ItemStack) {
                inputs.add(Arrays.asList((ItemStack) recipe.recipeInput));
            } else if (recipe.recipeInput != null) {
                inputs.add((List<ItemStack>) recipe.recipeInput);
            }
            if (recipe.recipeOutput instanceof ItemStack) {
                ingredients.setOutput(ItemStack.class, (ItemStack) recipe.recipeOutput);
            } else if (recipe.recipeInput != null && recipe.recipeOutput != null) {
                for (ItemStack stack : inputs.get(0)) {
                    if (stack != null) {
                        NBTTagCompound compound = stack.getTagCompound();
                        if (compound == null) compound = new NBTTagCompound();
                        compound.setTag((String) ((Object[]) recipe.recipeOutput)[0], (NBTBase) ((Object[]) recipe.recipeOutput)[1]);
                        stack.setTagCompound(compound);
                    }
                }
                ingredients.setOutputs(ItemStack.class, inputs.get(0));
            }
            for (Object comp : recipe.components) {
                if (comp instanceof ItemStack) {
                    inputs.add(Arrays.asList((ItemStack) comp));
                } else if (comp != null) {
                    inputs.add(OreDictionary.getOres((String) comp, false));
                }
            }
            ingredients.setInputLists(ItemStack.class, inputs);
        }

        @Override
        public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
            int space = 22;
            int center = (recipe.aspects.size() * space) / 2;
            int x = 0;
            for (Aspect aspect : recipe.aspects.getAspectsSortedByAmount()) {
                minecraft.renderEngine.bindTexture(aspect.getImage());
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                Color c = new Color(aspect.getColor());
                GL11.glColor4f((float) c.getRed() / 255.0F, (float) c.getGreen() / 255.0F, (float) c.getBlue() / 255.0F, 1.0F);
                Gui.drawModalRectWithCustomSizedTexture(ASPECT_X - center + x * space, ASPECT_Y, 0, 0, 16, 16, 16, 16);
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glScaled(0.5, 0.5, 0.5);
                minecraft.currentScreen.drawString(minecraft.fontRendererObj, TextFormatting.WHITE + "" + recipe.aspects.getAmount(aspect), 28 + (ASPECT_X - center + x * space) * 2, ASPECT_Y * 2 + 26, 0);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
                ++x;
            }

            int instability = Math.min(5, recipe.instability / 2);
            String inst = new TextComponentTranslation("tc.inst").getFormattedText() + new TextComponentTranslation("tc.inst." + instability).getUnformattedText();
            minecraft.fontRendererObj.drawString(TextFormatting.DARK_GRAY + inst, -minecraft.fontRendererObj.getStringWidth(String.valueOf(instability)) / 2, 145, 0);

        }

        @Nullable
        @Override
        public List<String> getTooltipStrings(int mouseX, int mouseY) {
            if (mouseY > ASPECT_Y && mouseY < ASPECT_Y + 16) {
                int space = 22;
                int center = (recipe.aspects.size() * space) / 2;
                int x = 0;
                for (Aspect aspect : recipe.aspects.getAspectsSortedByAmount()) {
                    if (mouseX > ASPECT_X - center + x * space && mouseX < ASPECT_X - center + x * space + 16) {
                        return Arrays.asList(TextFormatting.AQUA + aspect.getName(), TextFormatting.GRAY + aspect.getLocalizedDescription());
                    }
                    ++x;
                }
            }
            return Arrays.asList();
        }

        @Override
        public String[] getResearch() {
            return new String[]{recipe.research};
        }
    }

    public static class InfusionHandler implements IRecipeHandler<InfusionWrapper> {

        @Override
        public Class<InfusionWrapper> getRecipeClass() {
            return InfusionWrapper.class;
        }

        @Override
        public String getRecipeCategoryUid() {
            return InfusionCategory.UUID;
        }

        @Override
        public String getRecipeCategoryUid(InfusionWrapper recipe) {
            return InfusionCategory.UUID;
        }

        @Override
        public IRecipeWrapper getRecipeWrapper(InfusionWrapper recipe) {
            return recipe;
        }

        @Override
        public boolean isRecipeValid(InfusionWrapper recipe) {
            return true;
        }
    }
}