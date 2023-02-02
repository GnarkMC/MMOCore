package net.Indyuce.mmocore.player;

import io.lumine.mythic.lib.player.skill.PassiveSkill;
import net.Indyuce.mmocore.api.player.profess.SavedClassInformation;
import net.Indyuce.mmocore.skill.ClassSkill;
import net.Indyuce.mmocore.skilltree.SkillTreeNode;

import java.util.List;
import java.util.Map;

/**
 * All the class-specific information i.e information being saved
 * in {@link SavedClassInformation} when a player changes its current
 * class.
 */
public interface ClassDataContainer {

    int getLevel();

    double getExperience();

    int getSkillPoints();

    int getAttributePoints();

    int getAttributeReallocationPoints();

    int getSkillReallocationPoints();

    int getSkillTreeReallocationPoints();

    Map<String, Integer> mapAttributeLevels();

    Map<String, Integer> mapSkillLevels();

    Map<String, Integer> mapSkillTreePoints();

    List<ClassSkill> getBoundSkills();

    List<PassiveSkill> getBoundPassiveSkills();

    Map<SkillTreeNode, Integer> getNodeLevels();

    Map<String, Integer> getNodeTimesClaimed();
}