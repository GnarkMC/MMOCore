package net.Indyuce.mmocore.listener.option;

import io.lumine.mythic.lib.api.event.SynchronizedDataLoadEvent;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.event.PlayerChangeClassEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.InventoryManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ForceChooseClassListener implements Listener {
    @EventHandler
    public void onJoin(SynchronizedDataLoadEvent event) {
        if (event.getManager().getOwningPlugin().equals(MMOCore.plugin)) {
            PlayerData playerData = PlayerData.get(event.getHolder().getProfileId());
            if (playerData.isProfessNull()) {

                // Open GUI
                InventoryManager.CLASS_SELECT.newInventory(playerData).open();

                // Re-open GUI till the player
                new TemporaryListener(PlayerChangeClassEvent.getHandlerList(), InventoryCloseEvent.getHandlerList()) {

                    @EventHandler
                    public void whenClosed(InventoryCloseEvent event) {
                        if (event.getPlayer().equals(playerData.getPlayer()))
                            InventoryManager.CLASS_SELECT.newInventory(playerData).open();
                    }

                    @EventHandler
                    public void whenChoose(PlayerChangeClassEvent event) {
                        if (event.getPlayer().equals(playerData.getPlayer())) close();
                    }

                    @Override
                    public void whenClosed() {

                    }
                };

            }
        }
    }

}
