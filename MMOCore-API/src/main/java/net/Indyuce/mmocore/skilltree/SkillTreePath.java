package net.Indyuce.mmocore.skilltree;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.gui.skilltree.display.PathStatus;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;

public record SkillTreePath(SkillTree tree, IntegerCoordinates coordinates, SkillTreeNode from, SkillTreeNode to) {

    public PathStatus getStatus(PlayerData playerData) {
        NodeStatus fromStatus = playerData.getNodeStatus(from);
        NodeStatus toStatus = playerData.getNodeStatus(to);
        if (fromStatus == NodeStatus.UNLOCKED && toStatus == NodeStatus.UNLOCKED)
            return PathStatus.UNLOCKED;
        if ((fromStatus == NodeStatus.UNLOCKABLE && toStatus == NodeStatus.LOCKED) || (fromStatus == NodeStatus.LOCKED && toStatus == NodeStatus.UNLOCKABLE))
            return PathStatus.UNLOCKABLE;
        if (fromStatus == NodeStatus.FULLY_LOCKED || toStatus == NodeStatus.FULLY_LOCKED)
            return PathStatus.FULLY_LOCKED;
        return PathStatus.LOCKED;
    }


}
