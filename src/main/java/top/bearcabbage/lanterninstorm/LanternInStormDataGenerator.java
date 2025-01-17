package top.bearcabbage.lanterninstorm;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks;

import java.util.concurrent.CompletableFuture;

import static top.bearcabbage.lanterninstorm.LanternInStorm.MOD_ID;
import static top.bearcabbage.lanterninstorm.LanternInStormItems.*;

public class LanternInStormDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
//		pack.addProvider(BlockTagGenerator::new);
//		pack.addProvider(BlockLootTable::new);
//		pack.addProvider(RecipeGenerator::new);
//		pack.addProvider(LanguageGenerator::new);
	}

	private static class RecipeGenerator extends FabricRecipeProvider {

		public RecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		public void generate(RecipeExporter recipeExporter) {
//			ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, TALISMAN).pattern(" a ").pattern("bcb").pattern("b b")
//					.input('a', Ingredient.ofItems(Items.DEAD_BUSH, Items.FERN, Items.CACTUS, Items.BAMBOO, Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS, Items.CRIMSON_ROOTS, Items.WARPED_ROOTS))
//					.input('b', Items.STICK)
//					.input('c', ItemTags.PLANKS)
//					.criterion(FabricRecipeProvider.hasItem(Items.STICK),
//							FabricRecipeProvider.conditionsFromItem(Items.STICK))
//					.offerTo(recipeExporter);
//			ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, TALISMAN).pattern(" a ").pattern("bcb").pattern("b b")
//					.input('a', ItemTags.SAPLINGS)
//					.input('b', Items.STICK)
//					.input('c', ItemTags.PLANKS)
//					.criterion(FabricRecipeProvider.hasItem(Items.STICK),
//							FabricRecipeProvider.conditionsFromItem(Items.STICK))
//					.offerTo(recipeExporter);
//			ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, TALISMAN).pattern(" a ").pattern("bcb").pattern("b b")
//					.input('a', ItemTags.FLOWERS)
//					.input('b', Items.STICK)
//					.input('c', ItemTags.PLANKS)
//					.criterion(FabricRecipeProvider.hasItem(Items.STICK),
//							FabricRecipeProvider.conditionsFromItem(Items.STICK))
//					.offerTo(recipeExporter);
//			ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, LANTERN_CORE).pattern("aaa").pattern("aba").pattern("aaa")
//					.input('a', Blocks.IRON_BARS)
//					.input('b', Items.DIAMOND)
//					.criterion(FabricRecipeProvider.hasItem(Blocks.IRON_BARS),
//							FabricRecipeProvider.conditionsFromItem(Blocks.IRON_BARS))
//					.criterion(FabricRecipeProvider.hasItem(Items.DIAMOND),
//							FabricRecipeProvider.conditionsFromItem(Items.DIAMOND))
//					.offerTo(recipeExporter);
//			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, WHITE_PAPER_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
//					.input('a', Items.PAPER)
//					.input('b', LANTERN_CORE)
//					.criterion(FabricRecipeProvider.hasItem(Items.PAPER),
//							FabricRecipeProvider.conditionsFromItem(Items.PAPER))
//					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
//							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
//					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, OAK_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.OAK_WOOD)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.OAK_WOOD),
							FabricRecipeProvider.conditionsFromItem(Blocks.OAK_WOOD))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, SPRUCE_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.SPRUCE_WOOD)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.SPRUCE_WOOD),
							FabricRecipeProvider.conditionsFromItem(Blocks.SPRUCE_WOOD))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BIRCH_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.BIRCH_WOOD)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.BIRCH_WOOD),
							FabricRecipeProvider.conditionsFromItem(Blocks.BIRCH_WOOD))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, JUNGLE_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.JUNGLE_WOOD)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.JUNGLE_WOOD),
							FabricRecipeProvider.conditionsFromItem(Blocks.JUNGLE_WOOD))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ACACIA_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.ACACIA_WOOD)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.ACACIA_WOOD),
							FabricRecipeProvider.conditionsFromItem(Blocks.ACACIA_WOOD))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, DARK_OAK_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.DARK_OAK_WOOD)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.DARK_OAK_WOOD),
							FabricRecipeProvider.conditionsFromItem(Blocks.DARK_OAK_WOOD))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, MANGROVE_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.MANGROVE_WOOD)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.MANGROVE_WOOD),
							FabricRecipeProvider.conditionsFromItem(Blocks.MANGROVE_WOOD))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, CHERRY_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.CHERRY_WOOD)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.CHERRY_WOOD),
							FabricRecipeProvider.conditionsFromItem(Blocks.CHERRY_WOOD))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, BAMBOO_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.BAMBOO)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.BAMBOO),
							FabricRecipeProvider.conditionsFromItem(Blocks.BAMBOO))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, CRIMSON_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.CRIMSON_HYPHAE)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.CRIMSON_HYPHAE),
							FabricRecipeProvider.conditionsFromItem(Blocks.CRIMSON_HYPHAE))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);
			ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, WARPED_WOODEN_LANTERN_ITEM).pattern("aaa").pattern("aba").pattern("aaa")
					.input('a', Blocks.WARPED_HYPHAE)
					.input('b', LANTERN_CORE)
					.criterion(FabricRecipeProvider.hasItem(Blocks.WARPED_HYPHAE),
							FabricRecipeProvider.conditionsFromItem(Blocks.WARPED_HYPHAE))
					.criterion(FabricRecipeProvider.hasItem(LANTERN_CORE),
							FabricRecipeProvider.conditionsFromItem(LANTERN_CORE))
					.offerTo(recipeExporter);

		}
	}

	private static class LanguageGenerator extends FabricLanguageProvider {

		protected LanguageGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, "zh_cn", registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
			translationBuilder.add(SPIRIT_FRAG_ITEM, "灵魂碎片");
			translationBuilder.add(TALISMAN, "熊之符");
			translationBuilder.add(LANTERN_CORE, "彩灯核心");
//			translationBuilder.add(WHITE_PAPER_LANTERN_ITEM, "白色油纸灯");
			translationBuilder.add(OAK_WOODEN_LANTERN_ITEM, "橡木立方灯");
			translationBuilder.add(SPRUCE_WOODEN_LANTERN_ITEM, "云杉木立方灯");
			translationBuilder.add(BIRCH_WOODEN_LANTERN_ITEM, "白桦木立方灯");
			translationBuilder.add(JUNGLE_WOODEN_LANTERN_ITEM, "丛林木立方灯");
			translationBuilder.add(ACACIA_WOODEN_LANTERN_ITEM, "金合欢木立方灯");
			translationBuilder.add(DARK_OAK_WOODEN_LANTERN_ITEM, "深色橡木立方灯");
			translationBuilder.add(MANGROVE_WOODEN_LANTERN_ITEM, "红树木立方灯");
			translationBuilder.add(CHERRY_WOODEN_LANTERN_ITEM, "樱花木立方灯");
			translationBuilder.add(BAMBOO_WOODEN_LANTERN_ITEM, "竹立方灯");
			translationBuilder.add(CRIMSON_WOODEN_LANTERN_ITEM, "绯红立方灯");
			translationBuilder.add(WARPED_WOODEN_LANTERN_ITEM, "诡异立方灯");
		}
	}

	private static class BlockLootTable extends FabricBlockLootTableProvider {

		protected BlockLootTable(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, registryLookup);
		}

		@Override
		public void generate() {
			addDrop(SpiritLanternBlocks.CUBIC_GLASS_LANTERN, LootTable.builder().pool(addSurvivesExplosionCondition(LANTERN_CORE, LootPool.builder()
					.rolls(new BinomialLootNumberProvider(new ConstantLootNumberProvider(1), new ConstantLootNumberProvider(0.8F)))
					.with(ItemEntry.builder(LANTERN_CORE)))));
		}
	}

	private static class BlockTagGenerator extends FabricTagProvider<Block> {
		private static final TagKey<Block> SPIRIT_LANTERN = TagKey.of(Registries.BLOCK.getKey(), Identifier.of(MOD_ID, "spirit_lantern"));

		public BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
			super(output, Registries.BLOCK.getKey(), registriesFuture);
		}

		@Override
		protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
			getOrCreateTagBuilder(SPIRIT_LANTERN).add(
					SpiritLanternBlocks.CUBIC_GLASS_LANTERN,
					SpiritLanternBlocks.WHITE_PAPER_LANTERN,
					SpiritLanternBlocks.OAK_WOODEN_LANTERN);
		}
	}
}
