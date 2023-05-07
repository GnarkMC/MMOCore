package net.Indyuce.mmocore.listener;

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
                InventoryManager.CLASS_SELECT.newInventory(playerData).open();
                //Give 1 class point to make sure the player can choose a class.
                playerData.setClassPoints(1);
                TemporaryListener closeGUIListener = new TemporaryListener(InventoryCloseEvent.getHandlerList()) {

                    @EventHandler
                    public void whenClosed(InventoryCloseEvent e) {
                        if (e.getPlayer().equals(playerData.getPlayer()))
                            InventoryManager.CLASS_SELECT.newInventory(playerData).open();
                    }

                    @Override
                    public void whenClosed() {

                    }
                };
                new TemporaryListener(PlayerChangeClassEvent.getHandlerList()) {
                    @EventHandler
                    public void whenChoose(PlayerChangeClassEvent e) {
                        if (e.getPlayer().equals(playerData.getPlayer())) {
                            closeGUIListener.close();
                            close();
                        }

                    }

                    @Override
                    public void whenClosed() {

                    }
                };
            }
        }
    }

}
