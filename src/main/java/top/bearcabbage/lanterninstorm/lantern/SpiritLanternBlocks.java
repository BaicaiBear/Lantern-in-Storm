package top.bearcabbage.lanterninstorm.lantern;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static top.bearcabbage.lanterninstorm.LanternInStorm.MOD_ID;


public abstract class SpiritLanternBlocks {
    public static final Block WHITE_PAPER_LANTERN = (Block) Registry.register(Registries.BLOCK, Identifier.of(MOD_ID,"white_paper_lantern"), new SpiritLanternBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).requiresTool().strength(10.0F, 3600000.0F)/*2x铁门硬度基岩爆炸抗性*/.sounds(BlockSoundGroup.LANTERN).luminance((state) -> {return 10;}).nonOpaque().pistonBehavior(PistonBehavior.BLOCK)));

    public static void initialize(){}
}
