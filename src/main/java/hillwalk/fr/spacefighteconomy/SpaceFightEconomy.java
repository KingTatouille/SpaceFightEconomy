package hillwalk.fr.spacefighteconomy;

import hillwalk.fr.spacefighteconomy.commands.BalanceCommand;
import hillwalk.fr.spacefighteconomy.commands.GiveCommand;
import hillwalk.fr.spacefighteconomy.listener.LeavePlayer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SpaceFightEconomy extends JavaPlugin {

    private EconomyManager economyManager;
    private Connection connection;


    @Override
    public void onEnable() {
        if (!setupVault()) {
            getLogger().severe("Vault non détecté. Le plugin SpaceFightEconomy nécessite Vault pour fonctionner. Le plugin sera désactivé.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        economyManager = new EconomyManager(this);
        setupDatabase();

        getServer().getServicesManager().register(Economy.class, economyManager, this, ServicePriority.Normal);

        getCommand("balance").setExecutor(new BalanceCommand(economyManager));
        getCommand("pay").setExecutor(new GiveCommand(economyManager));

        getServer().getPluginManager().registerEvents(new LeavePlayer(economyManager), this);
    }

    private boolean setupVault() {
        Plugin vaultPlugin = getServer().getPluginManager().getPlugin("Vault");
        return vaultPlugin != null;
    }

    @Override
    public void onDisable() {
        getServer().getServicesManager().unregister(Economy.class, economyManager);
        economyManager.saveData();
    }

    private void setupDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "/balances.db");
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS balances (uuid TEXT PRIMARY KEY, balance DOUBLE)");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            getLogger().severe("Erreur lors de l'initialisation de la base de données SQLite. Le plugin sera désactivé.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + getDataFolder().getAbsolutePath() + "/balances.db");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                getLogger().severe("Erreur lors de la connexion à la base de données SQLite.");
            }
        }
        return connection;
    }

}
