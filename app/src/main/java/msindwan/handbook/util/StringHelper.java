/*
 * Created by Mayank Sindwani on 2017-06-05.
 *
 * This file is part of Handbook which is licensed under the
 * Apache License, Version 2.0. Full license details are
 * available at http://www.apache.org/licenses/LICENSE-2.0.
 */
package msindwan.handbook.util;

import org.jetbrains.annotations.Contract;

/**
 * StringHelper:
 * Defines a utility class for string-related helper methods.
 */
public class StringHelper {

    /**
     * Compares two strings including null references.
     *
     * @param a The first string.
     * @param b The second string.
     * @return True if a and b are equal; false otherwise.
     */
    @Contract("!null, null -> false; null, !null -> false; null, null -> true")
    public static boolean equals(String a, String b) {
        if (a == null || b == null) {
            return a == null && b == null;
        }
        return a.equals(b);
    }

    /**
     * Checks if a string is null or empty.
     *
     * @param a The string to check.
     * @return True if null or empty; false otherwise.
     */
    public static boolean isEmpty(String a) {
        return a == null || a.isEmpty();
    }
}
