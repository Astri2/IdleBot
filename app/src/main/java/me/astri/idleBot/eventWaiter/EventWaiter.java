package me.astri.idleBot.eventWaiter;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class EventWaiter implements EventListener {

    private static final HashMap<Class<GenericEvent>, CopyOnWriteArrayList<Waiter<GenericEvent>>> waiterMap = new HashMap<>();

    @Override
    public void onEvent(@NotNull GenericEvent e) {
            if (waiterMap.containsKey(e.getClass())) {
                waiterMap.get(e.getClass()).forEach(waiter -> {
                    if(waiter.getConditions().test(e)) {
                        waiter.getAction().accept(new WaiterAction<>(e,waiter.getId()));
                        if (waiter.getAutoRemove())
                            waiterMap.get(e.getClass()).remove(waiter);
                    } else {
                        if(waiter.getFailureAction() != null)
                            waiter.getFailureAction().accept(new WaiterAction<>(e,waiter.getId()));
                    }
                });
            }
    }

    public static void register(Waiter<? extends GenericEvent> waiterToRegister, String id) {
        @SuppressWarnings("unchecked")
        Waiter<GenericEvent> waiter = (Waiter<GenericEvent>) waiterToRegister; //casting from template to GenericEvent
        waiter.setId(id);
        waiterMap.compute(
                waiter.getEventType(), (k,v) -> {
                    if(v == null)
                        return new CopyOnWriteArrayList <>() {{add(waiter);}};
                    v.removeIf(wait -> wait.getId().equals(waiter.getId()));
                    v.add(waiter);
                    return v;
                });

        if(waiterToRegister.getExpirationTime() != 0) { //if = 0, no auto expiration
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(waiterMap.get(waiter.getEventType()).remove(waiter) && waiter.getTimeoutAction() != null) { //if waiter was in the list && there is a timeoutAction
                        waiter.getTimeoutAction().run();
                        unregister(waiter);
                    }
                }
            }, TimeUnit.MILLISECONDS.convert(waiter.getExpirationTime(),waiter.getTimeUnit()));
        }
    }

    public static void unregister(Waiter<GenericEvent> waiter) {
        waiterMap.get(waiter.getEventType()).remove(waiter);
    }
    public static void unregister(Class<? extends GenericEvent> eventType, String id) {
        waiterMap.get(eventType).removeIf(event -> event.getId().equals(id));
    }
}
