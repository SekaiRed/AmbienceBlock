package com.sekai.ambienceblocks.client.ambience;

//holds reference to a sound which can be grabbed later
public class AmbienceReservation {
    public AmbienceSlot owner;
    public AmbienceInstance reserved;
    public String playingResource;

    public AmbienceReservation(String playingResource) {
        this.playingResource = playingResource;
    }

    public void play() {
        //playingResource = new ResourceLocation(owner.data.getIntroName());

        //reserved = new AmbienceInstance(playingResource, ParsingUtil.tryParseEnum(d.getCategory().toUpperCase(), SoundCategory.MASTER), owner.getOrigin(), getVolumeInternal(d), getPitchInternal(d), false);

        AmbienceController.instance.handler.play(reserved);
    }
}