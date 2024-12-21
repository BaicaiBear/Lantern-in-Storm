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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.bearcabbage.lanterninstorm.entity.SpiritLanternEntity;
import top.bearcabbage.lanterninstorm.network.DistributingSpiritsPayload;
import top.bearcabbage.lanterninstorm.network.DistributingSpiritsPayloadHandler;
import top.bearcabbage.lanterninstorm.network.LanternBoundaryPayload;
import top.bearcabbage.lanterninstorm.player.PlayerEventRegistrator;


public class LanternInStorm implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("lantern-in-storm");
	public static final NbtDataStorage LSData = new NbtDataStorage("LS_Data");

	@Override
	public void onInitialize() {
		// 获取配置文件
		PlayerDataApi.register(LSData);
		// 注册实体
		SpiritLanternEntity.init();
		// 注册命令
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment)->LanternInStormCommands.registerCommands(dispatcher)); // 调用静态方法注册命令
		// 注册事件
		PlayerEventRegistrator.register();

		// 读写灯笼列表
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			// 读取全局灯笼列表
			LanternInStormSpiritManager.load(server,FabricLoader.getInstance().getConfigDir().resolve("Lantern-in-Storm/SpiritsData.json"));
		});
			ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			// 保存全局灯笼列表
			LanternInStormSpiritManager.save(FabricLoader.getInstance().getConfigDir().resolve("Lantern-in-Storm/SpiritsData.json"));
		});
		// 注册网络数据包
		PayloadTypeRegistry.playS2C().register(SpiritMassPayload.ID, SpiritMassPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(LanternPosPayload.ID, LanternPosPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(DistributingSpiritsPayload.ID, DistributingSpiritsPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(LanternBoundaryPayload.ID, LanternBoundaryPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(DistributingSpiritsPayload.ID, (payload, context) -> {
			context.server().execute(() -> {
				// 处理接收到的数据
				DistributingSpiritsPayloadHandler.onDistributingSpiritsPayload(payload, context);
			});
		});
	}
}