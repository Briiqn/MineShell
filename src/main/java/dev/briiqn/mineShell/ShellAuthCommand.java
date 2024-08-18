package dev.briiqn.mineShell;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShellAuthCommand implements CommandExecutor {
    private final MineShell plugin;

    public ShellAuthCommand(MineShell plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("Usage: /shellauth <password>");
            return true;
        }

        String password = args[0];
        plugin.getAuthManager().authenticate(player, password);
        return true;
    }
}
