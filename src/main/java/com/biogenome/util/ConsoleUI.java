package com.biogenome.util;

import java.util.Scanner;

/**
 * ConsoleUI - Professional Scientific CLI Theme & Formatting Utility.
 * <p>
 * Provides standardized ANSI color palettes, clean borders, structured result cards,
 * formatted nucleotide sequence visualizers, and robust interactive input validation
 * for the BioGenome Eclipse console application.
 * </p>
 *
 * @author BioGenome Team
 * @version 3.1.0
 */
public final class ConsoleUI {

    // -------------------------------------------------------------------------
    // ANSI ESCAPE COLOR CODES
    // -------------------------------------------------------------------------
    public static final String RESET   = "\u001B[0m";
    public static final String BOLD    = "\u001B[1m";
    public static final String DIM     = "\u001B[2m";
    public static final String ITALIC  = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";

    // Standard foreground colors
    public static final String BLACK   = "\u001B[30m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    // Bright high-intensity foreground colors
    public static final String BRIGHT_RED     = "\u001B[91m";
    public static final String BRIGHT_GREEN   = "\u001B[92m";
    public static final String BRIGHT_YELLOW  = "\u001B[93m";
    public static final String BRIGHT_BLUE    = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN    = "\u001B[96m";
    public static final String BRIGHT_WHITE   = "\u001B[97m";

    // Muted / dark tones
    public static final String GRAY    = "\u001B[90m";

    // Global color toggle (defaults to true; can be toggled in menu or via -Dbiogenome.ansi=false)
    private static boolean colorsEnabled = !"false".equalsIgnoreCase(System.getProperty("biogenome.ansi"));

    private ConsoleUI() {}

    public static boolean isColorsEnabled() {
        return colorsEnabled;
    }

    public static void setColorsEnabled(boolean enabled) {
        colorsEnabled = enabled;
    }

    /**
     * Wrap text with an ANSI color code if colors are enabled.
     */
    public static String color(String code, String text) {
        if (!colorsEnabled) return text;
        return code + text + RESET;
    }

    public static String green(String text)   { return color(BRIGHT_GREEN, text); }
    public static String cyan(String text)    { return color(BRIGHT_CYAN, text); }
    public static String yellow(String text)  { return color(BRIGHT_YELLOW, text); }
    public static String magenta(String text) { return color(BRIGHT_MAGENTA, text); }
    public static String blue(String text)    { return color(BRIGHT_BLUE, text); }
    public static String red(String text)     { return color(BRIGHT_RED, text); }
    public static String gray(String text)    { return color(GRAY, text); }
    public static String bold(String text)    { return color(BOLD, text); }
    public static String white(String text)   { return color(BRIGHT_WHITE, text); }

    // -------------------------------------------------------------------------
    // BORDERS & VISUAL CARDS
    // -------------------------------------------------------------------------
    public static final String DOUBLE_LINE =
            "================================================================================";
    public static final String SINGLE_LINE =
            "--------------------------------------------------------------------------------";
    public static final String DOTTED_LINE =
            "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ";

    public static void printBanner() {
        System.out.println(color(BRIGHT_GREEN + BOLD, DOUBLE_LINE));
        System.out.println(color(BRIGHT_GREEN + BOLD, "                  BIOGENOME | BIOINFORMATICS ALGORITHM EXPLORER"));
        System.out.println(color(BRIGHT_CYAN,         "           Demonstrating Classical Data Structures & Algorithms Through"));
        System.out.println(color(BRIGHT_CYAN,         "                        Real Biological Use Cases (v3.1.0)"));
        System.out.println(color(BRIGHT_GREEN + BOLD, DOUBLE_LINE));
    }

    public static void printSectionHeader(String title) {
        System.out.println();
        System.out.println(color(BRIGHT_CYAN + BOLD, DOUBLE_LINE));
        System.out.println(color(BRIGHT_CYAN + BOLD, "  " + title.toUpperCase()));
        System.out.println(color(BRIGHT_CYAN + BOLD, DOUBLE_LINE));
    }

    public static void printMenuHeader(String title) {
        System.out.println();
        System.out.println(color(BRIGHT_CYAN + BOLD, SINGLE_LINE));
        System.out.println(color(BRIGHT_WHITE + BOLD, "  " + title));
        System.out.println(color(BRIGHT_CYAN + BOLD, SINGLE_LINE));
    }

    /**
     * Biology-First Algorithm Context Card.
     */
    public static void printAlgorithmHeader(String biologicalTask, String dsaAlgorithm, String purpose) {
        System.out.println();
        System.out.println(color(BRIGHT_CYAN + BOLD, DOUBLE_LINE));
        System.out.println(color(BRIGHT_GREEN + BOLD,  "  BIOLOGICAL TASK : ") + color(BRIGHT_WHITE + BOLD, biologicalTask));
        System.out.println(color(BRIGHT_MAGENTA + BOLD,"  DSA ALGORITHM   : ") + color(BRIGHT_YELLOW + BOLD, dsaAlgorithm));
        System.out.println(color(GRAY,                 "  PURPOSE         : ") + color(BRIGHT_WHITE, purpose));
        System.out.println(color(BRIGHT_CYAN + BOLD, DOUBLE_LINE));
    }

    public static void printSubSection(String title) {
        System.out.println(color(BRIGHT_CYAN, "--- " + title + " " + "-".repeat(Math.max(2, 74 - title.length()))));
    }

    public static void printResultsHeader() {
        System.out.println(color(BRIGHT_GREEN, "---------------------------------- RESULTS -------------------------------------"));
    }

    public static void printTraceHeader() {
        System.out.println(color(BRIGHT_MAGENTA, "------------------------------ EXECUTION TRACE ---------------------------------"));
    }

    public static void printComplexity(String timeComplexity, String spaceComplexity, double runtimeMs) {
        System.out.println(color(BRIGHT_YELLOW, "--------------------------------- COMPLEXITY -----------------------------------"));
        System.out.printf("  %-20s : %s%n", color(GRAY, "Time Complexity"), color(BRIGHT_WHITE + BOLD, timeComplexity));
        System.out.printf("  %-20s : %s%n", color(GRAY, "Auxiliary Space"), color(BRIGHT_WHITE + BOLD, spaceComplexity));
        System.out.printf("  %-20s : %s ms%n", color(GRAY, "Observed Runtime"), color(BRIGHT_GREEN, String.format("%.3f", runtimeMs)));
        System.out.println(color(BRIGHT_CYAN + BOLD, DOUBLE_LINE));
    }

    public static void printComplexity(String best, String avg, String worst, String space, double runtimeMs) {
        System.out.println(color(BRIGHT_YELLOW, "--------------------------------- COMPLEXITY -----------------------------------"));
        System.out.printf("  %-20s : Best: %s | Average: %s | Worst: %s%n",
                color(GRAY, "Time Complexity"), color(BRIGHT_WHITE, best), color(BRIGHT_WHITE + BOLD, avg), color(BRIGHT_WHITE, worst));
        System.out.printf("  %-20s : %s%n", color(GRAY, "Auxiliary Space"), color(BRIGHT_WHITE + BOLD, space));
        System.out.printf("  %-20s : %s ms%n", color(GRAY, "Observed Runtime"), color(BRIGHT_GREEN, String.format("%.3f", runtimeMs)));
        System.out.println(color(BRIGHT_CYAN + BOLD, DOUBLE_LINE));
    }

    // -------------------------------------------------------------------------
    // DNA SEQUENCE FORMATTING & COLORING
    // -------------------------------------------------------------------------
    /**
     * Colorizes a DNA nucleotide sequence:
     * A = Bright Green, T = Bright Red, G = Bright Yellow, C = Bright Blue, Other = Gray.
     */
    public static String formatDna(String sequence) {
        if (sequence == null) return "";
        if (!colorsEnabled) return sequence;

        StringBuilder sb = new StringBuilder(sequence.length() * 8);
        for (char ch : sequence.toCharArray()) {
            switch (Character.toUpperCase(ch)) {
                case 'A': sb.append(BRIGHT_GREEN).append(ch).append(RESET); break;
                case 'T': sb.append(BRIGHT_RED).append(ch).append(RESET); break;
                case 'G': sb.append(BRIGHT_YELLOW).append(ch).append(RESET); break;
                case 'C': sb.append(BRIGHT_BLUE).append(ch).append(RESET); break;
                case 'U': sb.append(BRIGHT_RED).append(ch).append(RESET); break;
                default:  sb.append(GRAY).append(ch).append(RESET); break;
            }
        }
        return sb.toString();
    }

    /**
     * Prints a labeled DNA sequence, optionally truncating if too long for the console.
     */
    public static void printDnaField(String label, String sequence, int maxDisplayLen) {
        if (sequence == null) return;
        String displaySeq;
        if (sequence.length() > maxDisplayLen) {
            displaySeq = sequence.substring(0, maxDisplayLen) + "... (" + sequence.length() + " bp total)";
        } else {
            displaySeq = sequence + " (" + sequence.length() + " bp)";
        }
        System.out.printf("  %-20s : %s%n", color(GRAY, label), formatDna(displaySeq));
    }

    public static void printField(String label, Object value) {
        System.out.printf("  %-20s : %s%n", color(GRAY, label), color(BRIGHT_WHITE, String.valueOf(value)));
    }

    public static void printHighlightField(String label, Object value) {
        System.out.printf("  %-20s : %s%n", color(BRIGHT_YELLOW, label), color(BRIGHT_GREEN + BOLD, String.valueOf(value)));
    }

    public static void printError(String message) {
        System.out.println(color(BRIGHT_RED + BOLD, "  [!] ERROR: " + message));
    }

    public static void printWarning(String message) {
        System.out.println(color(BRIGHT_YELLOW + BOLD, "  [*] NOTICE: " + message));
    }

    public static void printSuccess(String message) {
        System.out.println(color(BRIGHT_GREEN + BOLD, "  [+] SUCCESS: " + message));
    }

    // -------------------------------------------------------------------------
    // SAFE INTERACTIVE INPUT READERS
    // -------------------------------------------------------------------------
    public static String readString(Scanner scanner, String prompt, String defaultValue) {
        System.out.print(color(BRIGHT_CYAN + BOLD, prompt) +
                (defaultValue != null && !defaultValue.isEmpty() ? color(GRAY, " [default: " + defaultValue + "]") : "") +
                color(BRIGHT_YELLOW + BOLD, " > ") + RESET);
        if (!scanner.hasNextLine()) {
            return defaultValue != null ? defaultValue : "";
        }
        String line = scanner.nextLine().trim();
        if (line.isEmpty() && defaultValue != null) {
            return defaultValue;
        }
        return line;
    }

    /**
     * Reads and validates a DNA sequence, filtering out whitespace and checking for valid bases.
     */
    public static String readDna(Scanner scanner, String prompt, String defaultSeq) {
        while (true) {
            String input = readString(scanner, prompt, defaultSeq);
            String clean = input.toUpperCase().replaceAll("\\s+", "");
            if (clean.isEmpty()) {
                if (defaultSeq != null) return defaultSeq;
                printError("Sequence cannot be empty. Please enter DNA bases (A, C, G, T).");
                continue;
            }
            if (!clean.matches("[ACGTN]+")) {
                printWarning("Sequence contains non-canonical bases. Standard DNA nucleotides are A, C, G, T. Continuing with input.");
            }
            return clean;
        }
    }

    public static int readInt(Scanner scanner, String prompt, int min, int max, int defaultVal) {
        while (true) {
            System.out.print(color(BRIGHT_CYAN + BOLD, prompt) +
                    color(GRAY, " (" + min + "-" + max + ", default: " + defaultVal + ")") +
                    color(BRIGHT_YELLOW + BOLD, " > ") + RESET);
            if (!scanner.hasNextLine()) {
                return defaultVal;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) return defaultVal;
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) {
                    return val;
                }
                printError("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Please enter a valid whole number.");
            }
        }
    }

    /**
     * Pauses console output so users can review the results before clearing/refreshing.
     */
    public static void pause(Scanner scanner) {
        System.out.print(color(GRAY, "\n  Press [Enter] to return to menu... "));
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }
}
