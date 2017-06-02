/*
 * Created by Mayank Sindwani on 2017-05-31.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Time:
 * Defines a utility class for time-related helper methods.
 */
public class Time {

    /**
     * Returns a SQLite compatible date time string for the
     * current time.
     *
     * @return The datetime as a string.
     */
    public static String now() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

}
