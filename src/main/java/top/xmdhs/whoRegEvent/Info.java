package top.xmdhs.whoRegEvent;

import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

public class Info {
    public Info(Plugin p, Listener l, Method m, EventPriority e) {
        this.p = p;
        this.l = l;
        this.m = m;
        this.e = e;
    }

    public Plugin p;
    public Listener l;
    public Method m;
    public EventPriority e;
}
