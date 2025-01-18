package fan.cropsprocess.com.cmd;

import fan.cropsprocess.com.inventory.TradeGuiLoader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MarketCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // 打开商店选择界面
            Inventory shopSelectorGui = TradeGuiLoader.loadShopSelector("market.yml");
            player.openInventory(shopSelectorGui);
        } else {
            sender.sendMessage("该命令只能由玩家执行！");
        }
        return true;
    }
}
