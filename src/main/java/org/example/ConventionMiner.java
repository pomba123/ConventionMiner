package org.example;

import java.util.*;


public class ConventionMiner {

    public static List<Convention> mine(List<CodeElement> elements,
                                        int minSupport,
                                        double minConfidence) {

        Map<CodeElement, Set<String>> structuralFeatures = new HashMap<>();
        Map<String, Set<CodeElement>> annotatedBy = new HashMap<>();

        // Build structural features and annotation index
        for (CodeElement e : elements) {

            Set<String> features = ConventionExtractor.extractConventions(e);
            structuralFeatures.put(e, features);

            for (String annotation : e.getAnnotations()) {
                annotatedBy
                        .computeIfAbsent(annotation, k -> new HashSet<>())
                        .add(e);
            }
        }

        List<Convention> conventions = new ArrayList<>();



        for (String annotation : annotatedBy.keySet()) {

            Set<CodeElement> annotatedElements = annotatedBy.get(annotation);

            if (annotatedElements.size() < minSupport)
                continue;


            Map<String, Integer> featureCount = new HashMap<>();

            for (CodeElement e : annotatedElements) {
                for (String f : structuralFeatures.get(e)) {
                    featureCount.merge(f, 1, Integer::sum);
                }
            }


            Set<String> candidateFeatures = new HashSet<>();
            for (Map.Entry<String, Integer> entry : featureCount.entrySet()) {
                if (entry.getValue() >= minSupport) {
                    candidateFeatures.add(entry.getKey());
                }
            }


            Set<Set<String>> combinations =
                    generateFeatureCombinations(candidateFeatures);

            for (Set<String> combo : combinations) {

                int lhsCount = 0;
                int bothCount = 0;

                for (CodeElement e : elements) {

                    if (structuralFeatures.get(e).containsAll(combo)) {
                        lhsCount++;

                        if (e.getAnnotations().contains(annotation)) {
                            bothCount++;
                        }
                    }
                }

                if (lhsCount < minSupport)
                    continue;

                double confidence = (double) bothCount / lhsCount;

                if (confidence >= minConfidence) {

                    Convention c = new Convention();
                    c.conditions = combo;
                    c.annotation = annotation;
                    c.support = bothCount;
                    c.confidence = confidence;

                    conventions.add(c);
                }
            }
        }


        conventions.addAll(
                mineAnnotationCooccurrence(elements, minSupport, minConfidence)
        );

        return removeRedundant(conventions);
    }


    private static List<Convention> mineAnnotationCooccurrence(
            List<CodeElement> elements,
            int minSupport,
            double minConfidence) {

        List<Convention> rules = new ArrayList<>();

        Set<String> allAnnotations = new HashSet<>();

        for (CodeElement e : elements) {
            allAnnotations.addAll(e.getAnnotations());
        }

        for (String lhsAnnotation : allAnnotations) {

            for (String rhsAnnotation : allAnnotations) {

                if (lhsAnnotation.equals(rhsAnnotation))
                    continue; // avoid tautology

                int lhsCount = 0;
                int bothCount = 0;

                for (CodeElement e : elements) {

                    if (e.getAnnotations().contains(lhsAnnotation)) {
                        lhsCount++;

                        if (e.getAnnotations().contains(rhsAnnotation)) {
                            bothCount++;
                        }
                    }
                }

                if (lhsCount < minSupport)
                    continue;

                double confidence = (double) bothCount / lhsCount;

                if (confidence >= minConfidence) {

                    Convention c = new Convention();
                    c.conditions = Set.of("hasAnnotation=" + lhsAnnotation);
                    c.annotation = rhsAnnotation;
                    c.support = bothCount;
                    c.confidence = confidence;

                    rules.add(c);
                }
            }
        }

        return rules;
    }


    private static Set<Set<String>> generateFeatureCombinations(Set<String> features) {

        Set<Set<String>> combinations = new HashSet<>();
        List<String> list = new ArrayList<>(features);

        // Single features
        for (String f : list) {
            combinations.add(Set.of(f));
        }

        // Pair features
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {

                Set<String> pair = new HashSet<>();
                pair.add(list.get(i));
                pair.add(list.get(j));
                combinations.add(pair);
            }
        }

        return combinations;
    }

    // =====================================================
    // Remove Redundant Rules
    // =====================================================

    private static List<Convention> removeRedundant(List<Convention> rules) {

        List<Convention> filtered = new ArrayList<>(rules);

        filtered.removeIf(r1 ->
                rules.stream().anyMatch(r2 ->
                        !r1.equals(r2)
                                && r2.annotation.equals(r1.annotation)
                                && r2.conditions.containsAll(r1.conditions)
                                && r2.conditions.size() > r1.conditions.size()
                                && r2.support == r1.support
                                && r2.confidence == r1.confidence
                )
        );

        return filtered;
    }
}

