package net.Indyuce.mmocore.manager.data;

import net.Indyuce.mmocore.api.player.profess.PlayerClass;

import java.util.UUID;

public interface AbstractOfflinePlayerData {


    public abstract void removeFriend(UUID uuid);

    public abstract boolean hasFriend(UUID uuid);

    public abstract PlayerClass getProfess();

    public abstract int getLevel();

    public abstract long getLastLogin();
}
