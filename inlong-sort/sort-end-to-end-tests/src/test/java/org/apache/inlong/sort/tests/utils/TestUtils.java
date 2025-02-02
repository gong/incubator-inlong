/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.inlong.sort.tests.utils;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Test util for test container.
 */
public class TestUtils {
    private static final ParameterProperty<Path> MODULE_DIRECTORY =
            new ParameterProperty<>("moduleDir", Paths::get);

    /**
     * Searches for a resource file matching the given regex in the given directory. This method is
     * primarily intended to be used for the initialization of static {@link Path} fields for
     * resource file(i.e. jar, config file) that reside in the modules {@code target} directory.
     *
     * @param resourceNameRegex regex pattern to match against
     * @return Path pointing to the matching jar
     * @throws RuntimeException if none or multiple resource files could be found
     */
    public static Path getResource(final String resourceNameRegex) {
        // if the property is not set then we are most likely running in the IDE, where the working
        // directory is the
        // module of the test that is currently running, which is exactly what we want
        Path moduleDirectory = MODULE_DIRECTORY.get(Paths.get("").toAbsolutePath());

        try (Stream<Path> dependencyResources = Files.walk(moduleDirectory)) {
            final List<Path> matchingResources =
                    dependencyResources
                            .filter(
                                    jar ->
                                            Pattern.compile(resourceNameRegex)
                                                    .matcher(jar.toAbsolutePath().toString())
                                                    .find())
                            .collect(Collectors.toList());
            switch (matchingResources.size()) {
                case 0:
                    throw new RuntimeException(
                            new FileNotFoundException(
                                    String.format(
                                            "No resource file could be found that matches the pattern %s. "
                                                    + "This could mean that the test module must be rebuilt via maven.",
                                            resourceNameRegex)));
                case 1:
                    return matchingResources.get(0);
                default:
                    throw new RuntimeException(
                            new IOException(
                                    String.format(
                                            "Multiple resource files were found matching the pattern %s. Matches=%s",
                                            resourceNameRegex, matchingResources)));
            }
        } catch (final IOException ioe) {
            throw new RuntimeException("Could not search for resource resource files.", ioe);
        }
    }

    /**
     * A simple system properties value getter with default value when could not find the system property.
     * @param <V>
     */
    static class ParameterProperty<V> {

        private final String propertyName;
        private final Function<String, V> converter;

        public ParameterProperty(final String propertyName, final Function<String, V> converter) {
            this.propertyName = propertyName;
            this.converter = converter;
        }

        /**
         * Retrieves the value of this property, or the given default if no value was set.
         *
         * @return the value of this property, or the given default if no value was set
         */
        public V get(final V defaultValue) {
            final String value = System.getProperty(propertyName);
            return value == null ? defaultValue : converter.apply(value);
        }
    }

    @Test
    public void testReplaceholder() {
        String before = "today is ${date}, today weather is ${weather}";
        Map<String, Object> maps = new HashMap<>();
        maps.put("date", "2022.08.05");
        maps.put("weather", "rain");
        String after = PlaceholderResolver.getDefaultResolver().resolveByMap(before, maps);
        assertEquals(after, "today is 2022.08.05, today weather is rain");
    }
}
