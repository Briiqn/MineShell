package dev.briiqn.mineShell;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthenticationManager {
    private final MineShell plugin;
    private final Set<UUID> authenticatedPlayers;
    private final String secretKey;
    private final LuckPerms luckPerms;

    public AuthenticationManager(MineShell plugin) {
        this.plugin = plugin;
        this.authenticatedPlayers = new HashSet<>();
        this.secretKey = generateOrLoadSecretKey();
        this.luckPerms = LuckPermsProvider.get();
        loadAuthenticatedPlayers();
    }

    public boolean isAuthenticated(Player player) {
        return authenticatedPlayers.contains(player.getUniqueId()) ||
                luckPerms.getUserManager().getUser(player.getUniqueId()).getCachedData().getPermissionData().checkPermission("mineshell.shellaccess").asBoolean();
    }

    public void authenticate(Player player, String password) {
        if (password.equals(plugin.getConfig().getString("admin-password")) ||
                luckPerms.getUserManager().getUser(player.getUniqueId()).getCachedData().getPermissionData().checkPermission("mineshell.shellaccess").asBoolean()) {
            authenticatedPlayers.add(player.getUniqueId());
            saveAuthenticatedPlayers();
            player.sendMessage("Authentication successful.");
        } else {
            player.sendMessage("Authentication failed. Incorrect password or insufficient permissions.");
        }
    }

    public void deauthenticate(Player player) {
        authenticatedPlayers.remove(player.getUniqueId());
        saveAuthenticatedPlayers();
        player.sendMessage("You have been deauthenticated.");
    }

    private void loadAuthenticatedPlayers() {
        FileConfiguration config = plugin.getConfig();
        authenticatedPlayers.clear();
        for (String uuidString : config.getStringList("authenticated-players")) {
            authenticatedPlayers.add(UUID.fromString(uuidString));
        }
    }

    private void saveAuthenticatedPlayers() {
        FileConfiguration config = plugin.getConfig();
        config.set("authenticated-players", authenticatedPlayers.stream()
                .map(UUID::toString)
                .toList());
        plugin.saveConfig();
    }

    private String generateOrLoadSecretKey() {
        FileConfiguration config = plugin.getConfig();
        String key = config.getString("secret-key");
        if (key == null || key.isEmpty()) {
            key = generateSecretKey();
            config.set("secret-key", key);
            plugin.saveConfig();
        }
        return key;
    }

    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}