package dev.briiqn.mineShell;


import org.bukkit.plugin.java.JavaPlugin;

public final class MineShell extends JavaPlugin {
    private ShellCommandExecutor shellCommandExecutor;
    private AuthenticationManager authManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.authManager = new AuthenticationManager(this);
        this.shellCommandExecutor = new ShellCommandExecutor(this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getCommand("shellauth").setExecutor(new ShellAuthCommand(this));
    }

    @Override
    public void onDisable() {
    }

    public ShellCommandExecutor getShellCommandExecutor() {
        return shellCommandExecutor;
    }

    public AuthenticationManager getAuthManager() {
        return authManager;
    }
}

