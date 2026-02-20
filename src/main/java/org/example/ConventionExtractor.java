package org.example;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConventionExtractor {

    static Set<String> extractConventions(CodeElement e) {
        Set<String> f = new HashSet<>();

        // Element type
        f.add("type=" + e.getType());

        // Exact package
        if (e.getPackageName() != null) {
            f.add("package=" + e.getPackageName());

            // All package prefixes (important for discovering conventions)
            String[] parts = e.getPackageName().split("\\.");
            for (int i = 1; i <= parts.length; i++) {
                String prefix = String.join(".", Arrays.copyOf(parts, i));
                f.add("packagePrefix=" + prefix);
            }
        }

        // Name
        if (e.getName() != null) {
            f.add("name=" + e.getName());

            // Name suffix patterns
            if (e.getName().length() > 3) {
                f.add("nameSuffix=" +
                        e.getName().substring(Math.max(0, e.getName().length() - 10)));
            }
        }

        // Declaring class
        if (e.getDeclaringClass() != null) {
            f.add("declaringClass=" + e.getDeclaringClass());
        }

        return f;
    }
}
