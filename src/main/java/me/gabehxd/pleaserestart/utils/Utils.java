package me.gabehxd.pleaserestart.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
    /**
     * @param seconds Seconds
     * @return Time of seconds in ticks.
     */
    public static int timeToTicks(int seconds) {
        return seconds * 20;
    }
}
