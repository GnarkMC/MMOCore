package net.Indyuce.mmocore.api.event;


import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerExitCastingModeEvent extends PlayerDataEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled = false;

    public PlayerExitCastingModeEvent(@NotNull Player who) {
        super(PlayerData.get(who.getUniqueId()));
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
