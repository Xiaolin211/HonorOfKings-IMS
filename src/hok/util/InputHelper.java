package hok.util;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Safe console input handling utility.
 * All methods loop until valid input is received — no exceptions escape to the caller.
 * Uses a single shared Scanner instance with NoSuchElementException guards.
 */
public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Reads a line safely. Returns empty string if input is exhausted.
     */
    private static String safeNextLine() {
        try {
            if (scanner.hasNextLine()) {
                return scanner.nextLine().trim();
            }
            // Input stream exhausted (e.g., piped input ended)
            System.out.println("\n[Input stream ended. Exiting program.]");
            System.exit(0);
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("\n[Scanner error. Exiting program.]");
            System.exit(1);
        }
        return "";
    }

    /**
     * Reads an integer from the console. Loops until valid input.
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine();
            if (line.isEmpty()) {
                System.out.println("No input available. Try again.");
                continue;
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    /**
     * Reads an integer within [min, max]. Loops until valid.
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Please enter a number between " + min + " and " + max + ".");
        }
    }

    /**
     * Reads a double from the console. Loops until valid input.
     */
    public static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine();
            if (line.isEmpty()) {
                System.out.println("No input available. Try again.");
                continue;
            }
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Reads a non-empty string from the console.
     */
    public static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = safeNextLine();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /**
     * Reads a yes/no response. Returns true for yes, false for no.
     */
    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String line = safeNextLine().toLowerCase();
            if (line.equals("y") || line.equals("yes")) {
                return true;
            }
            if (line.equals("n") || line.equals("no")) {
                return false;
            }
            System.out.println("Please enter 'y' or 'n'.");
        }
    }

    /**
     * Waits for the user to press Enter before continuing.
     */
    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        safeNextLine();
    }
}
