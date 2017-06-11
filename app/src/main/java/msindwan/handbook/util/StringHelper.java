/*
 * Copyright (C) 2017 Mayank Sindwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
