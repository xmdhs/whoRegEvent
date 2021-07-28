package top.xmdhs.whoRegEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.RegisteredListener;

import java.util.concurrent.atomic.AtomicBoolean;

public class Listener implements org.bukkit.event.Listener {

    public void regEvent(String eventName, CommandSender send) throws ClassNotFoundException {
        Class<?> c = Class.forName(eventName);
        AtomicBoolean isRun = new AtomicBoolean(false);
        Bukkit.getPluginManager().registerEvent((Class<? extends Event>) c, this, EventPriority.MONITOR, (listener, event) -> {
            if (isRun.get()){
                event.getHandlers().unregister(Main.p);
                return;
            }
            send.sendMessage("以下插件注册了 " + eventName);
            for (RegisteredListener p :event.getHandlers().getRegisteredListeners()){
                if (p.getPlugin().equals(Main.p)){
                    continue;
                }
                send.sendMessage(p.getPlugin().getName());
            }
            isRun.set(true);
        }, Main.p);
    }
}
