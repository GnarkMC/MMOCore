package net.Indyuce.mmocore.gui.eco;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.gui.api.InventoryClickContext;
import net.Indyuce.mmocore.util.item.SimpleItemBuilder;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.gui.api.PluginInventory;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.SmartGive;

public class DepositMenu extends PluginInventory {
    private ItemStack depositItem;
    private int deposit;

    public DepositMenu(Player player) {
        super(player);
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 27, "Deposit");

        inv.setItem(26, depositItem = new SimpleItemBuilder("DEPOSIT_ITEM").addPlaceholders("worth", "0").build());

        new BukkitRunnable() {

            @Override
            public void run() {
                if (inv.getViewers().size() < 1) {
                    cancel();
                    return;
                }

                updateDeposit(inv);
            }
        }.runTaskTimer(MMOCore.plugin, 0, 20);
        return inv;
    }

    @Override
    public void whenClicked(InventoryClickContext context) {
        if (context.isClassic()) {

            // event.setCancelled(true);
            if (context.getItemStack() == null || context.getItemStack().getType() == Material.AIR)
                return;

            if (context.getItemStack().isSimilar(depositItem)) {
                context.setCancelled(true);

                updateDeposit(context.getInventory());
                if (deposit <= 0)
                    return;

                EconomyResponse response = MMOCore.plugin.economy.getEconomy().depositPlayer(player, deposit);
                if (!response.transactionSuccess())
                    return;

                ;
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                MMOCore.plugin.configManager.getSimpleMessage("deposit", "worth", "" + deposit).send(player);
                return;
            }

            int worth = NBTItem.get(context.getItemStack()).getInteger("RpgWorth");
            if (worth < 1) {
                context.setCancelled(true);
            }
        }

        // in deposit menu
        // if (event.getRawSlot() < 27) {
        // int empty = player.getInventory().firstEmpty();
        // if (empty < 0)
        // return;
        //
        // player.playSound(player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT,
        // 1, 2);
        // player.getInventory().addItem(event.getCurrentItem());
        // event.setCurrentItem(null);
        // updateDeposit(event.getInventory());
        // return;
        // }

        // in player inventory
        // int empty = event.getInventory().firstEmpty();
        // if (empty < 0)
        // return;
        //
        // player.playSound(player.getLocation(),
        // Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
        // event.getInventory().addItem(event.getCurrentItem());
        // event.setCurrentItem(null);
        // updateDeposit(event.getInventory());
        // return;
    }

    @Override
    public void whenClosed(InventoryCloseEvent event) {
        SmartGive smart = new SmartGive(player);
        for (int j = 0; j < 26; j++) {
            ItemStack item = event.getInventory().getItem(j);
            if (item != null)
                smart.give(item);
        }
    }

    private void updateDeposit(Inventory inv) {
        deposit = MMOCoreUtils.getWorth(inv.getContents());
        inv.setItem(26, depositItem = new SimpleItemBuilder("DEPOSIT_ITEM").addPlaceholders("worth", "" + deposit).build());
    }
}
