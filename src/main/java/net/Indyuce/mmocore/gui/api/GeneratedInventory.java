package net.Indyuce.mmocore.gui.api;

import io.lumine.mythic.lib.MythicLib;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.gui.api.item.InventoryItem;
import net.Indyuce.mmocore.gui.api.item.TriggerItem;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class GeneratedInventory extends PluginInventory {
    private final EditableInventory editable;
    private final List<InventoryItem> loaded = new ArrayList<>();

    private Inventory open;

    public GeneratedInventory(PlayerData playerData, EditableInventory editable) {
        super(playerData);

        this.editable = editable;
    }

    public List<InventoryItem> getLoaded() {
        return loaded;
    }

    public EditableInventory getEditable() {
        return editable;
    }

    public InventoryItem getByFunction(String function) {
        for (InventoryItem item : loaded)
            if (item.getFunction().equals(function))
                return item;
        return null;
    }

    public InventoryItem getBySlot(int slot) {
        for (InventoryItem item : loaded)
            if (item.getSlots().contains(slot))
                return item;
        return null;
    }

    /**
     * This method must use an ordered collection because
     * of GUI items overriding possibilities. Hence the use
     * of an array list instead of a set
     */
    public void addLoaded(InventoryItem item) {
        loaded.add(0, item);
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, editable.getSlots(), MythicLib.plugin.getPlaceholderParser().parse(getPlayer(), calculateName()));

        for (InventoryItem item : editable.getItems())
            if (item.canDisplay(this))
                item.setDisplayed(inv, this);

        return inv;
    }

    public void open() {

        /*
         * Very important, in order to prevent ghost items, the loaded items map
         * must be cleared when the inventory is updated or open at least twice
         */
        loaded.clear();

        getPlayer().openInventory(open = getInventory());
    }

    /**
     * @deprecated Not a fan of that implementation.
     *         Better work with {@link InventoryItem#setDisplayed(Inventory, GeneratedInventory)}
     */
    @Deprecated
    public void dynamicallyUpdateItem(InventoryItem<?> item, int n, ItemStack placed, Consumer<ItemStack> update) {
        Bukkit.getScheduler().runTaskAsynchronously(MMOCore.plugin, () -> {
            update.accept(placed);
            open.setItem(item.getSlots().get(n), placed);
        });
    }

    public void whenClicked(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getClickedInventory() != null && event.getClickedInventory().equals(event.getInventory())) {
            InventoryItem item = getBySlot(event.getSlot());
            if (item == null)
                return;

            if (item instanceof TriggerItem)
                ((TriggerItem) item).getTriggers().forEach(trigger->trigger.apply(getPlayerData()));
            else
                whenClicked(event, item);
        }
    }

    public abstract String calculateName();

    public abstract void whenClicked(InventoryClickEvent event, InventoryItem item);
}
