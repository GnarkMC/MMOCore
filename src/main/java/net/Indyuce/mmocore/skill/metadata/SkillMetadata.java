package net.Indyuce.mmocore.skill.metadata;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skill.CasterMetadata;
import net.Indyuce.mmocore.skill.Skill;
import net.Indyuce.mmocore.skill.Skill.SkillInfo;

import java.util.Objects;

public class SkillMetadata {
    private final SkillInfo skill;
    private final int level;
    private final double mana, cooldown, stamina;

    private CancelReason cancelReason;

    public SkillMetadata(CasterMetadata caster, SkillInfo skill) {
        this.skill = skill;

        PlayerData data = caster.getPlayerData();

        level = data.getSkillLevel(skill.getSkill());
        cooldown = (skill.getSkill().hasModifier("cooldown") ? data.getSkillData().getCooldown(skill) : 0);
        mana = (skill.getSkill().hasModifier("mana") ? skill.getModifier("mana", level) : 0);
        stamina = (skill.getSkill().hasModifier("stamina") ? skill.getModifier("stamina", level) : 0);
        cancelReason = !data.hasSkillUnlocked(skill) ? CancelReason.LOCKED
                : cooldown > 0 ? CancelReason.COOLDOWN
                : mana > data.getMana() ? CancelReason.MANA
                : stamina > data.getStamina() ? CancelReason.STAMINA
                : !data.isOnline() ? CancelReason.OTHER
                : !MythicLib.plugin.getFlags().isFlagAllowed(data.getPlayer(), CustomFlag.MMO_ABILITIES) ? CancelReason.FLAG
                : null;
    }

    public SkillMetadata(PlayerData data, SkillInfo skill, CancelReason reason) {
        this.skill = skill;
        this.cancelReason = reason;

        level = data.getSkillLevel(skill.getSkill());
        cooldown = skill.getSkill().hasModifier("cooldown") ? data.getSkillData().getCooldown(skill) : 0;
        mana = skill.getSkill().hasModifier("mana") ? skill.getModifier("mana", level) : 0;
        stamina = (skill.getSkill().hasModifier("stamina") ? skill.getModifier("stamina", level) : 0);
    }

    public Skill getSkill() {
        return skill.getSkill();
    }

    public SkillInfo getInfo() {
        return skill;
    }

    public int getLevel() {
        return level;
    }

    public double getStaminaCost() {
        return stamina;
    }

    public double getManaCost() {
        return mana;
    }

    public double getCooldown() {
        return cooldown;
    }

    public boolean isSuccessful() {
        return cancelReason == null;
    }

    public CancelReason getCancelReason() {
        return cancelReason;
    }

    public void abort() {
        abort(CancelReason.OTHER);
    }

    public void abort(CancelReason reason) {
        cancelReason = Objects.requireNonNull(reason, "Reason cannot be null");
    }

    public double getModifier(String modifier) {
        return skill.getModifier(modifier, level);
    }

    public enum CancelReason {

        /**
         * Flag plugin like WorldGuard or any other
         */
        FLAG,

        /**
         * Not enough stamina
         */
        MANA,

        /**
         * Not enough mana
         */
        STAMINA,

        /**
         * Skill still on cooldown
         */
        COOLDOWN,

        /**
         * Skill is locked
         */
        LOCKED,

        /**
         * Anything else
         */
        OTHER;
    }
}
