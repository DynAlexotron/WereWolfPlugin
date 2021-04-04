package io.github.ph1lou.werewolfplugin.roles.villagers;


import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.enums.Day;
import io.github.ph1lou.werewolfapi.enums.StateGame;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.DayEvent;
import io.github.ph1lou.werewolfapi.events.DayWillComeEvent;
import io.github.ph1lou.werewolfapi.events.NightEvent;
import io.github.ph1lou.werewolfapi.events.UpdatePlayerNameTag;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.FinalDeathEvent;
import io.github.ph1lou.werewolfapi.events.game.life_cycle.ResurrectionEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.EnchantmentEvent;
import io.github.ph1lou.werewolfapi.events.game.utils.GoldenAppleParticleEvent;
import io.github.ph1lou.werewolfapi.events.roles.InvisibleEvent;
import io.github.ph1lou.werewolfapi.events.roles.StealEvent;
import io.github.ph1lou.werewolfapi.events.werewolf.WereWolfChatEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IInvisible;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleVillage;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.javatuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LittleGirl extends RoleVillage implements IInvisible {

    private boolean invisible = false;

    public LittleGirl(GetWereWolfAPI main, IPlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @EventHandler
    public void onNight(NightEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        getPlayerWW().sendMessageWithKey(
                "werewolf.role.little_girl.remove_armor");
    }

    @EventHandler
    public void onDay(DayEvent event) {

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }
        if (!isInvisible()) return;

        getPlayerWW().removePotionEffect(PotionEffectType.INVISIBILITY);

        setInvisible(false);
        Bukkit.getPluginManager().callEvent(
                new InvisibleEvent(getPlayerWW(),
                        false));
        getPlayerWW().sendMessageWithKey("werewolf.role.little_girl.visible");
    }

    @EventHandler
    public void onUpdateNameTag(UpdatePlayerNameTag event) {

        if (!event.getPlayerUUID().equals(getPlayerUUID())) return;

        if (event.isVisibility()) {
            event.setVisibility(!invisible);
        }
    }

    @EventHandler
    public void onGoldenAppleEat(GoldenAppleParticleEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (!isInvisible()) return;

        if (game.isDay(Day.DAY)) return;

        event.setCancelled(true);
    }


    @EventHandler
    public void onDayWillCome(DayWillComeEvent event) {


        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        getPlayerWW().sendMessageWithKey(
                "werewolf.role.little_girl.soon_to_be_day");
    }

    @Override
    public void second() {

        Player player = Bukkit.getPlayer(getPlayerUUID());

        if (player == null) {
            return;
        }

        if (!game.isDay(Day.NIGHT)) {
            return;
        }

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!isInvisible()) {
            return;
        }

        game.getPlayerWW()
                .stream()
                .filter(playerWW -> playerWW.isState(StatePlayer.ALIVE))
                .map(IPlayerWW::getRole)
                .filter(roles -> !roles.equals(this))
                .filter(roles -> roles instanceof IInvisible)
                .map(roles -> (IInvisible) roles)
                .filter(IInvisible::isInvisible)
                .map(IInvisible -> {
                    if (((IRole) IInvisible).isWereWolf()) {
                        return new Pair<>(Material.REDSTONE_BLOCK,
                                ((IRole) IInvisible).getPlayerUUID());
                    } else return new Pair<>(Material.LAPIS_BLOCK,
                            ((IRole) IInvisible).getPlayerUUID());
                })
                .map(objects -> new Pair<>(objects.getValue0(),
                        Bukkit.getPlayer(objects.getValue1())))
                .filter(objects -> objects.getValue1() != null)
                .map(objects -> new Pair<>(objects.getValue0(),
                        objects.getValue1().getLocation()))
                .forEach(objects -> player.playEffect(objects.getValue1(),
                        Effect.STEP_SOUND, objects.getValue0()));

    }


    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.little_girl.description"))
                .setItems(() -> game.translate("werewolf.role.little_girl.item"))
                .setEffects(() -> game.translate("werewolf.role.little_girl.effect"))
                .build();
    }


    @EventHandler
    public void onStealEvent(StealEvent event) {

        if (!event.getThiefWW().equals(getPlayerWW())) return;

        setInvisible(false);
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public boolean isInvisible() {
        return this.invisible;
    }

    @Override
    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    @Override
    public void recoverPotionEffect() {

        super.recoverPotionEffect();


        getPlayerWW().addPotionEffect(PotionEffectType.NIGHT_VISION);
    }

    @EventHandler
    public void onFinalDeath(FinalDeathEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        setInvisible(false);

        if(game.getConfig().getLimitKnockBack()==2) return;

        for (ItemStack i : getPlayerWW().getItemDeath()) {
            if (i != null) {
                i.removeEnchantment(Enchantment.KNOCKBACK);
            }
        }
    }

    @EventHandler
    public void onEnchantment(EnchantmentEvent event){

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        if (event.getEnchants().containsKey(Enchantment.KNOCKBACK)) {
            event.getFinalEnchants().put(Enchantment.KNOCKBACK,
                    Math.min(event.getEnchants().get(Enchantment.KNOCKBACK),
                            game.getConfig().getLimitKnockBack()));
        }
    }

    @EventHandler
    private void onCloseInvent(InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!uuid.equals(getPlayerUUID())) return;

        Inventory inventory = player.getInventory();

        if (!game.isState(StateGame.GAME)) return;
        if (!game.isDay(Day.NIGHT)) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (inventory.getItem(36) == null &&
                inventory.getItem(37) == null &&
                inventory.getItem(38) == null &&
                inventory.getItem(39) == null) {
            if (!isInvisible()) {
                player.sendMessage(game.translate("werewolf.role.little_girl.remove_armor_perform"));
                getPlayerWW().addPotionEffect(PotionEffectType.INVISIBILITY);
                if (getInfected()) {
                    getPlayerWW().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                }
                setInvisible(true);
                Bukkit.getPluginManager().callEvent(
                        new InvisibleEvent(getPlayerWW(), true));
            }
        } else if (isInvisible()) {
            player.sendMessage(game.translate(
                    "werewolf.role.little_girl.visible"));
            if (getInfected()) {
                player.addPotionEffect(new PotionEffect(
                        PotionEffectType.INCREASE_DAMAGE,
                        Integer.MAX_VALUE,
                        -1,
                        false,
                        false));
            }
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            this.setInvisible(false);
            Bukkit.getPluginManager().callEvent(
                    new InvisibleEvent(getPlayerWW(), false));
        }
    }

    @EventHandler
    public void onWWChatLittleGirl(WereWolfChatEvent event) {

        if (event.isCancelled()) return;

        if (!getPlayerWW().isState(StatePlayer.ALIVE)) return;

        if (isWereWolf()) { //pour éviter qu'elle ait le message en double
            return;
        }

        getPlayerWW().sendMessageWithKey("werewolf.commands.admin.ww_chat.prefix", event.getMessage());

    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onResurrection(ResurrectionEvent event) {

        if (!event.getPlayerWW().equals(getPlayerWW())) return;

        setInvisible(false);
    }

}
