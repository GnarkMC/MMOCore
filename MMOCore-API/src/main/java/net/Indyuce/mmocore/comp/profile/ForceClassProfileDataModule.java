package net.Indyuce.mmocore.comp.profile;

import fr.phoenixdevt.profile.ProfileDataModule;
import fr.phoenixdevt.profile.ProfileProvider;
import fr.phoenixdevt.profile.event.ProfileCreateEvent;
import fr.phoenixdevt.profile.event.ProfileRemoveEvent;
import fr.phoenixdevt.profile.event.ProfileSelectEvent;
import fr.phoenixdevt.profile.event.ProfileUnloadEvent;
import fr.phoenixdevt.profile.placeholder.PlaceholderRequest;
import io.lumine.mythic.lib.api.event.SynchronizedDataLoadEvent;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.manager.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ForceClassProfileDataModule implements ProfileDataModule, Listener {
    public ForceClassProfileDataModule() {
        final ProfileProvider<?> provider = Bukkit.getServicesManager().getRegistration(ProfileProvider.class).getProvider();
        provider.registerModule(this);
    }

    @Override
    public JavaPlugin getOwningPlugin() {
        return MMOCore.plugin;
    }

    @Override
    public boolean hasPlaceholders() {
        return false;
    }

    @Override
    public String getIdentifier() {
        return "mmocore_force_class";
    }

    @Override
    public void processPlaceholderRequest(PlaceholderRequest placeholderRequest) {
        throw new RuntimeException("Not supported");
    }

    /**
     * Force class before profile creation
     */
    @EventHandler
    public void onProfileCreate(ProfileCreateEvent event) {
        final PlayerData playerData = PlayerData.get(event.getPlayerData().getUniqueId());
        InventoryManager.CLASS_SELECT.newInventory(playerData, () -> event.validate(this)).open();
    }

    /**
     * Force class before profile selection once MMOCore loaded its data
     */
    @EventHandler
    public void onDataLoad(SynchronizedDataLoadEvent event) {
        if (event.getManager().getOwningPlugin().equals(MMOCore.plugin)) {
            final PlayerData playerData = (PlayerData) event.getHolder();
            final ProfileSelectEvent event1 = (ProfileSelectEvent) event.getProfileEvent();

            // Validate if necessary
            if (playerData.getProfess().equals(MMOCore.plugin.classManager.getDefaultClass()))
                InventoryManager.CLASS_SELECT.newInventory(playerData, () -> event1.validate(this)).open();
            else event1.validate(this);
        }
    }

    @EventHandler
    public void onProfileRemove(ProfileRemoveEvent event) {
        event.validate(this);
    }

    @EventHandler
    public void onProfileUnload(ProfileUnloadEvent event) {
        event.validate(this);
    }
}
