package top.bearcabbage.lanterninstorm;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bearcabbage.lanterninstorm.block.SpiritLanternBlock;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.item.FoxTailItem;
import top.bearcabbage.lanterninstorm.item.PublicLanternItem;
import top.bearcabbage.lanterninstorm.item.SpirirtFragItem;
import top.bearcabbage.lanterninstorm.network.*;
import top.bearcabbage.lanterninstorm.player.PlayerEventRegistrator;

import static top.bearcabbage.lanterninstorm.LanternInStormClient.MOD_NAMESPACE;


public class LanternInStorm implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("lantern-in-storm");
	public static final NbtDataStorage LSData = new NbtDataStorage("LS_Data");
	public static final NbtDataStorage LanternData = new NbtDataStorage("Lantern_Data");

	public static final Block WHITE_PAPER_LANTERN = (Block)Registry.register(Registries.BLOCK, Identifier.of(MOD_NAMESPACE,"white_paper_lantern"),
			new SpiritLanternBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).requiresTool().strength(3.5F).sounds(BlockSoundGroup.LANTERN).luminance((state) -> {return 10;}).nonOpaque().pistonBehavior(PistonBehavior.DESTROY)));
	public static final Item WHITE_PAPER_LANTERN_ITEM = (Item)Registry.register(Registries.ITEM, Identifier.of(MOD_NAMESPACE,"white_paper_lantern"), new BlockItem(WHITE_PAPER_LANTERN, new Item.Settings()));


	@Override
	public void onInitialize() {
		// 获取配置文件
		PlayerDataApi.register(LSData);
		// 注册方块

		// 注册实体
		SpiritLanternEntity.register_SERVER();
		// 注册物品
		PublicLanternItem.initialize();
		SpirirtFragItem.initialize();
		FoxTailItem.initialize();
		// 注册事件
		PlayerEventRegistrator.register();

		// 读写灯笼列表
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			// 读取全局灯笼列表
			LanternInStormLanternManager.load(FabricLoader.getInstance().getConfigDir().resolve("Lantern-in-Storm/LanternsData.json"));
			LanternInStormSpiritManager.load(FabricLoader.getInstance().getConfigDir().resolve("Lantern-in-Storm/SpiritsData.json"));
		});
			ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			// 保存全局灯笼列表
			LanternInStormLanternManager.save(FabricLoader.getInstance().getConfigDir().resolve("Lantern-in-Storm/LanternsData.json"));
			LanternInStormSpiritManager.save(FabricLoader.getInstance().getConfigDir().resolve("Lantern-in-Storm/SpiritsData.json"));
		});
		// 注册网络数据包
		PayloadTypeRegistry.playS2C().register(SpiritMassPayload.ID, SpiritMassPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(LanternPosPayload.ID, LanternPosPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(OwnerQueryPayload.ID, OwnerQueryPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(OwnerQueryPayload.ID, OwnerQueryPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(DistributingSpiritsPayload.ID, DistributingSpiritsPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(DistributingSpiritsPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				// 处理接收到的数据
				DistributingSpiritsPayloadHandler.onDistributingSpiritsPayload(payload, context);
			});
		});
		ServerPlayNetworking.registerGlobalReceiver(OwnerQueryPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				// 处理接收到的数据
				OwnerQueryPayloadHandler.onOwnerQueryPayload_SERVER(payload, context);
			});
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			// 发送灯笼列表
			LanternInStormSpiritManager.sendAll(handler.player);
		});

	}
}