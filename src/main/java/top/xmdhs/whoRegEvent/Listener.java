package top.xmdhs.whoRegEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Listener implements org.bukkit.event.Listener {

    public void regEvent(String eventName, CommandSender send) throws ClassNotFoundException {
        Class<?> ec = Class.forName(eventName);

        Plugin[] pl = Bukkit.getPluginManager().getPlugins();
        for (Plugin p : pl) {
            ArrayList<RegisteredListener> s = HandlerList.getRegisteredListeners(p);
            Bukkit.getScheduler().runTaskAsynchronously(Main.p, () -> {
                for (RegisteredListener r : s) {
                    Method[] ml = r.getListener().getClass().getMethods();
                    for (Method m : ml) {
                        for (Class<?> c : m.getParameterTypes()) {
                            if (ec.equals(c)) {
                                send.sendMessage(p.getName()+ " " + r.getPriority().toString());
                                return;
                            }
                        }
                    }
                }
            });
        }
    }
}
