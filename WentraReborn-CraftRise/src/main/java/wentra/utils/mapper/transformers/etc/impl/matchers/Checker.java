package wentra.utils.mapper.transformers.etc.impl.matchers;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static wentra.utils.mapper.Entity.getThePlayer;

public class Checker {
    public static double map_posX() {
        double x1 = 0;
        String parseString = getThePlayer() + "";
        if (parseString != null && !parseString.isEmpty()) {
            Pattern pattern = Pattern.compile("x=([-\\d,\\.]+), y=([-\\d,\\.]+), z=([-\\d,\\.]+)");
            Matcher matcher = pattern.matcher(parseString);

            if (matcher.find()) {
                double x = Double.parseDouble(matcher.group(1).replace(",", "."));
                x1 = x;
            } else {
                System.out.println("Pattern not matched for X.");
            }
        } else {
            System.out.println("parseString is empty or null.");
        }

        return x1;
    }

    public static double map_posY() {
        double y1 = 0;
        String parseString = getThePlayer() + "";
        if (parseString != null && !parseString.isEmpty()) {
            Pattern pattern = Pattern.compile("x=([-\\d,\\.]+), y=([-\\d,\\.]+), z=([-\\d,\\.]+)");
            Matcher matcher = pattern.matcher(parseString);
            if (matcher.find()) {
                double y = Double.parseDouble(matcher.group(2).replace(",", "."));
                y1 = y;
            } else {
                System.out.println("Pattern not matched for Y.");
            }
        } else {
            System.out.println("parseString is empty or null.");
        }

        return y1;
    }


    public static double map_posZ() {
        double z1 = 0;
        String parseString = getThePlayer() + "";
        if (parseString != null && !parseString.isEmpty()) {
            Pattern pattern = Pattern.compile("x=([-\\d,\\.]+), y=([-\\d,\\.]+), z=([-\\d,\\.]+)");
            Matcher matcher = pattern.matcher(parseString);
            if (matcher.find()) {
                double z = Double.parseDouble(matcher.group(3).replace(",", "."));
                z1 = z;
            } else {
                System.out.println("Pattern not matched for Z.");
            }
        } else {
            System.out.println("parseString is empty or null.");
        }
        return z1;
    }
}