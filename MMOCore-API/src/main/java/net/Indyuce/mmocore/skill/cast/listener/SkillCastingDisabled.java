package net.Indyuce.mmocore.skill.cast.listener;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.cast.SkillCastingInstance;
import net.Indyuce.mmocore.skill.cast.SkillCastingListener;
import net.Indyuce.mmocore.skill.cast.SkillCastingMode;
import org.jetbrains.annotations.NotNull;

public class SkillCastingDisabled implements SkillCastingListener {

    @Override
    public SkillCastingInstance newInstance(@NotNull PlayerData player) {
        throw new RuntimeException("Skill casting is disabled");
    }

    @Override
    public SkillCastingMode getCastingMode() {
        return SkillCastingMode.NONE;
    }
}
