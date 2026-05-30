package de.SurvivalChallengesPlugin.challengesmenu.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Version implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        TextComponent message = new TextComponent("");

        TextComponent prefix = new TextComponent("[SurvivalChallengesPlugin] ");
        prefix.setColor(net.md_5.bungee.api.ChatColor.GOLD);

        TextComponent infoComponent = new TextComponent("This Plugin by KeineAhnung_Leo runs on version SurvivalChallengesPlugin-1.1.1. Additionally, this Plugin is open source - visit the GitHub repository ");
        infoComponent.setColor(net.md_5.bungee.api.ChatColor.GRAY);

        TextComponent clickComponent = new TextComponent("here.");
        clickComponent.setColor(ChatColor.DARK_GREEN);
        clickComponent.setColor(ChatColor.UNDERLINE);

        clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/KeineAhnungLeo/Survival-Challenges-Plugin"));
        clickComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GRAY + "GitHub").create()));

        message.addExtra(prefix);
        message.addExtra(infoComponent);
        message.addExtra(clickComponent);
        commandSender.spigot().sendMessage(message);
        return true;
    }
}
