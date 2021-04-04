package io.github.ph1lou.werewolfplugin.roles.neutrals;

import io.github.ph1lou.werewolfapi.DescriptionBuilder;
import io.github.ph1lou.werewolfapi.GetWereWolfAPI;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.rolesattributs.RoleNeutral;
import org.jetbrains.annotations.NotNull;

public class WhiteWereWolf extends RoleNeutral {

    public WhiteWereWolf(GetWereWolfAPI main, IPlayerWW playerWW, String key) {
        super(main, playerWW, key);
    }


    @Override
    public void recoverPower() {
        getPlayerWW().addPlayerMaxHealth(10);
    }


    @Override
    public @NotNull String getDescription() {

        return new DescriptionBuilder(game, this)
                .setDescription(() -> game.translate("werewolf.role.white_werewolf.description"))
                .setPower(() -> game.translate("werewolf.role.white_werewolf.heart"))
                .build();
    }


    @Override
    public boolean isWereWolf() {
        return true;
    }

    @Override
    public boolean isNeutral(){
        return true;
    }
}
