/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.skywars.events.listeners;

import net.daboross.bukkitdev.skywars.api.SkyWars;
import net.daboross.bukkitdev.skywars.api.kits.SkyKit;
import net.daboross.bukkitdev.skywars.api.players.SkyPlayer;
import net.daboross.bukkitdev.skywars.api.translations.SkyTrans;
import net.daboross.bukkitdev.skywars.api.translations.TransKey;
import net.daboross.bukkitdev.skywars.events.events.GameStartInfo;
import org.bukkit.entity.Player;

public class KitApplyListener {

    private final SkyWars plugin;

    public KitApplyListener(final SkyWars plugin) {
        this.plugin = plugin;
    }

    public void onGameStart(GameStartInfo info) {
        for (Player p : info.getPlayers()) {
            SkyPlayer skyPlayer = plugin.getPlayers().getPlayer(p);
            SkyKit kit = skyPlayer.getSelectedKit();
            if (kit != null) {
                String permission = kit.getPermission();
                if (permission != null && !p.hasPermission(permission)) {
                    p.sendMessage(SkyTrans.get(TransKey.KITS_NO_PERMISSION, kit.getName()));
                    skyPlayer.setSelectedKit(null);
                    continue;
                }
                int cost = kit.getCost();
                if (cost == 0) {
                    p.sendMessage(SkyTrans.get(TransKey.KITS_APPLIED_KIT, kit.getName()));
                    kit.applyTo(p);
                } else if (plugin.getEconomyHook().canAfford(p, cost)) {
                    p.sendMessage(SkyTrans.get(TransKey.KITS_APPLIED_KIT_WITH_COST, kit.getName(), kit.getCost()));
                    if (plugin.getEconomyHook().charge(p, cost)) {
                        kit.applyTo(p);
                    } else {
                        skyPlayer.setSelectedKit(null);
                        p.sendMessage(SkyTrans.get(TransKey.KITS_NOT_ENOUGH_MONEY, kit.getName()));
                    }
                } else {
                    skyPlayer.setSelectedKit(null);
                    p.sendMessage(SkyTrans.get(TransKey.KITS_NOT_ENOUGH_MONEY, kit.getName()));
                }
            }
        }
    }
}
