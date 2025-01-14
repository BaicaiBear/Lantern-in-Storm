package top.bearcabbage.lanterninstorm.lantern;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static top.bearcabbage.lanterninstorm.LanternInStorm.MOD_ID;


public abstract class SpiritLanternBlocks {
    /*2x铁门硬度基岩爆炸抗性*/
    public static final Block CUBIC_GLASS_LANTERN = (Block) Registry.register(Registries.BLOCK, Identifier.of(MOD_ID,"cubic_glass_lantern"), new SpiritLanternBlock(AbstractBlock.Settings.create().mapColor(MapColor.CYAN).strength(10.0F, 3600000.0F).sounds(BlockSoundGroup.GLASS).luminance((state) -> {return 10;}).nonOpaque().pistonBehavior(PistonBehavior.BLOCK)));
    public static final Block WHITE_PAPER_LANTERN = (Block) Registry.register(Registries.BLOCK, Identifier.of(MOD_ID,"white_paper_lantern"), new SpiritLanternBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(10.0F, 3600000.0F).sounds(BlockSoundGroup.LANTERN).luminance((state) -> {return 10;}).nonOpaque().pistonBehavior(PistonBehavior.BLOCK).dropsLike(CUBIC_GLASS_LANTERN)));
    public static final Block OAK_WOODEN_LANTERN = (Block) Registry.register(Registries.BLOCK, Identifier.of(MOD_ID,"oak_wooden_lantern"), new SpiritLanternBlock(AbstractBlock.Settings.create().mapColor(MapColor.BROWN).strength(10.0F, 3600000.0F).sounds(BlockSoundGroup.WOOD).luminance((state) -> {return 10;}).nonOpaque().pistonBehavior(PistonBehavior.BLOCK).dropsLike(CUBIC_GLASS_LANTERN)));

    public static void initialize(){}
}
