package top.xmdhs.whoRegEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Add implements org.bukkit.event.Listener {

    private final Class<?> ec;
    public CommandSender send;
    private final Set<String> s = new HashSet<>();
    public boolean on;

    public Add(String eventName, CommandSender send) throws ClassNotFoundException {
        this.ec = Class.forName(eventName);
        this.send = send;
        this.on = true;
    }

    private final List<Info> list = new ArrayList<>();

    public void getRegEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HandlerList h = (HandlerList) ec.getMethod("getHandlerList").invoke(null);
        for (RegisteredListener r : h.getRegisteredListeners()) {
            for (Method m : r.getListener().getClass().getMethods()) {
                for (Class<?> c : m.getParameterTypes()) {
                    if (ec.equals(c)) {
                        s.add(r.getPlugin().getName() + " " + r.getListener().getClass().getName());
                        list.add(new Info(r.getPlugin(), r.getListener(), m, r.getPriority()));
                    }
                }
            }
        }
    }

    public void printRegEvent() {
        if (s.size() == 0) {
            return;
        }
        for (String s : s) {
            send.sendMessage(s);
        }
    }

    private Set<Event> eSet = new HashSet<>();

    public void insert() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HandlerList h = (HandlerList) ec.getMethod("getHandlerList").invoke(null);
        for (Info i : list) {
            h.unregister(i.p);
            Bukkit.getPluginManager().registerEvent((Class<? extends Event>) ec, i.l, i.e, (l, e) -> {
                try {
                    i.m.invoke(i.l, e);
                    if (on && e instanceof Cancellable) {
                        if (((Cancellable) e).isCancelled() && !eSet.contains(e)) {
                            send.sendMessage(i.p.getName() + " 取消了" + e.getEventName() + " 事件");
                            eSet.add(e);
                        }
                        if (!((Cancellable) e).isCancelled() && eSet.contains(e)) {
                            send.sendMessage(i.p.getName() + " 恢复了" + e.getEventName() + " 事件");
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
            }, i.p, false);
        }
    }



}
