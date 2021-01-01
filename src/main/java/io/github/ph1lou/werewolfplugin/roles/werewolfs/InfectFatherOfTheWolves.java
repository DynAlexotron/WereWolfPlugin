package io.github.ph1lou.werewolfplugin.roles.werewolfs;


import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.PlayerWW;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.SecondDeathEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.AffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.Power;
import io.github.ph1lou.werewolfapi.rolesattributs.RolesWereWolf;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InfectFatherOfTheWolves extends RolesWereWolf implements AffectedPlayers, Power {

    private final List<PlayerWW> affectedPlayer = new ArrayList<>();
    private boolean power = true;

    public InfectFatherOfTheWolves(GetWereWolfAPI main,
                                   PlayerWW playerWW,
                                   String key) {
        super(main, playerWW, key);
    }

    @Override
    public void setPower(boolean power) {
        this.power = power;
    }

    @Override
    public boolean hasPower() {
        return (this.power);
    }

    @Override
    public void addAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.add(playerWW);
    }

    @Override
    public void removeAffectedPlayer(PlayerWW playerWW) {
        this.affectedPlayer.remove(playerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        this.affectedPlayer.clear();
    }

    @Override
    public List<PlayerWW> getAffectedPlayers() {
        return (this.affectedPlayer);
    }


    @Override
    public @NotNull String getDescription() {
        return super.getDescription() +
                game.translate("werewolf.description.description", game.translate("werewolf.role.infect_father_of_the_wolves.description")) +
                game.translate("werewolf.description.power", game.translate(power ? "werewolf.role.infect_father_of_the_wolves.power_available" : "werewolf.role.infect_father_of_the_wolves.power_not_available")) +
                game.translate("werewolf.description.item", game.translate("werewolf.role.infect_father_of_the_wolves.items"));
    }


    @Override
    public void recoverPower() {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSecondDeathEvent(SecondDeathEvent event) {

        if (event.isCancelled()) return;

        if (!hasPower()) return;

        PlayerWW playerWW = event.getPlayerWW();


        PlayerWW killerWW = playerWW.getLastKiller();


        if (killerWW == null) {
            return;
        }

        if (!killerWW.getRole().isWereWolf()) {
            return;
        }

        if (playerWW.equals(getPlayerWW())) {
            return;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        TextComponent infect_msg = new TextComponent(
                game.translate(
                        "werewolf.role.infect_father_of_the_wolves.infection_message",
                        playerWW.getName()));
        infect_msg.setClickEvent(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        String.format("/ww %s %s",
                                game.translate("werewolf.role.infect_father_of_the_wolves.command"),
                                playerWW.getUUID())));
        getPlayerWW().sendMessage(infect_msg);
    }
}
