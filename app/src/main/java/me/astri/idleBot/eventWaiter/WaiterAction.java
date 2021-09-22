package me.astri.idleBot.eventWaiter;

import net.dv8tion.jda.api.events.GenericEvent;

/**
 *
 * The object that is sent when the action is performed
 *
 */

public class WaiterAction<T extends GenericEvent> {
    private final T e;
    private final String id;

    public T getEvent() {
        return e;
    }

    WaiterAction(T e, String id) {
        this.e = e;
        this.id = id;
    }

    public void unregister() {
        EventWaiter.unregister(e.getClass(),id);
    }
}
