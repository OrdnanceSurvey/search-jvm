/*
 * Copyright (C) 2016 Ordnance Survey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.os.search.android.providers;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

final public class Util {

    private static final Logger LOGGER =
            Logger.getLogger(Util.class.getSimpleName());

    private Util() { }

    public static String getEnvironmentalVariable(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.isEmpty()) {
            LOGGER.warning(String.format("The '%s' environment variable not set, using '%s'", envVar, defaultValue));
            value = defaultValue;
        }
        return value;
    }

    public static String getStringResource(String fileName) {
        ClassLoader classLoader = Util.class.getClassLoader();
        return getStringResource(classLoader, fileName);
    }

    public static String getStringResource(ClassLoader classLoader, String fileName) {
        StringBuilder result = new StringBuilder("");

        File file = new File(classLoader.getResource(fileName).getFile());

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}
