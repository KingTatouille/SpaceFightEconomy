package hillwalk.fr.spacefighteconomy.commands;

import hillwalk.fr.spacefighteconomy.EconomyManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public BalanceCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;
        double balance = economyManager.getBalance(player);

        player.sendMessage(ChatColor.GREEN + "Votre solde actuel est de " + ChatColor.GOLD + balance + " " + economyManager.currencyNamePlural() + ChatColor.GREEN + ".");
        return true;
    }
}
