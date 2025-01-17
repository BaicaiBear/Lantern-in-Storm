package top.bearcabbage.lanterninstorm;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.player.LiSPlayer;

import static top.bearcabbage.lanterninstorm.LanternInStorm.MOD_ID;
import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks.*;

public class LanternInStormItems {
    public static final Item CUBIC_GLASS_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"cubic_glass_lantern"), new BlockItem(CUBIC_GLASS_LANTERN, new Item.Settings()));

//    public static final Item WHITE_PAPER_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"white_paper_lantern"), new BlockItem(WHITE_PAPER_LANTERN, new Item.Settings()));

    // 木质立方灯
    public static final Item OAK_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"oak_wooden_lantern"), new BlockItem(OAK_WOODEN_LANTERN, new Item.Settings()));
    public static final Item SPRUCE_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"spruce_wooden_lantern"), new BlockItem(SPRUCE_WOODEN_LANTERN, new Item.Settings()));
    public static final Item BIRCH_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"birch_wooden_lantern"), new BlockItem(BIRCH_WOODEN_LANTERN, new Item.Settings()));
    public static final Item JUNGLE_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"jungle_wooden_lantern"), new BlockItem(JUNGLE_WOODEN_LANTERN, new Item.Settings()));
    public static final Item ACACIA_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"acacia_wooden_lantern"), new BlockItem(ACACIA_WOODEN_LANTERN, new Item.Settings()));
    public static final Item DARK_OAK_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"dark_oak_wooden_lantern"), new BlockItem(DARK_OAK_WOODEN_LANTERN, new Item.Settings()));
    public static final Item MANGROVE_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"mangrove_wooden_lantern"), new BlockItem(MANGROVE_WOODEN_LANTERN, new Item.Settings()));
    public static final Item CHERRY_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"cherry_wooden_lantern"), new BlockItem(CHERRY_WOODEN_LANTERN, new Item.Settings()));
    public static final Item BAMBOO_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"bamboo_wooden_lantern"), new BlockItem(BAMBOO_WOODEN_LANTERN, new Item.Settings()));
    public static final Item CRIMSON_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"crimson_wooden_lantern"), new BlockItem(CRIMSON_WOODEN_LANTERN, new Item.Settings()));
    public static final Item WARPED_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"warped_wooden_lantern"), new BlockItem(WARPED_WOODEN_LANTERN, new Item.Settings()));
    public static final Item FIR_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"fir_wooden_lantern"), new BlockItem(FIR_WOODEN_LANTERN, new Item.Settings()));
    public static final Item PINE_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"pine_wooden_lantern"), new BlockItem(PINE_WOODEN_LANTERN, new Item.Settings()));
    public static final Item MAPLE_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"maple_wooden_lantern"), new BlockItem(MAPLE_WOODEN_LANTERN, new Item.Settings()));
    public static final Item REDWOOD_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"redwood_wooden_lantern"), new BlockItem(REDWOOD_WOODEN_LANTERN, new Item.Settings()));
    public static final Item MAHOGANY_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"mahogany_wooden_lantern"), new BlockItem(MAHOGANY_WOODEN_LANTERN, new Item.Settings()));
    public static final Item JACARANDA_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"jacaranda_wooden_lantern"), new BlockItem(JACARANDA_WOODEN_LANTERN, new Item.Settings()));
    public static final Item PALM_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"palm_wooden_lantern"), new BlockItem(PALM_WOODEN_LANTERN, new Item.Settings()));
    public static final Item WILLOW_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"willow_wooden_lantern"), new BlockItem(WILLOW_WOODEN_LANTERN, new Item.Settings()));
    public static final Item DEAD_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"dead_wooden_lantern"), new BlockItem(DEAD_WOODEN_LANTERN, new Item.Settings()));
    public static final Item MAGIC_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"magic_wooden_lantern"), new BlockItem(MAGIC_WOODEN_LANTERN, new Item.Settings()));
    public static final Item UMBRAN_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"umbran_wooden_lantern"), new BlockItem(UMBRAN_WOODEN_LANTERN, new Item.Settings()));
    public static final Item HELLBARK_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"hellbark_wooden_lantern"), new BlockItem(HELLBARK_WOODEN_LANTERN, new Item.Settings()));
    public static final Item EMPYREAL_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"empyreal_wooden_lantern"), new BlockItem(EMPYREAL_WOODEN_LANTERN, new Item.Settings()));



    public static final Item TALISMAN = register("talisman", new Item(new Item.Settings().maxDamage(120)));//120s耐久
    public static final Item LANTERN_CORE = register("lantern_core", new Item(new Item.Settings()));
    public static final Item SPIRIT_FRAG_ITEM = register("spirit_frag_item", new Item(new Item.Settings().maxCount(99)));


    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_ID, path), item);
    }

    public static TypedActionResult<ItemStack> useSpiritFrag(World world, PlayerEntity player, ItemStack itemStack) {
        if (world.isClient) return TypedActionResult.success(itemStack);
        int requiredCount = ((LiSPlayer)player).getLS().getSpiritUpgradeCount();
        if (itemStack.getCount() >= requiredCount){
            itemStack.decrementUnlessCreative(requiredCount,player);
            ((LiSPlayer)player).getLS().addSpirit(1);
            ((ServerPlayerEntity) player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("增加了1点灵魂，现有剩余灵魂量为："+ ((LiSPlayer)player).getLS().getSpirit())));
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    public static void initialize() {
    }
}
