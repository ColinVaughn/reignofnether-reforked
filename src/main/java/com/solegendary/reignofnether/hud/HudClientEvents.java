package com.solegendary.reignofnether.hud;

import com.solegendary.reignofnether.orthoview.OrthoviewClientEvents;
import com.solegendary.reignofnether.units.Unit;
import com.solegendary.reignofnether.units.UnitClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class HudClientEvents {

    private static final Minecraft MC = Minecraft.getInstance();

    private static ArrayList<Button> unitButtons = new ArrayList<>();
    private static final ArrayList<Button> actionButtons = new ArrayList<>(Arrays.asList(
            ActionButtons.attack,
            ActionButtons.stop,
            ActionButtons.hold,
            ActionButtons.move
    ));
    // unit type that is selected in the list of unit icons
    private static String hudSelectedUnitType = null;

    // if we are rendering > this amount, then just render an empty icon with +N for the remaining units
    private static final int maxUnitButtons = 8;

    // eg. entity.reignofnether.zombie_unit -> zombie
    private static String getSimpleUnitName(LivingEntity unit) {
        return unit.getName().getString()
            .replace(" ","")
            .replace("entity.reignofnether.","")
            .replace("_unit","");
    }

    @SubscribeEvent
    public static void onDrawScreen(ScreenEvent.DrawScreenEvent evt) {
        String screenName = evt.getScreen().getTitle().getString();
        if (!OrthoviewClientEvents.isEnabled() || !screenName.equals("topdowngui_container"))
            return;
        if (MC.level == null)
            return;

        int mouseX = evt.getMouseX();
        int mouseY = evt.getMouseY();

        ArrayList<LivingEntity> units = new ArrayList<>();
        unitButtons = new ArrayList<>();

        for (int id: UnitClientEvents.getSelectedUnitIds()) {
            Entity entity = MC.level.getEntity(id);
            if (entity instanceof LivingEntity)
                units.add((LivingEntity) entity);
        }

        // sort and hudSelect the first unit type in the list
        units.sort(Comparator.comparing(a -> a.getName().getString()));

        if (units.size() <= 0)
            hudSelectedUnitType = null;
        else if (hudSelectedUnitType == null)
            hudSelectedUnitType = getSimpleUnitName(units.get(0));

        // create all of the unit buttons for this frame
        int screenWidth = MC.getWindow().getGuiScaledWidth();
        int screenHeight = MC.getWindow().getGuiScaledHeight();

        int iconSize = 14;
        int iconFrameSize = Button.iconFrameSize;

        for (LivingEntity unit : units) {

            if (unitButtons.size() < maxUnitButtons) {
                // mob head icon
                String unitName = getSimpleUnitName(unit);

                unitButtons.add(new Button(
                        unitName,
                        iconSize,
                        "textures/mobheads/" + unitName + ".png",
                        unit,
                        () -> hudSelectedUnitType.equals(unitName),
                        () -> {
                            // click to select this unit type as a group
                            if (hudSelectedUnitType.equals(unitName)) {
                                UnitClientEvents.setSelectedUnitIds(new ArrayList<>());
                                UnitClientEvents.addSelectedUnitId(unit.getId());
                            } else { // select this one specific unit
                                hudSelectedUnitType = unitName;
                            }
                        }
                ));
            }
        }

        // ---------------------------
        // Unit icons using mob heads
        // ---------------------------
        int numUnitButtons = Math.min(units.size(), maxUnitButtons);
        int blitX = (screenWidth / 2) - (numUnitButtons * iconFrameSize / 2);
        int blitY = screenHeight - iconFrameSize;

        for (Button button : unitButtons) {
            button.render(evt.getPoseStack(), blitX, blitY, mouseX, mouseY);
            button.renderHealthBar(evt.getPoseStack());
            blitX += iconFrameSize;
        }

        // -------------------------------------------------------
        // Unit action icons (attack, stop, move, abilities etc.)
        // -------------------------------------------------------

        if (UnitClientEvents.getSelectedUnitIds().size() > 0) {
            blitX = 0;
            blitY = screenHeight - iconFrameSize;
            for (Button actionButton : actionButtons) {
                actionButton.render(evt.getPoseStack(), blitX, blitY, mouseX, mouseY);
                actionButton.checkPressed();
                blitX += iconFrameSize;
            }
            blitX = 0;
            blitY = screenHeight - (iconFrameSize * 2);
            for (LivingEntity unit : units) {
                if (getSimpleUnitName(unit).equals(hudSelectedUnitType)) {
                    for (AbilityButton ability : ((Unit) unit).getAbilities()) {
                        ability.render(evt.getPoseStack(), blitX, blitY, mouseX, mouseY);
                        ability.checkPressed();
                        blitX += iconFrameSize;
                    }
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMouseRelease(ScreenEvent.MouseReleasedEvent.Post evt) {
        int mouseX = (int) evt.getMouseX();
        int mouseY = (int) evt.getMouseY();

        ArrayList<Button> buttons = new ArrayList<>();
        buttons.addAll(actionButtons);
        buttons.addAll(unitButtons);

        for (Button button : buttons)
            button.checkClicked(mouseX, mouseY);
    }
}