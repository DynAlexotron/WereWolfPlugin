package io.github.ph1lou.pluginlg.commandlg.admin.ingame;

import io.github.ph1lou.pluginlg.MainLG;
import io.github.ph1lou.pluginlg.commandlg.Commands;
import io.github.ph1lou.pluginlg.game.GameManager;
import io.github.ph1lou.pluginlg.savelg.TextLG;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandConfig extends Commands {


    public CommandConfig(MainLG main) {
        super(main);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return;
        }

        GameManager game=null;
        Player player =(Player) sender;

        for(GameManager gameManager:main.listGames.values()){
            if(gameManager.getWorld().equals(player.getWorld())){
                game=gameManager;
                break;
            }
        }

        if(game==null){
            return;
        }

        TextLG text = game.text;

        if (!sender.hasPermission("adminLG.use") && !sender.hasPermission("adminLG.config.use") && !game.getHosts().contains(((Player) sender).getUniqueId())) {
            sender.sendMessage(text.getText(116));
            return;
        }

        game.optionlg.toolBar((Player) sender);
    }
}