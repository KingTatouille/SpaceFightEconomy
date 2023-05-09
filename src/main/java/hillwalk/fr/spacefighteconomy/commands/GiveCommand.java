package hillwalk.fr.spacefighteconomy.commands;

import hillwalk.fr.spacefighteconomy.EconomyManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GiveCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public GiveCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Utilisation incorrecte. Syntaxe: /give <joueur> <montant>");
            return true;
        }

        OfflinePlayer targetPlayer = sender.getServer().getOfflinePlayer(args[0]);

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Le montant doit être un nombre.");
            return true;
        }

        if (amount < 0) {
            sender.sendMessage(ChatColor.RED + "Le montant doit être positif.");
            return true;
        }

        EconomyResponse response = economyManager.depositPlayer(targetPlayer, amount);

        if (response.transactionSuccess()) {
            sender.sendMessage(ChatColor.GREEN + "Vous avez donné " + ChatColor.GOLD + amount + " " + economyManager.currencyNamePlural() + ChatColor.GREEN + " à " + ChatColor.GOLD + targetPlayer.getName() + ChatColor.GREEN + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Erreur lors de la transaction: " + response.errorMessage);
        }

        return true;
    }
}
