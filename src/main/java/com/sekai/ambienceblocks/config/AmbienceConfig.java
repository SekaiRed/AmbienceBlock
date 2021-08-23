package com.sekai.ambienceblocks.config;

import com.sekai.ambienceblocks.Main;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AmbienceConfig {
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    //Common
    public static int maxAmountOfConditions;
    public static int maxAmountOfCompendiumEntries;
    public static double maxEntitySearchRange;
    public static boolean shouldTrackBattles;
    //public static boolean shouldTrackStructures;

    public static void bakeConfig() {
        //Common
        //battleRadius = COMMON.battleRadius.get();
        maxAmountOfConditions = COMMON.maxAmountOfConditions.get();
        maxAmountOfCompendiumEntries = COMMON.maxAmountOfCompendiumEntries.get();
        maxEntitySearchRange = COMMON.maxEntitySearchRange.get();

        shouldTrackBattles = COMMON.shouldTrackBattles.get();
    }

    public static class CommonConfig {
        public final ForgeConfigSpec.IntValue maxAmountOfConditions;
        public final ForgeConfigSpec.IntValue maxAmountOfCompendiumEntries;
        public final ForgeConfigSpec.DoubleValue maxEntitySearchRange;
        public final ForgeConfigSpec.BooleanValue shouldTrackBattles;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Warning : If you lower the maximum then some conditions/entries may get truncated out of the game, so take caution when changing those values.");
            builder.push("Max Amounts");
            maxAmountOfConditions = builder
                    .comment("The maximal number of conditions that can be specified at once.")
                    .translation(Main.MODID + ".config." + "maxAmountOfConditions")
                    .defineInRange("maxAmountOfConditions", 15, 0, 50);
            maxAmountOfCompendiumEntries = builder
                    .comment("The maximal number of compendium entries that can be saved.")
                    .translation(Main.MODID + ".config." + "maxAmountOfCompendiumEntries")
                    .defineInRange("maxAmountOfCompendiumEntries", 20, 0, 50);
            maxEntitySearchRange = builder
                    .comment("The maximal range of detection of entities for the relevant conditions.")
                    .translation(Main.MODID + ".config." + "maxEntitySearchRange")
                    .defineInRange("maxEntitySearchRange", 32D, 0D, 128D);
            builder.pop();

            builder.push("Toggle Network Dependant Conditions");
            shouldTrackBattles = builder
                    .comment("Should the server notify the player of entities that currently target them?\nWhen disabled the player.in_battle always returns false.")
                    .translation(Main.MODID + ".config." + "shouldTrackBattles")
                    .define("shouldTrackBattles", true);
            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == AmbienceConfig.COMMON_SPEC) {
            bakeConfig();
        }
    }
}
