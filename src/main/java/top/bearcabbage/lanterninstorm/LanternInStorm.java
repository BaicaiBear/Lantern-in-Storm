package top.bearcabbage.lanterninstorm;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.entity.entities.PrivateLantern;
import top.bearcabbage.lanterninstorm.entity.renderers.PrivateLanternRenderer;
import top.bearcabbage.lanterninstorm.entity.renderers.PublicLanternRenderer;
import top.bearcabbage.lanterninstorm.network.DistributingSpiritsPayload;
import top.bearcabbage.lanterninstorm.player.PlayerEventRegistrator;

import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.PRIVATE_LANTERN;
import static top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity.PUBLIC_LANTERN;

public class LanternInStorm implements ModInitializer {
	public static final String MOD_ID = "lantern-in-storm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final NbtDataStorage LSData = new NbtDataStorage("LS_Data");
	public static final EntityType<PrivateLantern> PRIVATE_LANTERN_ENTITY_TYPE = PRIVATE_LANTERN;

	@Override
	public void onInitialize() {
		// 获取配置文件
		PlayerDataApi.register(LSData);
		// 使用CommandRegistrationCallback.EVENT注册命令
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment)->LanternInStormCommands.registerCommands(dispatcher)); // 调用静态方法注册命令
		PlayerEventRegistrator.register();
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			// 读取全局灯笼列表
			LanternInStormSpiritManager.load(server,FabricLoader.getInstance().getConfigDir().resolve("Lantern-in-Storm/SpiritsData.json"));
		});
			ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			// 保存全局灯笼列表
			LanternInStormSpiritManager.save(FabricLoader.getInstance().getConfigDir().resolve("Lantern-in-Storm/SpiritsData.json"));
		});


//		ClientTickEvents.END_CLIENT_TICK.register(client -> {
//			//接受scroll事件然后进行对应spirit的加减
//		});
//
//		CustomPayload.Id<ScrollEventPayload> scrollPacketId = new CustomPayload.Id<>(SCROLL_PACKET_ID);
//		ServerPlayNetworking.registerGlobalReceiver(scrollPacketId, (scrollEventPayload, server, player, packetSender, packetContext) -> {
//			// 调用你的处理方法
//			PacketHandler.handleScrollEventOnServer();
//		});

		PayloadTypeRegistry.playC2S().register(DistributingSpiritsPayload.ID, DistributingSpiritsPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(DistributingSpiritsPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				// 处理接收到的数据
				LanternInStormSpiritManager.set(context.player().getUuid(), payload.lantern(), payload.spirits());
			});
		});
	}
}