package https.flood.transparency;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class transparencyPlayer implements ClientModInitializer {
	public static final String MOD_ID = "transparency";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static int transparency = 100;
	private static boolean applyToSelf = true;
	private static boolean applyToOthers = true;
	private static KeyBinding configKey;

	@Override
	public void onInitializeClient() {
		configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.transparency.config", InputUtil.Type.KEYSYM, InputUtil.GLFW_KEY_O, "category.transparency"));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (configKey.wasPressed()) client.setScreen(createConfigScreen(client.currentScreen));
		});
		LOGGER.info("Transparency mod loaded!");
	}

	public static int getTransparency() { return transparency; }
	public static void setTransparency(int value) { transparency = value; }
	public static boolean shouldApplyToSelf() { return applyToSelf; }
	public static void setApplyToSelf(boolean value) { applyToSelf = value; }
	public static boolean shouldApplyToOthers() { return applyToOthers; }
	public static void setApplyToOthers(boolean value) { applyToOthers = value; }

	private Screen createConfigScreen(Screen parent) {
		ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.translatable("config.transparency.title"));
		ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.transparency.category"));
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		general.addEntry(entryBuilder.startIntSlider(Text.translatable("config.transparency.transparency"), transparency, 0, 100)
				.setDefaultValue(100).setSaveConsumer(transparencyPlayer::setTransparency).build());
		general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.transparency.applyToSelf"), applyToSelf)
				.setDefaultValue(true).setSaveConsumer(transparencyPlayer::setApplyToSelf)
				.setTooltip(Text.translatable("config.transparency.applyToSelf.tooltip")).build());
		general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.transparency.applyToOthers"), applyToOthers)
				.setDefaultValue(true).setSaveConsumer(transparencyPlayer::setApplyToOthers).build());

		return builder.build();
	}
}