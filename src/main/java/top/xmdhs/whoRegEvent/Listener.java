package top.xmdhs.whoRegEvent;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Listener implements org.bukkit.event.Listener {

    public void regEvent(String eventName, CommandSender send) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> ec = Class.forName(eventName);
        HandlerList h = (HandlerList) ec.getMethod("getHandlerList").invoke(null);

        Set<String> s = new HashSet<>();
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

        for (String msg : s) {
            send.sendMessage(msg);
        }
    }
}
