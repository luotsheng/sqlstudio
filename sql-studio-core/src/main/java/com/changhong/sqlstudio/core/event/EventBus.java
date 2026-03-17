package com.changhong.sqlstudio.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件总线
 *
 * @author luotiansheng
 */
public class EventBus {

    private static final Map<Class<? extends Event>, List<EventListener>> listeners
            = new ConcurrentHashMap<>();

    /**
     * 订阅事件
     */
    public static void subscribe(Class<? extends Event> clazz, EventListener listener) {
        listeners.computeIfAbsent(clazz, k -> new ArrayList<>()).add(listener);
    }

    /**
     * 发布事件
     */
    public static <T extends Event> void publish(T event) {
        List<EventListener> eventListeners = listeners.get(event.getClass());

        if (eventListeners == null || eventListeners.isEmpty())
            return;

        for (EventListener eventListener : eventListeners)
            eventListener.eventTigger(event);
    }

}
