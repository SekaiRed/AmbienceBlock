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
    public static double maxBlockSearchRange;
    public static boolean shouldTrackBattles;
    public static int targetCountdownAmount;
    public static int structureCountdownAmount;
    //public static boolean shouldTrackStructures;

    public static void bakeConfig() {
        //Common
        maxAmountOfConditions = COMMON.maxAmountOfConditions.get();
        maxAmountOfCompendiumEntries = COMMON.maxAmountOfCompendiumEntries.get();
        maxEntitySearchRange = COMMON.maxEntitySearchRange.get();
        maxBlockSearchRange = COMMON.maxBlockSearchRange.get();
        shouldTrackBattles = COMMON.shouldTrackBattles.get();
        targetCountdownAmount = COMMON.targetCountdownAmount.get();
        structureCountdownAmount = COMMON.structureCountdownAmount.get();
    }

    public static class CommonConfig {
        public final ForgeConfigSpec.IntValue maxAmountOfConditions;
        public final ForgeConfigSpec.IntValue maxAmountOfCompendiumEntries;
        public final ForgeConfigSpec.DoubleValue maxEntitySearchRange;
        public final ForgeConfigSpec.DoubleValue maxBlockSearchRange;
        public final ForgeConfigSpec.BooleanValue shouldTrackBattles;
        public final ForgeConfigSpec.IntValue targetCountdownAmount;
        public final ForgeConfigSpec.IntValue structureCountdownAmount;

        public CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Warning : If you lower the maximum then some conditions/entries may get truncated out of the game, so take caution when changing those values.");
            builder.push("Max Amounts");
            maxAmountOfConditions = builder
                    .comment("The maximal number of conditions that can be specified at once.")
                    .translation(Main.MODID + ".config." + "maxAmountOfConditions")
                    .defineInRange("maxAmountOfConditions", 15, 0, 100);
            maxAmountOfCompendiumEntries = builder
                    .comment("The maximal number of compendium entries that can be saved.")
                    .translation(Main.MODID + ".config." + "maxAmountOfCompendiumEntries")
                    .defineInRange("maxAmountOfCompendiumEntries", 20, 0, 100);
            maxEntitySearchRange = builder
                    .comment("The maximal range of detection of entities for the relevant conditions.")
                    .translation(Main.MODID + ".config." + "maxEntitySearchRange")
                    .defineInRange("maxEntitySearchRange", 64D, 0D, 256D);
            maxBlockSearchRange = builder
                    .comment("The maximal range of detection of block density for the relevant conditions.\n" +
                            "Warning : Higher values will very quickly start affecting your performances, be careful.")
                    .translation(Main.MODID + ".config." + "maxBlockSearchRange")
                    .defineInRange("maxBlockSearchRange", 16D, 0D, 128D);
            builder.pop();

            builder.push("Data Sync");
            shouldTrackBattles = builder
                    .comment("Should the server notify the player of entities that currently target them?\nWhen disabled the player.in_battle always returns false.")
                    .translation(Main.MODID + ".config." + "shouldTrackBattles")
                    .define("shouldTrackBattles", true);
            targetCountdownAmount = builder
                    .comment("The maximal amount of ticks the client will keep track of entities fighting you.\n" +
                            "This works alongside the timer value in the player.in_battle condition to tell you how long it has been since you were targeted by that enemy.\n" +
                            "As such, the parameter in the condition shouldn't exceed this value.")
                    .translation(Main.MODID + ".config." + "targetCountdownAmount")
                    .defineInRange("targetCountdownAmount", 400, 0, Integer.MAX_VALUE);
            structureCountdownAmount = builder
                    .comment("The maximal amount of ticks the client will keep track of whether or not you're within a structure.")
                    .translation(Main.MODID + ".config." + "structureCountdownAmount")
                    .defineInRange("structureCountdownAmount", 20, 0, Integer.MAX_VALUE);
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
