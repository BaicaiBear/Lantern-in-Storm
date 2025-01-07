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
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

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
			ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, TALISMAN).pattern(" a ").pattern("bcb").pattern("b b")
					.input('a', Ingredient.ofItems(Items.DANDELION,Items.POPPY,Items.BLUE_ORCHID,Items.ALLIUM,Items.AZURE_BLUET,Items.RED_TULIP,Items.ORANGE_TULIP,Items.WHITE_TULIP,Items.PINK_TULIP,Items.OXEYE_DAISY,Items.CORNFLOWER,Items.LILY_OF_THE_VALLEY,Items.WITHER_ROSE,Items.AZALEA,Items.FLOWERING_AZALEA,Items.OAK_SAPLING,Items.SPRUCE_SAPLING,Items.BIRCH_SAPLING,Items.JUNGLE_SAPLING,Items.ACACIA_SAPLING,Items.DARK_OAK_SAPLING,Items.CHERRY_SAPLING,Items.MANGROVE_PROPAGULE,Items.DEAD_BUSH, Items.FERN,Items.CACTUS,Items.BAMBOO,Items.BROWN_MUSHROOM,Items.RED_MUSHROOM,Items.CRIMSON_FUNGUS,Items.WARPED_FUNGUS,Items.CRIMSON_ROOTS,Items.WARPED_ROOTS))
					.input('b', Items.STICK)
					.input('c', ItemTags.PLANKS)
					.criterion(FabricRecipeProvider.hasItem(Items.DANDELION),
							FabricRecipeProvider.conditionsFromItem(Items.DANDELION))
					.criterion(FabricRecipeProvider.hasItem(Items.POPPY),
							FabricRecipeProvider.conditionsFromItem(Items.POPPY))
					.criterion(FabricRecipeProvider.hasItem(Items.BLUE_ORCHID),
							FabricRecipeProvider.conditionsFromItem(Items.BLUE_ORCHID))
					.criterion(FabricRecipeProvider.hasItem(Items.ALLIUM),
							FabricRecipeProvider.conditionsFromItem(Items.ALLIUM))
					.criterion(FabricRecipeProvider.hasItem(Items.AZURE_BLUET),
							FabricRecipeProvider.conditionsFromItem(Items.AZURE_BLUET))
					.criterion(FabricRecipeProvider.hasItem(Items.RED_TULIP),
							FabricRecipeProvider.conditionsFromItem(Items.RED_TULIP))
					.criterion(FabricRecipeProvider.hasItem(Items.ORANGE_TULIP),
							FabricRecipeProvider.conditionsFromItem(Items.ORANGE_TULIP))
					.criterion(FabricRecipeProvider.hasItem(Items.WHITE_TULIP),
							FabricRecipeProvider.conditionsFromItem(Items.WHITE_TULIP))
					.criterion(FabricRecipeProvider.hasItem(Items.PINK_TULIP),
							FabricRecipeProvider.conditionsFromItem(Items.PINK_TULIP))
					.criterion(FabricRecipeProvider.hasItem(Items.OXEYE_DAISY),
							FabricRecipeProvider.conditionsFromItem(Items.OXEYE_DAISY))
					.criterion(FabricRecipeProvider.hasItem(Items.CORNFLOWER),
							FabricRecipeProvider.conditionsFromItem(Items.CORNFLOWER))
					.criterion(FabricRecipeProvider.hasItem(Items.LILY_OF_THE_VALLEY),
							FabricRecipeProvider.conditionsFromItem(Items.LILY_OF_THE_VALLEY))
					.criterion(FabricRecipeProvider.hasItem(Items.WITHER_ROSE),
							FabricRecipeProvider.conditionsFromItem(Items.WITHER_ROSE))
					.criterion(FabricRecipeProvider.hasItem(Items.AZALEA),
							FabricRecipeProvider.conditionsFromItem(Items.AZALEA))
					.criterion(FabricRecipeProvider.hasItem(Items.FLOWERING_AZALEA),
							FabricRecipeProvider.conditionsFromItem(Items.FLOWERING_AZALEA))
					.criterion(FabricRecipeProvider.hasItem(Items.OAK_SAPLING),
							FabricRecipeProvider.conditionsFromItem(Items.OAK_SAPLING))
					.criterion(FabricRecipeProvider.hasItem(Items.SPRUCE_SAPLING),
							FabricRecipeProvider.conditionsFromItem(Items.SPRUCE_SAPLING))
					.criterion(FabricRecipeProvider.hasItem(Items.BIRCH_SAPLING),
							FabricRecipeProvider.conditionsFromItem(Items.BIRCH_SAPLING))
					.criterion(FabricRecipeProvider.hasItem(Items.JUNGLE_SAPLING),
							FabricRecipeProvider.conditionsFromItem(Items.JUNGLE_SAPLING))
					.criterion(FabricRecipeProvider.hasItem(Items.ACACIA_SAPLING),
							FabricRecipeProvider.conditionsFromItem(Items.ACACIA_SAPLING))
					.criterion(FabricRecipeProvider.hasItem(Items.DARK_OAK_SAPLING),
							FabricRecipeProvider.conditionsFromItem(Items.DARK_OAK_SAPLING))
					.criterion(FabricRecipeProvider.hasItem(Items.CHERRY_SAPLING),
							FabricRecipeProvider.conditionsFromItem(Items.CHERRY_SAPLING))
					.criterion(FabricRecipeProvider.hasItem(Items.MANGROVE_PROPAGULE),
							FabricRecipeProvider.conditionsFromItem(Items.MANGROVE_PROPAGULE))
					.criterion(FabricRecipeProvider.hasItem(Items.DEAD_BUSH),
							FabricRecipeProvider.conditionsFromItem(Items.DEAD_BUSH))
					.criterion(FabricRecipeProvider.hasItem(Items.FERN),
							FabricRecipeProvider.conditionsFromItem(Items.FERN))
					.criterion(FabricRecipeProvider.hasItem(Items.CACTUS),
							FabricRecipeProvider.conditionsFromItem(Items.CACTUS))
					.criterion(FabricRecipeProvider.hasItem(Items.BAMBOO),
							FabricRecipeProvider.conditionsFromItem(Items.BAMBOO))
					.criterion(FabricRecipeProvider.hasItem(Items.BROWN_MUSHROOM),
							FabricRecipeProvider.conditionsFromItem(Items.BROWN_MUSHROOM))
					.criterion(FabricRecipeProvider.hasItem(Items.RED_MUSHROOM),
							FabricRecipeProvider.conditionsFromItem(Items.RED_MUSHROOM))
					.criterion(FabricRecipeProvider.hasItem(Items.CRIMSON_FUNGUS),
							FabricRecipeProvider.conditionsFromItem(Items.CRIMSON_FUNGUS))
					.criterion(FabricRecipeProvider.hasItem(Items.WARPED_FUNGUS),
							FabricRecipeProvider.conditionsFromItem(Items.WARPED_FUNGUS))
					.criterion(FabricRecipeProvider.hasItem(Items.CRIMSON_ROOTS),
							FabricRecipeProvider.conditionsFromItem(Items.CRIMSON_ROOTS))
					.criterion(FabricRecipeProvider.hasItem(Items.WARPED_ROOTS),
							FabricRecipeProvider.conditionsFromItem(Items.WARPED_ROOTS))
					.criterion(FabricRecipeProvider.hasItem(Items.STICK),
							FabricRecipeProvider.conditionsFromItem(Items.STICK))
					.offerTo(recipeExporter);
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
