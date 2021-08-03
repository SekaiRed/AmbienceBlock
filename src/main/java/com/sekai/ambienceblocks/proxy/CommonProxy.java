package com.sekai.ambienceblocks.proxy;

import net.minecraft.item.Item;

public class CommonProxy {
    public void init()
    {
        /*CapabilityManager.INSTANCE.register(ISp.class, new SpStorage(), Sp::new);
        CapabilityManager.INSTANCE.register(IStats.class, new StatsStorage(), Stats::new);

        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        ModEvents.registerEvents();

        bindingProfile = new KeyBinding("Player Profile", Keyboard.KEY_I, "Persona 5 Battle Mod");
        ClientRegistry.registerKeyBinding(bindingProfile);*/
    }

    public void registerItemRenderer(Item item, int meta, String id) {}

    public void registerModelVariants() {}
}
