package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ConventionDetector {

    public static void main(String[] args) throws Exception {
        String filePath = args[0];
        File report = new File(filePath);

        List<CodeElement> elements = ReportParser.parse(report);

        List<Convention> conventions =
                ConventionMiner.mine(elements, 2, 0.7);

        try (PrintWriter writer = new PrintWriter(new FileWriter("conventions.txt"))) {

            conventions.forEach(c -> {
                writer.println("CONVENTION:");
                writer.println("  IF " + c.conditions);
                writer.println("  THEN @" + c.annotation);
                writer.println("  support=" + c.support);
                writer.println();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
