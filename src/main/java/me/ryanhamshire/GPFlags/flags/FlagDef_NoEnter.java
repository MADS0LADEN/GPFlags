package me.ryanhamshire.GPFlags.flags;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GPFlags.TextMode;
import me.ryanhamshire.GPFlags.util.Util;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class FlagDef_NoEnter extends PlayerMovementFlagDefinition {

    public FlagDef_NoEnter(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }

    @Override
    public void onFlagSet(Claim claim, String string) {
        World world = claim.getLesserBoundaryCorner().getWorld();
        for (Player p : world.getPlayers()) {
            if (claim.contains(Util.getInBoundsLocation(p), false, false)) {
                if (Util.canAccess(claim, p) && !p.hasPermission("gpflags.bypass.noenter")) {
                    GriefPrevention.instance.ejectPlayer(p);
                }
            }
        }
    }

    @Override
    public boolean allowMovement(Player player, Location lastLocation, Location to, Claim claimFrom, Claim claimTo) {
        if (player.hasPermission("gpflags.bypass.noenter")) return true;

        Flag flag = this.getFlagInstanceAtLocation(to, player);
        if (flag == null) return true;

        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(to, false, playerData.lastClaim);
        if (Util.canAccess(claim, player)) return true;

        Util.sendClaimMessage(player, TextMode.Err, Messages.NoEnterMessage);
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Flag flag = this.getFlagInstanceAtLocation(player.getLocation(), player);
        if (flag == null) return;
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, playerData.lastClaim);
        if (Util.canAccess(claim, player)) return;
        Util.sendClaimMessage(player, TextMode.Err, Messages.NoEnterMessage);
        GriefPrevention.instance.ejectPlayer(player);
    }

    @Override
    public String getName() {
        return "NoEnter";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnabledNoEnter, parameters);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisabledNoEnter);
    }

}
