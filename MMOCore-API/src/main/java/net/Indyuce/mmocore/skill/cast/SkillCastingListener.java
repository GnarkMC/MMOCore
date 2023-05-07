package net.Indyuce.mmocore.skill.cast;


import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * @apiNote Purely for API
 */
public interface SkillCastingListener extends Listener {

    @NotNull
    public SkillCastingMode getCastingMode();

    @NotNull
    public SkillCastingInstance newInstance(@NotNull PlayerData player);
}
