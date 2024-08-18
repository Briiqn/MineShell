package dev.briiqn.mineShell;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

public class ChatListener implements Listener {
    private final MineShell plugin;

    public ChatListener(MineShell plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (message.startsWith("!shell ")) {
            event.setCancelled(true);
            String command = message.substring(7).trim();
            if (plugin.getAuthManager().isAuthenticated(player)) {
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        plugin.getShellCommandExecutor().executeShellCommand(player, command));
            } else {
                player.sendMessage("You are not authenticated to use shell commands.");
            }
        }
    }
}
