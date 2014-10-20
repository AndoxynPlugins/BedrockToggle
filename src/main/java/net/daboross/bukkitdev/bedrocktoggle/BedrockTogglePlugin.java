package net.daboross.bukkitdev.bedrocktoggle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BedrockTogglePlugin extends JavaPlugin implements Listener {

    private final HashSet<UUID> enabledFor = new HashSet<UUID>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        for (String uuidString : getConfig().getStringList("enabled-for")) {
            enabledFor.add(UUID.fromString(uuidString));
        }
    }

    @Override
    public void onDisable() {
        List<String> saveList = new ArrayList<String>(enabledFor.size());
        for (UUID uuid : enabledFor) {
            saveList.add(uuid.toString());
        }
        getConfig().set("enabled-for", saveList);
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bedrock")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.DARK_RED + "Only players may use this command.");
                return true;
            }
            UUID uuid = ((Player) sender).getUniqueId();
            if (args.length < 1) {
                if (enabledFor.contains(uuid)) {
                    enabledFor.remove(uuid);
                    sender.sendMessage(ChatColor.GREEN + "Bedrock now unbreakable.");
                } else {
                    enabledFor.add(uuid);
                    sender.sendMessage(ChatColor.GREEN + "Bedrock now breakable.");
                }
                return true;
            } else if (args.length > 1) {
                sender.sendMessage(ChatColor.DARK_RED + "Too many arguments.");
                return true;
            }
            if (args[0].equalsIgnoreCase("off")) {
                enabledFor.remove(uuid);
                sender.sendMessage(ChatColor.GREEN + "Bedrock now unbreakable.");
            } else if (args[0].equalsIgnoreCase("on")) {
                enabledFor.add(uuid);
                sender.sendMessage(ChatColor.GREEN + "Bedrock now breakable.");
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Unknown argument: '" + ChatColor.RED + args[0] + ChatColor.DARK_RED + "'.");
            }
            return true;
        }
        sender.sendMessage("BedrockToggle doesn't know about the command /" + cmd.getName());
        return true;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent evt) {
        if (evt.getBlock().getType() == Material.BEDROCK
                && !enabledFor.contains(evt.getPlayer().getUniqueId())) {
            evt.setCancelled(true);
        }
    }
}
