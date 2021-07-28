package top.xmdhs.whoRegEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

public class Main extends JavaPlugin implements CommandExecutor {
    protected static Plugin p;
    private final Listener l = new Listener();

    @Override
    public void onEnable() {
        p = this;
        PluginCommand c = Bukkit.getPluginCommand("whoregevent");
        assert c != null;
        c.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player p = (Player) commandSender;
            if (!p.isOp()) {
                p.sendMessage("仅 op 可使用");
                return false;
            }
        }
        if (strings.length != 1) {
            commandSender.sendMessage("/whoregevent <event class name>");
            return false;
        }
        try {
            l.regEvent(strings[0], commandSender);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            commandSender.sendMessage("没有这个事件");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
