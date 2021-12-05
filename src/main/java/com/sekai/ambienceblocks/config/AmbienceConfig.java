package com.sekai.ambienceblocks.config;

import com.sekai.ambienceblocks.Main;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Main.MODID)
@Config.LangKey(Main.MODID + ".config.title")
public class AmbienceConfig {
    @Config.Comment({"The maximal number of conditions that can be specified at once.", "Warning : If you lower the maximum then some conditions may get truncated out of the world's data, so take caution when changing those values."})
    @Config.RangeInt(min = 0)
    public static int maxAmountOfConditions = 15;

    @Config.Comment({"The maximal number of compendium entries that can be saved.", "Warning : If you lower the maximum then some compendium entries may get truncated out of the world's data, so take caution when changing those values."})
    @Config.RangeInt(min = 0)
    public static int maxAmountOfCompendiumEntries = 20;

    @Config.Comment({"The maximal range of detection of entities.", "Only used by the player.entity_in_range condition."})
    @Config.RangeDouble(min = 0D)
    public static double maxEntitySearchRange = 64D;

    @Config.Comment({"The maximal range of detection of block density for the relevant conditions.", "Only used by the player.block.density condition.", "Warning : Higher values will very quickly start to affect your performances, be careful."})
    @Config.RangeDouble(min = 0D, max = 128D)
    public static double maxBlockSearchRange = 16D;

    @Config.Comment({"Should the server notify the player of entities that currently target them?", "When disabled the player.in_battle always returns false."})
    public static boolean shouldTrackBattles = true;

    @Config.Comment({"The maximal amount of ticks the client will keep track of entities fighting you.", "This works alongside the timer value in the player.in_battle condition to tell you how long it has been since you were targeted by that enemy.", "As such, the parameter in the condition shouldn't exceed this maximal value."})
    @Config.RangeInt(min = 0)
    public static int targetCountdownAmount = 400;

    @Config.Comment({"The maximal amount of ticks the client will keep track of whether or not you're within a structure."})
    @Config.RangeInt(min = 0)
    public static int structureCountdownAmount = 20;

    @Mod.EventBusSubscriber(modid = Main.MODID)
    private static class ConfigEventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Main.MODID)) {
                ConfigManager.sync(Main.MODID, Config.Type.INSTANCE);
            }
        }
    }
}
