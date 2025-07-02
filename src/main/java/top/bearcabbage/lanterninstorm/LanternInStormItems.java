package top.bearcabbage.lanterninstorm;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import dev.emi.trinkets.api.*;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.player.LiSPlayer;

import java.util.ArrayList;
import java.util.UUID;

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
    // 神话金属灯
    public static final Item MIDAS_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"midas_lantern"), new BlockItem(MIDAS_LANTERN, new Item.Settings()));
    public static final Item MORKITE_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"morkite_lantern"), new BlockItem(MORKITE_LANTERN, new Item.Settings()));
    public static final Item STARRITE_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"starrite_lantern"), new BlockItem(STARRITE_LANTERN, new Item.Settings()));
    public static final Item PROMETHIUM_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"promethium_lantern"), new BlockItem(PROMETHIUM_LANTERN, new Item.Settings()));

    // 限量版彩灯
    public static final Item SNOWMAN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"snowman_lantern"), new BlockItem(SNOWMAN_LANTERN, new Item.Settings()));
    public static final Item SNAKE_NEWYEAR_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"snake_newyear_lantern"), new BlockItem(SNAKE_NEWYEAR_LANTERN, new Item.Settings()));
    public static final Item KONGMING_LANTERN_2025_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"kongming_lantern_2025"), new BlockItem(KONGMING_LANTERN_2025, new Item.Settings()));
    public static final Item DEATHBEAR_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"deathbear_lantern"), new BlockItem(DEATHBEAR_LANTERN, new Item.Settings()));

    public static final Item TALISMAN = register("talisman", new TrinketItem(new Item.Settings().maxDamage(120)));//120s耐久
    public static final Item FLASHLIGHT = register("flashlight", new TrinketItem(new Item.Settings().maxDamage(600)));//10min耐久
    public static final Item LANTERN_CORE = register("lantern_core", new Item(new Item.Settings()));


    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_ID, path), item);
    }

    public static void initialize() {
        TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {
            if (stack.getItem() == FLASHLIGHT||stack.getItem() == TALISMAN) {
                return TrinketEnums.DropRule.KEEP;
            }
            return rule;
        });
    }
}
