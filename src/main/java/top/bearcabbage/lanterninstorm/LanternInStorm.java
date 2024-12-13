package top.bearcabbage.lanterninstorm;

import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.playerdata.api.storage.NbtDataStorage;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanternInStorm implements ModInitializer {
	public static final String MOD_ID = "lantern-in-storm";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final NbtDataStorage LSData = new NbtDataStorage("LS_Data");

	@Override
	public void onInitialize() {

		// 获取配置文件
		PlayerDataApi.register(LSData);
	}
}