package top.bearcabbage.lanterninstorm.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import top.bearcabbage.lanterninstorm.LanternInStormSpiritManager;
import top.bearcabbage.lanterninstorm.interfaces.PlayerAccessor;

import static top.bearcabbage.lanterninstorm.LanternInStormClient.MOD_NAMESPACE;


public class SpirirtFragItem {
    public static final Item SPIRIT_FRAG_ITEM = register("spirit_frag_item", new Item(new Item.Settings().maxCount(99)));

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_NAMESPACE, path), item);
    }

    public static TypedActionResult<ItemStack> use(World world, PlayerEntity player, ItemStack itemStack) {
        if (world.isClient) return TypedActionResult.success(itemStack);
        int requiredCount = ((PlayerAccessor)player).getLS().getSpiritUpgradeCount();
        if (itemStack.getCount() >= requiredCount){
            itemStack.decrementUnlessCreative(requiredCount,player);
            ((PlayerAccessor)player).getLS().upgradeSpirit();
            ((ServerPlayerEntity) player).networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.literal("增加了1点灵魂，现有剩余灵魂量为："+ LanternInStormSpiritManager.get_left(player.getUuid()))));
            return TypedActionResult.consume(itemStack);
        }
        return TypedActionResult.fail(itemStack);
    }


    public static void initialize() {
    }

}
