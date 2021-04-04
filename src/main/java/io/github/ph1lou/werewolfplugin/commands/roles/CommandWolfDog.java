package io.github.ph1lou.werewolfplugin.commands.roles;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.UpdateNameTagEvent;
import io.github.ph1lou.werewolfapi.events.roles.wolf_dog.WolfDogChooseWereWolfForm;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.ITransformed;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandWolfDog implements ICommands {


    private final Main main;

    public CommandWolfDog(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid);

        if (playerWW == null) return;

        IRole wolfDog = playerWW.getRole();
        ((IPower) wolfDog).setPower(false);
        WolfDogChooseWereWolfForm event = new WolfDogChooseWereWolfForm(playerWW);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            playerWW.sendMessageWithKey("werewolf.check.cancel");
            return;
        }

        ((ITransformed) wolfDog).setTransformed(true);

        playerWW.sendMessageWithKey("werewolf.role.wolf_dog.perform");

        Bukkit.getPluginManager().callEvent(new UpdateNameTagEvent(player));

        game.checkVictory();
    }
}
