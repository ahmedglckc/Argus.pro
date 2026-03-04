package wentra.event;

public class Event {
    public boolean cancelled = false;
    private Type type;
    public void setType(Type type)
    {
        this.type = type;
    }
    public enum Type
    {
        PRE, POST
    }

    public void cancel() {
        cancelled = true;
    }
    public void cancel2(Object packet) {
        cancelled = true;
    }

    public static class StateEvent extends Event {
        private boolean pre = true;

        public boolean isPre() { return pre;}
        public boolean isPost() { return !pre;}
        public void setPost() { pre = false; }
    }
}
