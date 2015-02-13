package codechicken.nei.recipe;

import codechicken.nei.PositionedStack;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A Template Recipe Handler!
 * How about that.
 * Because it was sooo hard, and more seriously required lots of copied code to make a handler in the past.
 * you can now extend this class to make your custom recipe handlers much easier to create.
 * Just look at the 5 handlers included by default to work out how to do stuff if you are still stuck.
 */
public abstract class TemplateRecipeHandler implements ICraftingHandler, IUsageHandler {
    /**
     * This Recipe Handler runs on this internal class
     * Fill the recipe array with subclasses of this to make transforming the different types of recipes out there into a nice format for NEI a much easier job.
     */
    public abstract class CachedRecipe {
        final long offset = System.currentTimeMillis();

        /**
         * @return The item produced by this recipe, with position
         */
        public abstract PositionedStack getResult();

        /**
         * The ingredients required to produce the result
         * Use this if you have more than one ingredient
         *
         * @return A list of positioned ingredient items.
         */
        public List<PositionedStack> getIngredients() {
            ArrayList<PositionedStack> stacks = new ArrayList<PositionedStack>();
            PositionedStack stack = getIngredient();
            if (stack != null)
                stacks.add(stack);
            return stacks;
        }

        /**
         * @return The ingredient required to produce the result
         */
        public PositionedStack getIngredient() {
            return null;
        }

        /**
         * This will perform default cycling of ingredients, mulitItem capable
         */
        public List<PositionedStack> getCycledIngredients(int cycle, List<PositionedStack> ingredients) {
            for (int itemIndex = 0; itemIndex < ingredients.size(); itemIndex++)
                randomRenderPermutation(ingredients.get(itemIndex), cycle + itemIndex);

            return ingredients;
        }

        public void randomRenderPermutation(PositionedStack stack, long cycle) {
            Random rand = new Random(cycle + offset);
            stack.setPermutationToRender(Math.abs(rand.nextInt()) % stack.items.length);
        }
    }

    /**
     * The Rectangle is an region of the gui relative to the corner of the recipe that will activate the recipe with the corresponding outputId apon being clicked.
     * Apply this over fuel icons or arrows that the user may click to see all recipes pertaining to that action.
     */
    public static class RecipeTransferRect
    {
        public RecipeTransferRect(Rectangle rectangle, String outputId, Object... results) {
            rect = rectangle;
            this.outputId = outputId;
            this.results = results;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof RecipeTransferRect))
                return false;

            return rect.equals(((RecipeTransferRect) obj).rect);
        }

        public int hashCode() {
            return rect.hashCode();
        }

        Rectangle rect;
        String outputId;
        Object[] results;
    }

    /**
     * Internal tick counter, initialised to random value and incremented every tick.
     * Used for cycling similar ingredients and progress bars.
     */
    public int cycleticks = Math.abs((int) System.currentTimeMillis());
    /**
     * The list of matching recipes
     */
    public ArrayList<CachedRecipe> arecipes = new ArrayList<CachedRecipe>();
    /**
     * A list of transferRects that apon when clicked or R is pressed will open a new recipe.
     */
    public LinkedList<RecipeTransferRect> transferRects = new LinkedList<RecipeTransferRect>();

    /**
     * Add all RecipeTransferRects to the transferRects list during this call.
     * Afterward they may be added to the input handler for the corresponding guis from getRecipeTransferRectGuis
     */
    public void loadTransferRects() {

    }

    /**
     * In this function you need to fill up the empty recipe array with recipes.
     * The default passes it to a cleaner handler if outputId is an item
     *
     * @param outputId A String identifier representing the type of output produced. Eg. {"item", "fuel"}
     * @param results  Objects representing the results that matching recipes must produce.
     */
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("item"))
            loadCraftingRecipes((ItemStack) results[0]);
    }

    /**
     * Simplified wrapper, implement this and fill the empty recipe array with recipes
     *
     * @param result The result the recipes must output.
     */
    public void loadCraftingRecipes(ItemStack result) {}

    /**
     * In this function you need to fill up the empty recipe array with recipes
     * The default passes it to a cleaner handler if inputId is an item
     *
     * @param inputId     A String identifier representing the type of ingredients used. Eg. {"item", "fuel"}
     * @param ingredients Objects representing the ingredients that matching recipes must contain.
     */
    public void loadUsageRecipes(String inputId, Object... ingredients) {
        if (inputId.equals("item"))
            loadUsageRecipes((ItemStack) ingredients[0]);
    }

    /**
     * Simplified wrapper, implement this and fill the empty recipe array with recipes
     *
     * @param ingredient The ingredient the recipes must contain.
     */
    public void loadUsageRecipes(ItemStack ingredient) {}

    /**
     * @return The filepath to the texture to use when drawing this recipe
     */
    public abstract String getGuiTexture();

    /**
     * Simply works with the {@link DefaultOverlayRenderer}
     * If the current container has been registered with this identifier, the question mark appears and an overlay guide can be drawn.
     *
     * @return The overlay identifier of this recipe type.
     */
    public String getOverlayIdentifier() {
        return null;
    }

    /**
     * @return The gui classes to which the transfer rects added in the constructor are to be located over. null if none.
     */
    public List<Class<? extends GuiContainer>> getRecipeTransferRectGuis() {
        Class<? extends GuiContainer> clazz = getGuiClass();
        if (clazz != null) {
            LinkedList<Class<? extends GuiContainer>> list = new LinkedList<Class<? extends GuiContainer>>();
            list.add(clazz);
            return list;
        }
        return null;
    }

    /**
     * @return The class of the GuiContainer that this recipe would be crafted in.
     */
    public Class<? extends GuiContainer> getGuiClass() {
        return null;
    }

    public TemplateRecipeHandler newInstance() {
        try {
            return getClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ICraftingHandler getRecipeHandler(String outputId, Object... results) {
        TemplateRecipeHandler handler = newInstance();
        handler.loadCraftingRecipes(outputId, results);
        return handler;
    }

    public IUsageHandler getUsageHandler(String inputId, Object... ingredients) {
        TemplateRecipeHandler handler = newInstance();
        handler.loadUsageRecipes(inputId, ingredients);
        return handler;
    }

    public boolean hasOverlay(GuiContainer gui, net.minecraft.inventory.Container container, int recipe) {
        return RecipeInfo.hasDefaultOverlay(gui, getOverlayIdentifier()) || RecipeInfo.hasOverlayHandler(gui, getOverlayIdentifier());
    }
}
