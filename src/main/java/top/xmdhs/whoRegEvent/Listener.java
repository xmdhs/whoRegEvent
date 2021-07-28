package top.xmdhs.whoRegEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Listener implements org.bukkit.event.Listener {

    public void regEvent(String eventName, CommandSender send) throws ClassNotFoundException {
        Class<?> ec = Class.forName(eventName);

        ArrayList<HandlerList> hl = HandlerList.getHandlerLists();
        Set<String> sl = new HashSet<>();
        for (HandlerList h : hl) {
            for (RegisteredListener r : h.getRegisteredListeners()) {
                for (Method m : r.getListener().getClass().getMethods()) {
                    for (Class<?> c : m.getParameterTypes()) {
                        if (ec.equals(c)) {
                            sl.add(r.getPlugin().getName() + " " + r.getPriority().toString() + " " + r.getListener().getClass().getName());
                        }
                    }
                }
            }
        }
        for (String s : sl) {
            send.sendMessage(s);
        }
    }
}
