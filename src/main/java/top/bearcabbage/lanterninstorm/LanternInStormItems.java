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
import top.bearcabbage.lanterninstorm.player.PlayerAccessor;

import static top.bearcabbage.lanterninstorm.LanternInStorm.MOD_ID;
import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks.OAK_WOODEN_LANTERN;
import static top.bearcabbage.lanterninstorm.lantern.SpiritLanternBlocks.WHITE_PAPER_LANTERN;

public class LanternInStormItems {
    public static final Item WHITE_PAPER_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"white_paper_lantern"), new BlockItem(WHITE_PAPER_LANTERN, new Item.Settings()));
    public static final Item OAK_WOODEN_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_ID,"oak_wooden_lantern"), new BlockItem(OAK_WOODEN_LANTERN, new Item.Settings()));

    public static final Item TALISMAN = register("talisman", new Item(new Item.Settings().maxDamage(120)));//120s耐久
    public static final Item LANTERN_CORE = register("lantern_core", new Item(new Item.Settings()));
    public static final Item FOX_TAIL_ITEM = register("fox_tail_item", new Item(new Item.Settings().maxCount(99)));
    public static final Item SPIRIT_FRAG_ITEM = register("spirit_frag_item", new Item(new Item.Settings().maxCount(99)));


    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_ID, path), item);
    }

    public static TypedActionResult<ItemStack> useSpiritFrag(World world, PlayerEntity player, ItemStack itemStack) {
        if (world.isClient) return TypedActionResult.success(itemStack);
        int requiredCount = ((PlayerAccessor)player).getLS().getSpiritUpgradeCount();
        if (itemStack.getCount() >= requiredCount){
            itemStack.decrementUnlessCreative(requiredCount,player);
            ((PlayerAccessor)player).getLS().addSpirit(1);
            ((ServerPlayerEntity) player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("增加了1点灵魂，现有剩余灵魂量为："+ ((PlayerAccessor)player).getLS().getSpirit())));
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }

    public static void initialize() {
    }
}
