package fr.mist;

import fr.mist.core.consumers.commands.ClientCommandManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class MistMod implements ClientModInitializer {
	public static final String MOD_ID = "mist";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		Optional<ModContainer> mistMod = FabricLoader.getInstance().getModContainer("mist");
		if (mistMod.isEmpty()) {
			error("Where is my Mist? :(");
			return;
		}

		ClientCommandManager.register();
	}

	public static void error(String msg) {
		LOGGER.error(msg);
	}

	public static void error(String msg, Throwable t) {
		LOGGER.error(msg, t);
	}
}