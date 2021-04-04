package io.github.ph1lou.werewolfplugin.listeners.random_events;

import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.ListenerManager;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StartEvent;
import io.github.ph1lou.werewolfapi.events.game.game_cycle.StopEvent;
import io.github.ph1lou.werewolfapi.events.game.timers.RepartitionEvent;
import io.github.ph1lou.werewolfapi.events.random_events.ExposedEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IDisplay;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Exposed extends ListenerManager {

    private IPlayerWW temp = null;

    public Exposed(GetWereWolfAPI main) {
        super(main);
    }

    @EventHandler
    public void onRepartition(RepartitionEvent event) {
        WereWolfAPI game = main.getWereWolfAPI();

        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
            if (game.isState(StateGame.GAME)) {
                if (isRegister()) {
                    IPlayerWW playerWW = announce();

                    if (temp == null && playerWW != null) {
                        temp = playerWW;
                        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin) main, () -> {
                            if (game.isState(StateGame.GAME)) {
                                if (isRegister()) {
                                    announce();
                                    temp = null;
                                    register(false);
                                }
                            }
                        }, 35 * 60 * 20);
                    }

                }
            }
        }, (long) (20 * 60 * 40 + game.getRandom().nextDouble() * 15 * 60 * 20));
    }

    @Nullable
    private IPlayerWW announce() {

        WereWolfAPI game = main.getWereWolfAPI();

        List<IPlayerWW> IPlayerWWList = game.getPlayerWW().stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .filter(playerWW -> !playerWW.equals(temp))
                .collect(Collectors.toList());

        if (IPlayerWWList.isEmpty()) return null;

        IPlayerWW playerWW = IPlayerWWList.get((int) Math.floor(game.getRandom().nextDouble() * IPlayerWWList.size()));

        List<IRole> role1List = game.getPlayerWW().stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> !playerWW.equals(playerWW1))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.isCamp(playerWW.getRole().getCamp()))
                .collect(Collectors.toList());

        if (role1List.isEmpty()) return null;

        IRole role1 = role1List.get((int) Math.floor(game.getRandom().nextDouble() * role1List.size()));

        List<IRole> role2List = game.getPlayerWW().stream()
                .filter(playerWW1 -> playerWW1.isState(StatePlayer.ALIVE))
                .filter(playerWW1 -> !playerWW.equals(playerWW1))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(role1))
                .collect(Collectors.toList());

        if (role2List.isEmpty()) return null;

        IRole role2 = role2List.get((int) Math.floor(game.getRandom().nextDouble() * role2List.size()));

        List<String> roles = new ArrayList<>(Arrays.asList(role1.getKey(),
                role2.getKey(),
                playerWW.getRole() instanceof IDisplay ?
                        ((IDisplay) playerWW.getRole()).getDisplayRole() :
                        playerWW.getRole().getKey()));

        Collections.shuffle(roles);

        ExposedEvent exposedEvent = new ExposedEvent(playerWW,
                new ArrayList<>(Arrays.asList(role1.getPlayerWW(),
                        role2.getPlayerWW(),
                        playerWW)));

        Bukkit.getPluginManager().callEvent(exposedEvent);

        if (!exposedEvent.isCancelled()) {
            roles = roles.stream().map(game::translate).collect(Collectors.toList());
            Bukkit.broadcastMessage(game.translate("werewolf.random_events.exposed.message",
                    playerWW.getName(), roles.get(0), roles.get(1), roles.get(2)));
        }

        return playerWW;
    }

    @EventHandler
    public void onGameStop(StopEvent event) {
        temp = null;
    }

    @EventHandler
    public void onGameStart(StartEvent event) {
        temp = null;
    }

}
