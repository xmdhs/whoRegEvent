package top.xmdhs.whoRegEvent;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Add implements org.bukkit.event.Listener {

    private final Class<?> ec;
    public CommandSender send;
    public boolean on;

    public Add(String eventName, CommandSender send) throws ClassNotFoundException {
        this.ec = Class.forName(eventName);
        this.send = send;
        this.on = true;
    }


    public void getRegEvent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Set<String> s1 = new HashSet<>();
        HandlerList h = (HandlerList) ec.getMethod("getHandlerList").invoke(null);
        for (RegisteredListener r : h.getRegisteredListeners()) {
            s1.add(r.getPlugin().getName() + " " + r.getListener().getClass().getName());
        }
        for (String s : s1) {
            send.sendMessage(s);
        }
    }


    public void insert() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        HandlerList h = (HandlerList) ec.getMethod("getHandlerList").invoke(null);
        for (RegisteredListener r : h.getRegisteredListeners()) {
            Field ff = r.getClass().getDeclaredField("executor");
            ff.setAccessible(true);
            EventExecutor ex = (EventExecutor) ff.get(r);
            ff.set(r, (EventExecutor) (l, e) -> {
                if (on) {
                    try {
                        Field f = e.getClass().getDeclaredField("callbackWre");
                        AtomicBoolean has = new AtomicBoolean(false);
                        f.set(e, (InvocationHandler) (Object methodName, Method method, Object[] args) -> {
                            if (!has.get()) {
                                send.sendMessage("------------");
                                has.set(true);
                                send.sendMessage("插件 " + r.getPlugin().getName() + " 调用了 " + e.getEventName() + " 事件的以下方法");
                            }
                            if (args.length != 0) {
                                send.sendMessage(methodName + " 方法，参数 " + Arrays.toString(args));
                            } else {
                                send.sendMessage(methodName + " 方法");
                            }
                            return null;
                        });
                    } catch (NoSuchFieldException | IllegalAccessException ignored) {
                    }
                }
                ex.execute(l, e);
            });
        }
    }
}
