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
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin implements CommandExecutor {
    protected static Plugin p;
    private Map<String, Add> map = new HashMap<>();

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
        Add a = map.get(strings[0]);
        if (a == null || !a.on) {
            if (a == null){
                try {
                    a = new Add(strings[0], commandSender);
                } catch (ClassNotFoundException e) {
                    commandSender.sendMessage("没有这个事件");
                    e.printStackTrace();
                    return false;
                }
            }
            a.on = true;
            a.send = commandSender;
            map.put(strings[0], a);
            try {
                a.getRegEvent();
                commandSender.sendMessage("以下插件监听了 " + strings[0]);
                a.printRegEvent();
                a.insert();
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
                a.on = false;
                commandSender.sendMessage("已停止检测 " + strings[0]);
        }
        return true;
    }
}
