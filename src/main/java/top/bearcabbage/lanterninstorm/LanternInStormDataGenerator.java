package top.bearcabbage.lanterninstorm;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

import static top.bearcabbage.lanterninstorm.LanternInStormItems.*;

public class LanternInStormDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(RecipeGenerator::new);
	}

	private static class RecipeGenerator extends FabricRecipeProvider {

		public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		public void generate(RecipeExporter recipeExporter) {
			ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, LANTERN_CORE).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.IRON_BARS)
					.input('b', Items.DIAMOND)
					.criterion(FabricRecipeProvider.hasItem(Blocks.IRON_BARS),
							FabricRecipeProvider.conditionsFromItem(Blocks.IRON_BARS))
					.criterion(FabricRecipeProvider.hasItem(Items.DIAMOND),
							FabricRecipeProvider.conditionsFromItem(Items.DIAMOND))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, WHITE_PAPER_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Items.PAPER)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Items.PAPER),
							FabricRecipeProvider.conditionsFromItem(Items.PAPER))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, OAK_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.OAK_WOOD)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.OAK_WOOD),
							FabricRecipeProvider.conditionsFromItem(Blocks.OAK_WOOD))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);

		}
	}
}
