package wentra.event;

public class EventBus extends com.google.common.eventbus.EventBus {
    public static EventBus bus = new EventBus();


    public static <T extends Event> void callEvent(T ev) {
        bus.post(ev);
    }

    public static void subscribe(Object obj) {
        bus.register(obj);
    }

    public static void unSubscribe(Object obj) {
        bus.unregister(obj);
    }
}
