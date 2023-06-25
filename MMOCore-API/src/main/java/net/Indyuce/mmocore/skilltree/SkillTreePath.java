package net.Indyuce.mmocore.skilltree;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.gui.skilltree.display.PathStatus;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;

public record SkillTreePath(SkillTree tree, IntegerCoordinates coordinates, SkillTreeNode from, SkillTreeNode to) {

    public PathStatus getStatus(PlayerData playerData) {
        SkillTreeStatus fromStatus = playerData.getNodeStatus(from);
        SkillTreeStatus toStatus = playerData.getNodeStatus(to);
        if (fromStatus == SkillTreeStatus.UNLOCKED && toStatus == SkillTreeStatus.UNLOCKED)
            return PathStatus.UNLOCKED;
        if ((fromStatus == SkillTreeStatus.UNLOCKABLE && toStatus == SkillTreeStatus.LOCKED) || (fromStatus == SkillTreeStatus.LOCKED && toStatus == SkillTreeStatus.UNLOCKABLE))
            return PathStatus.UNLOCKABLE;
        if (fromStatus == SkillTreeStatus.FULLY_LOCKED || toStatus == SkillTreeStatus.FULLY_LOCKED)
            return PathStatus.FULLY_LOCKED;
        return PathStatus.LOCKED;
    }


}
