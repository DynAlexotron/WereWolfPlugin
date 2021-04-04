package io.github.ph1lou.werewolfplugin.commands.admin.ingame;

import io.github.ph1lou.werewolfapi.ICommands;
import io.github.ph1lou.werewolfapi.IStuffManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.events.UpdateStuffEvent;
import io.github.ph1lou.werewolfplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CommandStuffRole implements ICommands {

    private final Main main;

    public CommandStuffRole(Main main) {
        this.main = main;
    }

    @Override
    public void execute(Player player, String[] args) {

        WereWolfAPI game = main.getWereWolfAPI();
        IStuffManager stuffManager = game.getStuffs();
        UUID uuid = player.getUniqueId();

        if (!stuffManager.getStuffRoles().containsKey(args[0])) {
            player.sendMessage(game.translate("werewolf.check.invalid_key"));
            return;
        }

        stuffManager.getStuffRoles().get(args[0]).clear();
        for (ItemStack i : player.getInventory().getContents()) {
            stuffManager.getStuffRoles().get(args[0]).add(i);
        }
        player.sendMessage(game.translate("werewolf.commands.admin.loot_role.perform", game.translate(args[0])));

        Inventory inventory;
        if (stuffManager.getTempStuff().containsKey(uuid)) {
            inventory = stuffManager.getTempStuff().get(uuid);
            stuffManager.getTempStuff().remove(uuid);
        } else inventory = Bukkit.createInventory(player, 45);

        for (int j = 0; j < 40; j++) {
            player.getInventory().setItem(j, inventory.getItem(j));
        }

        player.setGameMode(GameMode.ADVENTURE);

        Bukkit.getPluginManager().callEvent(new UpdateStuffEvent());
    }
}
