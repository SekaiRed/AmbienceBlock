package com.sekai.ambienceblocks.ambience.sync;

class Countdown {
    int countdown;
    boolean active = true;

    public Countdown(int countdown) {
        this.countdown = countdown;
    }

    public int getTime() {
        return countdown;
    }

    public void setTime(int time) {
        countdown = time;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public boolean tick() {
        if(active) {
            countdown--;
            return countdown <= 0;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Countdown{" +
                "countdown=" + countdown +
                ", active=" + active +
                '}';
    }
}
