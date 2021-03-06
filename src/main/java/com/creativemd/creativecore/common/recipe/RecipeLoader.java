package com.creativemd.creativecore.common.recipe;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeLoader {
	
	public static int[] getRecipeDimensions(IRecipe recipe) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (recipe.canFit(i, j)) {
					return new int[] { i, j };
				}
			}
		}
		return new int[] { 3, 3 };
	}
	
	public static Object[] getInput(IRecipe recipe, boolean vanillaOnly) {
		if (recipe instanceof IRecipeInfo)
			return ((IRecipeInfo) recipe).getInput();
		if (recipe instanceof ShapedRecipes)
			return ((ShapedRecipes) recipe).recipeItems.toArray();
		if (recipe instanceof ShapedOreRecipe)
			return ((ShapedOreRecipe) recipe).getIngredients().toArray();
		if (recipe instanceof ShapelessRecipes)
			return ((ShapelessRecipes) recipe).recipeItems.toArray();
		if (recipe instanceof ShapelessOreRecipe)
			return ((ShapelessOreRecipe) recipe).getIngredients().toArray();
		
		if (!vanillaOnly) {
			try {
				Method m = recipe.getClass().getMethod("getInput");
				if (m != null)
					return (Object[]) m.invoke(recipe);
			} catch (Exception e) {
				
			}
			
			try {
				Object[] input = (Object[]) ReflectionHelper.getPrivateValue((Class<? super IRecipe>) recipe.getClass(), recipe, "input");
				Class classRecipeInput = Class.forName("ic2.api.recipe.IRecipeInput");
				if (classRecipeInput != null) {
					
					for (int i = 0; i < input.length; i++) {
						if (classRecipeInput.isAssignableFrom(input[i].getClass())) {
							List<ItemStack> stacks = (List<ItemStack>) ReflectionHelper.findMethod((Class<? super Object>) input[i].getClass(), "getInputs", "getInputs").invoke(input[i]);
							if (stacks.size() > 0)
								input[i] = stacks.get(0);
							else
								input[i] = null;
						}
					}
				}
				return input;
			} catch (Exception e) {
				
			}
		}
		
		return null;
	}
	
	public static Object[] getInput(IRecipe recipe) {
		return getInput(recipe, false);
	}
	
	public static int getWidth(IRecipe recipe) {
		try {
			Method m = recipe.getClass().getMethod("getWidth");
			if (m != null)
				return (Integer) m.invoke(recipe);
		} catch (Exception e) {
			
		}
		
		try {
			return (Integer) ReflectionHelper.getPrivateValue((Class<? super IRecipe>) recipe.getClass(), recipe, "width");
		} catch (Exception e) {
			
		}
		
		if (recipe instanceof IRecipeInfo)
			return ((IRecipeInfo) recipe).getWidth();
		if (recipe instanceof ShapedRecipes)
			return ((ShapedRecipes) recipe).recipeWidth;
		return 3;
	}
	
}
