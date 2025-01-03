package top.bearcabbage.lanterninstorm.item;



import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static top.bearcabbage.lanterninstorm.LanternInStormClient.MOD_NAMESPACE;


public class FoxTailItem {
    public static final Item SPIRIT_FRAG_ITEM = register("fox_tail_item", new Item(new Item.Settings().maxCount(99)));

    public static <T extends Item> T register(String path, T item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_NAMESPACE, path), item);
    }

    public static void initialize() {
    }

}
