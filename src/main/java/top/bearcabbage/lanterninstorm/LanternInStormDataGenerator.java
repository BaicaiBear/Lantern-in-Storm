package top.bearcabbage.lanterninstorm;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.data.server.loottable.LootTableGenerator;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static top.bearcabbage.lanterninstorm.LanternInStorm.MOD_ID;
import static top.bearcabbage.lanterninstorm.LanternInStormItems.*;

public class LanternInStormDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
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
			ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, TALISMAN).pattern(" a ").pattern("bcb").pattern("b b")
					.input('a', Ingredient.ofItems(Items.DEAD_BUSH, Items.FERN, Items.CACTUS, Items.BAMBOO, Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS, Items.CRIMSON_ROOTS, Items.WARPED_ROOTS))
					.input('b', Items.STICK)
					.input('c', ItemTags.PLANKS)
					.criterion(FabricRecipeProvider.hasItem(Items.STICK),
							FabricRecipeProvider.conditionsFromItem(Items.STICK))
					.offerTo(recipeExporter);
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

	private static class LanguageGenerator extends FabricLanguageProvider {

		protected LanguageGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
			super(dataOutput, "zh_cn", registryLookup);
		}

		@Override
		public void generateTranslations(RegistryWrapper.WrapperLookup wrapperLookup, TranslationBuilder translationBuilder) {
			translationBuilder.add(SPIRIT_FRAG_ITEM, "灵魂碎片");
			translationBuilder.add(TALISMAN, "熊之符");
			translationBuilder.add(LANTERN_CORE, "彩灯核心");
			translationBuilder.add(FOX_TAIL_ITEM, "狐狸尾巴");
			translationBuilder.add(WHITE_PAPER_LANTERN_ITEM, "白色油纸灯");
			translationBuilder.add(OAK_WOODEN_LANTERN_ITEM, "橡木立方灯");
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
}
