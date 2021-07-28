package top.xmdhs.whoRegEvent;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Method;
import java.util.*;

public class Listener implements org.bukkit.event.Listener {

    public void regEvent(String eventName, CommandSender send) throws ClassNotFoundException {
        Class<?> ec = Class.forName(eventName);

        ArrayList<HandlerList> hl = HandlerList.getHandlerLists();
        Set<String> s = new HashSet<>();
        for (HandlerList h : hl) {
            C:
            for (RegisteredListener r : h.getRegisteredListeners()) {
                for (Method m : r.getListener().getClass().getMethods()) {
                    for (Class<?> c : m.getParameterTypes()) {
                        if (ec.equals(c)) {
                            s.add(r.getPlugin().getName() + " " + r.getListener().getClass().getName());
                            continue C;
                        }
                    }
                }
            }
        }
        for (String msg : s) {
            send.sendMessage(msg);
        }
    }
}
