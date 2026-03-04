package wentra.module.setting;

public class DelaySetting extends Setting {
    private int range;
    private int width;
    private int height;
    public boolean dragging;

    public DelaySetting(final int integer1, final int integer2) {
        super("ComeToMuslim");
        this.height = 0;
        this.dragging = false;
        this.range = integer2;
        this.width = integer1;
    }

    public int getSelectedDelay() {
        return this.width;
    }

}
