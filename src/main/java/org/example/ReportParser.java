package org.example;

import com.fasterxml.jackson.databind.*;
import java.io.File;
import java.util.*;

public class ReportParser {

    public static List<CodeElement> parse(File jsonFile) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonFile);

        List<CodeElement> elements = new ArrayList<>();
        traverse(root, null, null, elements);
        return elements;
    }

    private static void traverse(JsonNode node,
                                 String currentPackage,
                                 String declaringClass,
                                 List<CodeElement> out) {

        String type = node.path("type").asText(null);
        String name = node.path("name").asText(null);

        if ("package".equals(type)) {
            currentPackage = name;
        }

        if (type != null && !type.equals("annotation") && !type.equals("package")) {
            CodeElement e = new CodeElement(UUID.randomUUID().toString(),type,name,
                    currentPackage,declaringClass,new HashSet<>(),new HashMap<>());


            node.path("metadata").fields()
                    .forEachRemaining(f -> e.addProperty(f.getKey(), f.getValue().asText()));

            for (JsonNode child : node.path("children")) {
                if ("annotation".equals(child.path("type").asText())) {
                    e.addAnnotation(child.path("name").asText());
                }
            }

            out.add(e);

            if ("class".equals(type)) {
                declaringClass = name;
            }
        }

        for (JsonNode child : node.path("children")) {
            traverse(child, currentPackage, declaringClass, out);
        }
    }
}
