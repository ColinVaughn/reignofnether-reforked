package com.solegendary.reignofnether.unit.goals;

import com.solegendary.reignofnether.building.Building;
import net.minecraft.world.entity.ai.goal.Goal;

public class BuildRepairGoal extends Goal {

    private Building targetBuilding;

    public Building getTarget() {
        return targetBuilding;
    }

    // TODO: move towards target
    public void setTarget(Building target) {
        this.targetBuilding = target;
    }

    // TODO: only count as building if in range of the target {
    public boolean isBuilding() {
        return this.targetBuilding != null;
    }

    public boolean canUse() {
        return false;
    }
}
