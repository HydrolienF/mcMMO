package com.gmail.nossr50.runnables.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.StringUtils;

public class MctopCommandAsyncTask implements Runnable {

    private CommandSender sender;
    private String query;
    private int page;

    public MctopCommandAsyncTask(int page, String query, CommandSender sender) {
        this.page = page;
        this.query = query;
        this.sender = sender;
    }

    @Override
    public void run() {
        String tablePrefix = Config.getInstance().getMySQLTablePrefix();
        final HashMap<Integer, ArrayList<String>> userslist = DatabaseManager.read("SELECT " + query + ", user, NOW() FROM " + tablePrefix + "users JOIN " + tablePrefix + "skills ON (user_id = id) WHERE " + query + " > 0 ORDER BY " + query + " DESC, user LIMIT " + ((page * 10) - 10) + ",10");
        mcMMO.p.getServer().getScheduler().runTaskLater(mcMMO.p, new Runnable() {
            @Override
            public void run() {
                if (query.equalsIgnoreCase("taming+mining+woodcutting+repair+unarmed+herbalism+excavation+archery+swords+axes+acrobatics+fishing")) {
                    sender.sendMessage(LocaleLoader.getString("Commands.PowerLevel.Leaderboard"));
                }
                else {
                    sender.sendMessage(LocaleLoader.getString("Commands.Skill.Leaderboard", StringUtils.getCapitalized(query)));
                }

                int place = (page * 10) - 9;
                for (int i = 1; i <= 10; i++) {
                    if (userslist.get(i) == null) {
                        break;
                    }

                    // Format: 1. Playername - skill value
                    sender.sendMessage(place + ". " + ChatColor.GREEN + userslist.get(i).get(1) + " - " + ChatColor.WHITE + userslist.get(i).get(0));
                    place++;
                }

                sender.sendMessage(LocaleLoader.getString("Commands.mctop.Tip"));
            }
        }, 1L);
    }

}
