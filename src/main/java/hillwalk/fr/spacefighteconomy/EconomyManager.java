package hillwalk.fr.spacefighteconomy;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EconomyManager extends AbstractEconomy {

        private final SpaceFightEconomy plugin;
        private final HashMap<UUID, Double> balances;

        public EconomyManager(SpaceFightEconomy plugin) {
            this.plugin = plugin;
            balances = new HashMap<>();

            loadData();
        }

    public void loadData() {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM balances");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                double balance = resultSet.getDouble("balance");
                balances.put(uuid, balance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Erreur lors du chargement des données de la base de données SQLite.");
        }
    }

    public void saveData() {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("INSERT OR REPLACE INTO balances (uuid, balance) VALUES (?, ?)");

            for (UUID uuid : balances.keySet()) {
                statement.setString(1, uuid.toString());
                statement.setDouble(2, balances.get(uuid));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Erreur lors de l'enregistrement des données dans la base de données SQLite.");
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "SpaceFightEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        return String.format("%.2f %s", v, v == 1 ? currencyNameSingular() : currencyNamePlural());
    }

    @Override
    public String currencyNamePlural() {
        return plugin.getConfig().getString("currencyNamePlural");
    }

    @Override
    public String currencyNameSingular() {
        return plugin.getConfig().getString("currencyNameSingular");
    }


    @Override
    public boolean hasAccount(String s) {
        return false;
    }

    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        double balance = 0.0;

        try (PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT balance FROM balances WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    balance = resultSet.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Erreur lors de la récupération du solde du joueur " + player.getName() + " à partir de la base de données SQLite.");
        }

        return balance;
    }

    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }


    @Override
    public double getBalance(String playerName) {
        UUID uuid = null;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equalsIgnoreCase(playerName)) {
                uuid = onlinePlayer.getUniqueId();
                break;
            }
        }

        if (uuid == null) {
            return 0.0;
        }

        double balance = 0.0;

        try (PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT balance FROM balances WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    balance = resultSet.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().severe("Erreur lors de la récupération du solde du joueur " + playerName + " à partir de la base de données SQLite.");
        }

        return balance;
    }



    @Override
    public boolean has(String s, double v) {
        return false;
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String s) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        UUID uuid = player.getUniqueId();
        double balance = getBalance(player);

        if (amount < 0) {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Le montant à retirer doit être positif.");
        }

        if (balance < amount) {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Fonds insuffisants pour retirer " + amount + " " + currencyNamePlural() + ".");
        }

        setBalance(uuid, balance - amount);
        return new EconomyResponse(amount, balance - amount, EconomyResponse.ResponseType.SUCCESS, "");
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        UUID uuid = player.getUniqueId();
        double balance = getBalance(player);

        if (amount < 0) {
            return new EconomyResponse(0, balance, EconomyResponse.ResponseType.FAILURE, "Le montant à déposer doit être positif.");
        }

        setBalance(uuid, balance + amount);
        return new EconomyResponse(amount, balance + amount, EconomyResponse.ResponseType.SUCCESS, "");
    }

    public void setBalance(UUID uuid, double amount) {
        if (amount < 0) {
            amount = 0;
        }
        balances.put(uuid, amount);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return balances.containsKey(player.getUniqueId());
    }
}
