package net.Indyuce.mmocore.comp.region.pvpmode;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.session.handler.Handler;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import javax.annotation.Nullable;

public class PvPModeListener implements Listener {
    public PvPModeListener() {
        Validate.isTrue(registerHandler(PvPModeHandler.FACTORY), "Could not register WG handler for PvP mode");
        if (MMOCore.plugin.getConfig().getBoolean("pvp_mode.invulnerability.apply_to_pvp_flag"))
            Validate.isTrue(registerHandler(PvPFlagHandler.FACTORY), "Could not register WG handler for PvP");
    }

    private boolean registerHandler(Handler.Factory factory) {
        return WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(factory, null);
    }

    /**
     * Runs after MythicLib interaction checks. This listener
     * takes care of PVP inside of PvP-mode regions.
     */
    @EventHandler(ignoreCancelled = true)
    public void a(EntityDamageByEntityEvent event) {
        if (!UtilityMethods.isRealPlayer(event.getEntity()))
            return;

        final @Nullable Player source = UtilityMethods.getPlayerDamager(event);
        if (source == null)
            return;

        // Check for target's invulnerability BEFORE pvp-mode flag because it can also
        // happen when the option pvp_mode.invulnerability.apply_to_pvp_flag is on
        final Player target = (Player) event.getEntity();
        final PlayerData targetData = PlayerData.get(target);
        if (targetData.getCombat().isInvulnerable()) {
            final long left = targetData.getCombat().getInvulnerableTill() - System.currentTimeMillis();
            MMOCore.plugin.configManager.getSimpleMessage("pvp-mode.cannot-hit.invulnerable-target",
                    "left", MythicLib.plugin.getMMOConfig().decimal.format(left / 1000d)).send(source);
            event.setCancelled(true);
            return;
        }

        // If attacker is still invulnerable and cannot deal damage
        final PlayerData sourceData = PlayerData.get(source);
        if (!MMOCore.plugin.configManager.pvpModeInvulnerabilityCanDamage && sourceData.getCombat().isInvulnerable()) {
            final long left = sourceData.getCombat().getInvulnerableTill() - System.currentTimeMillis();
            MMOCore.plugin.configManager.getSimpleMessage("pvp-mode.cannot-hit.invulnerable-self",
                    "left", MythicLib.plugin.getMMOConfig().decimal.format(left / 1000d)).send(source);
            event.setCancelled(true);
            return;
        }

        // Checks for PvP mode on target location
        if (!MythicLib.plugin.getFlags().isFlagAllowed(target.getLocation(), CustomFlag.PVP_MODE))
            return;

        // Defender has not enabled PvP mode
        if (!targetData.getCombat().isInPvpMode()) {
            event.setCancelled(true);
            MMOCore.plugin.configManager.getSimpleMessage("pvp-mode.cannot-hit.pvp-mode-disabled-target").send(source);
        }

        // Attacker has not enabled PvP mode
        else if (!sourceData.getCombat().isInPvpMode()) {
            event.setCancelled(true);
            MMOCore.plugin.configManager.getSimpleMessage("pvp-mode.cannot-hit.pvp-mode-disabled-self").send(source);
        }
    }
}
