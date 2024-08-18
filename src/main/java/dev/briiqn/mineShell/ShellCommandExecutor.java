package dev.briiqn.mineShell;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class ShellCommandExecutor {
    private final MineShell plugin;

    public ShellCommandExecutor(MineShell plugin) {
        this.plugin = plugin;
    }

    public void executeShellCommand(Player player, String command) {
        if (!isAllowedCommand(command)) {
            player.sendMessage("This command is not allowed.");
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    long startTime = System.currentTimeMillis();
                    long timeout = 30000; // 30 seconds timeout

                    while ((System.currentTimeMillis() - startTime) < timeout) {
                        if (reader.ready()) {
                            line = reader.readLine();
                            if (line != null) {
                                final String outputLine = line;
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        player.sendMessage(outputLine);
                                    }
                                }.runTask(plugin);
                            }
                        }
                        Thread.sleep(100);
                    }

                    if (process.isAlive()) {
                        process.destroyForcibly();
                        player.sendMessage("Command execution timed out.");
                    }
                } catch (Exception e) {
                    player.sendMessage("Error executing command: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private boolean isAllowedCommand(String command) {
        java.util.List<String> allowedCommands = plugin.getConfig().getStringList("allowed-commands");
        return allowedCommands.stream().anyMatch(command::startsWith);
    }
}