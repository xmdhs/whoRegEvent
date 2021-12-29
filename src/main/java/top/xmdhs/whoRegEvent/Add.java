package top.xmdhs.whoRegEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Add implements org.bukkit.event.Listener {

    private final Class<?> ec;
    public CommandSender send;
    private Set<String> s = null;
    public boolean on;

    public Add(String eventName, CommandSender send) throws ClassNotFoundException {
        this.ec = Class.forName(eventName);
        this.send = send;
        this.on = true;
    }

    private List<Info> list = null;

    public void getRegEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        list = new ArrayList<>();
        s = new HashSet<>();
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

    public void insert() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HandlerList h = (HandlerList) ec.getMethod("getHandlerList").invoke(null);
        for (Info i : list) {
            h.unregister(i.p);
            Bukkit.getPluginManager().registerEvent((Class<? extends Event>) ec, i.l, i.e, (l, e) -> {
                try {
                    try {
                        Field f = e.getClass().getDeclaredField("callbackWre");
                        AtomicBoolean has = new AtomicBoolean(false);
                        f.set(e, (Callback) (String methodName, Object[] args) -> {
                            if (!has.get()) {
                                send.sendMessage("------------");
                                has.set(true);
                            }
                            send.sendMessage("插件 " + i.p.getName() + " 调用了 " + e.getEventName() + " 事件的 " + methodName + " 方法");
                            if (args.length != 0) {
                                send.sendMessage("参数 " + Arrays.toString(args));
                            }
                        });
                    } catch (NoSuchFieldException ignored) {
                    }
                    i.m.invoke(i.l, e);
                } catch (IllegalAccessException | InvocationTargetException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                }
            }, i.p, false);
        }
    }

}
