import java.util.*;

/**
 * Main.java
 * Entry point for the Flip-Flop Simulator.
 * This class handles the menu selection, user input, and simulation logic for different flip-flop types.
 */
public class Main {
    private static Scanner scanner = new Scanner(System.in);

    // Data for the table
    private static List<String> presentStates = new ArrayList<>();
    private static List<String> nextStates = new ArrayList<>();
    private static List<String> flipFlopInputs = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            int flipFlopType = displayMenuAndGetChoice();
            if (flipFlopType == 5) {
                System.out.println("Exiting program. Goodbye!");
                break;
            }
            int numVars = getNumVariables();
            getTableRows(flipFlopType, numVars);
            printTable(flipFlopType, numVars);
        }
        scanner.close();
    }

    // Display menu and get flip-flop type
    private static int displayMenuAndGetChoice() {
        System.out.println("\n--- Flip-Flop Simulator ---");
        System.out.println("1. RS Flip-Flop");
        System.out.println("2. D Flip-Flop");
        System.out.println("3. JK Flip-Flop");
        System.out.println("4. T Flip-Flop");
        System.out.println("5. Exit");
        System.out.print("Select flip-flop type: ");
        int choice;
        while (true) {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice >= 1 && choice <= 5) break;
            } else {
                scanner.next();
            }
            System.out.print("Invalid input. Please enter a number from 1 to 5: ");
        }
        return choice;
    }

    // Ask for number of variables (1-3)
    private static int getNumVariables() {
        int n;
        do {
            System.out.print("Enter the number of variables (1 to 3): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
            n = scanner.nextInt();
        } while (n < 1 || n > 3);
        return n;
    }

    // Get all table rows from user
    private static void getTableRows(int flipFlopType, int numVars) {
        presentStates.clear();
        nextStates.clear();
        flipFlopInputs.clear();
        System.out.println("Enter rows. Type 'done' as present state to finish input.");
        while (true) {
            String present = getBinaryInput("Present state (" + numVars + " bits): ", numVars);
            if (present.equalsIgnoreCase("done")) break;
            String ffInput;
            int ffBits = (flipFlopType == 1 || flipFlopType == 3) ? numVars * 2 : numVars;
            ffInput = getBinaryInput("Flip-flop input (" + ffBits + " bits): ", ffBits);
            presentStates.add(present);
            flipFlopInputs.add(ffInput);
            // Calculate next state for this row
            String next = calcNextState(flipFlopType, present, ffInput, numVars);
            nextStates.add(next);
        }
    }

    // Get a binary string input of required length
    private static String getBinaryInput(String prompt, int length) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.next();
            if (s.equalsIgnoreCase("done")) return s;
            if (s.length() == length && s.matches("[01]+")) return s;
            System.out.println("Invalid input. Please enter exactly " + length + " bits (0 or 1). Try again.");
        }
    }

    // Calculate next state for a row, given flip-flop type, present state, and flip-flop input
    private static String calcNextState(int type, String present, String ffInput, int n) {
        StringBuilder next = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char q = present.charAt(i);
            switch (type) {
                case 1: // RS
                    int r = ffInput.charAt(i * 2) - '0';
                    int s = ffInput.charAt(i * 2 + 1) - '0';
                    if (r == 0 && s == 0) next.append(q); // hold
                    else if (r == 0 && s == 1) next.append('0'); // reset
                    else if (r == 1 && s == 0) next.append('1'); // set
                    else next.append('X'); // invalid
                    break;
                case 2: // D
                    next.append(ffInput.charAt(i));
                    break;
                case 3: // JK
                    int j = ffInput.charAt(i * 2) - '0';
                    int k = ffInput.charAt(i * 2 + 1) - '0';
                    if (j == 0 && k == 0) next.append(q); // hold
                    else if (j == 0 && k == 1) next.append('0'); // reset
                    else if (j == 1 && k == 0) next.append('1'); // set
                    else next.append(q == '0' ? '1' : '0'); // toggle
                    break;
                case 4: // T
                    int t = ffInput.charAt(i) - '0';
                    next.append((char)(((q - '0') ^ t) + '0'));
                    break;
            }
        }
        return next.toString();
    }

    // Print the table in the required format
    private static void printTable(int flipFlopType, int numVars) {
        // Calculate column widths
        int stateColWidth = numVars * 3 + 2; // 3 chars per bit + 2 for padding
        int inputBits = (flipFlopType == 1 || flipFlopType == 3) ? numVars * 2 : numVars;
        int inputColWidth = inputBits * 3 + 2;

        String sep = "+" + "-".repeat(stateColWidth) + "+" + "-".repeat(stateColWidth) + "+" + "-".repeat(inputColWidth) + "+";

        // Header
        System.out.println(sep);
        System.out.printf("|%-" + stateColWidth + "s|%-" + stateColWidth + "s|%-" + inputColWidth + "s|%n",
                " Present State", " Next State", " Flip-flop Inputs");
        System.out.println(sep);

        // Sub-header: variable names
        StringBuilder presHeader = new StringBuilder();
        StringBuilder nextHeader = new StringBuilder();
        StringBuilder inputHeader = new StringBuilder();

        for (int i = numVars - 1; i >= 0; i--) {
            presHeader.append(String.format("A%d ", i));
            nextHeader.append(String.format("A%d ", i));
        }
        presHeader.append(" ".repeat(stateColWidth - presHeader.length()));
        nextHeader.append(" ".repeat(stateColWidth - nextHeader.length()));

        if (flipFlopType == 1) { // RS
            for (int i = numVars - 1; i >= 0; i--) inputHeader.append(String.format("R%d S%d ", i, i));
        } else if (flipFlopType == 2) { // D
            for (int i = numVars - 1; i >= 0; i--) inputHeader.append(String.format("D%d ", i));
        } else if (flipFlopType == 3) { // JK
            for (int i = numVars - 1; i >= 0; i--) inputHeader.append(String.format("J%d K%d ", i, i));
        } else if (flipFlopType == 4) { // T
            for (int i = numVars - 1; i >= 0; i--) inputHeader.append(String.format("T%d ", i));
        }
        inputHeader.append(" ".repeat(inputColWidth - inputHeader.length()));

        System.out.printf("|%-" + stateColWidth + "s|%-" + stateColWidth + "s|%-" + inputColWidth + "s|%n",
                presHeader, nextHeader, inputHeader);
        System.out.println(sep);

        // Rows
        for (int row = 0; row < presentStates.size(); row++) {
            // Present State
            StringBuilder pres = new StringBuilder();
            for (int i = 0; i < numVars; i++)
                pres.append(" ").append(presentStates.get(row).charAt(i)).append(" ");
            pres.append(" ".repeat(stateColWidth - pres.length()));

            // Next State
            StringBuilder next = new StringBuilder();
            for (int i = 0; i < numVars; i++)
                next.append(" ").append(nextStates.get(row).charAt(i)).append(" ");
            next.append(" ".repeat(stateColWidth - next.length()));

            // Flip-flop Inputs
            StringBuilder ff = new StringBuilder();
            if (flipFlopType == 1 || flipFlopType == 3) { // RS or JK
                for (int i = 0; i < numVars; i++) {
                    ff.append(" ").append(flipFlopInputs.get(row).charAt(i * 2)).append(" ");
                    ff.append(" ").append(flipFlopInputs.get(row).charAt(i * 2 + 1)).append(" ");
                }
            } else { // D or T
                for (int i = 0; i < numVars; i++)
                    ff.append(" ").append(flipFlopInputs.get(row).charAt(i)).append(" ");
            }
            ff.append(" ".repeat(inputColWidth - ff.length()));

            System.out.printf("|%-" + stateColWidth + "s|%-" + stateColWidth + "s|%-" + inputColWidth + "s|%n",
                    pres, next, ff);
        }
        System.out.println(sep);
    }
}