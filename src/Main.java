import java.util.*;

/**
 * Main.java
 * A Flip-Flop Simulator that demonstrates the behavior of different types of flip-flops:
 * - RS Flip-Flop
 * - D Flip-Flop
 * - JK Flip-Flop
 * - T Flip-Flop
 * 
 * The program allows users to:
 * 1. Select a flip-flop type
 * 2. Specify the number of variables (1-3)
 * 3. Input binary values for the flip-flop
 * 4. View the state transition table
 * 
 * @author CODERZZZ
 */
public class Main {
    // Scanner object for reading user input
    private static Scanner scanner = new Scanner(System.in);

    // Lists to store the state transition table data
    private static List<String> presentStates = new ArrayList<>();  // Current states
    private static List<String> nextStates = new ArrayList<>();     // Next states
    private static List<String> flipFlopInputs = new ArrayList<>(); // Input values

    /**
     * Main entry point of the program.
     * Implements the main program loop that:
     * 1. Displays the menu
     * 2. Gets user input
     * 3. Processes the flip-flop simulation
     * 4. Displays results
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        while (true) {
            int flipFlopType = displayMenuAndGetChoice();
            if (flipFlopType == 5) {
                System.out.println("Exiting program. Goodbye!");
                break;
            }
            int numVars = getNumVariables();
            if (numVars == 0) {
                System.out.println("Going back to menu. Goodbye!");
                continue;
            }
            getTableRows(flipFlopType, numVars);
            printTable(flipFlopType, numVars);
        }
        scanner.close();
    }

    /**
     * Displays the main menu and gets the user's choice of flip-flop type.
     * Validates input to ensure it's a number between 1 and 5.
     * 
     * @return int representing the selected flip-flop type (1-4) or exit (5)
     */
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

    /**
     * Prompts the user to enter the number of variables for the flip-flop.
     * Validates input to ensure it's a number between 1 and 3, or 0 to exit.
     * 
     * @return int representing the number of variables (1-3) or 0 to exit
     */
    private static int getNumVariables() {
        int n;
        do {
            System.out.print("Enter the number of variables (1 to 3) 0 to exit: ");
            if(scanner.hasNextInt()) {
                n = scanner.nextInt();
                if(n==1 || n==2 || n==3) {
                    return n;
                } else if (n==0) {
                    return 0;
                }else {
                    System.out.println("Invalid input. Please enter a number from 1 to 3: ");
                }
            }
            else{
                System.out.println("Invalid input. Please enter a valid integer from 1 to 3: ");
                scanner.next();
            }
        } while (true);
    }

    /**
     * Collects the state transition table data from user input.
     * For each state:
     * 1. Gets the flip-flop input
     * 2. Calculates the next state
     * 3. Updates the table
     * 
     * @param flipFlopType The type of flip-flop (1-4)
     * @param numVars Number of variables in the flip-flop (1-3)
     */
    private static void getTableRows(int flipFlopType, int numVars) {
        presentStates.clear();
        nextStates.clear();
        flipFlopInputs.clear();
        
        System.out.println("Enter flip-flop inputs. Type 'done' to finish input.");
        
        // Start with all zeros as initial state
        String currentState = "0".repeat(numVars);
        
        while (true) {
            // Get flip-flop input from user
            int ffBits = (flipFlopType == 1 || flipFlopType == 3) ? numVars * 2 : numVars;
            String ffInput = getBinaryInput("Flip-flop input for state " + currentState + " (" + ffBits + " bits): " + "(or type done to exit)", ffBits);
            
            if (ffInput.equalsIgnoreCase("done")) {
                break;
            }
            
            // Add current state and input
            presentStates.add(currentState);
            flipFlopInputs.add(ffInput);
            
            // Calculate next state for this row
            String next = calcNextState(flipFlopType, currentState, ffInput, numVars);
            nextStates.add(next);
            
            // Print the table after each input
            System.out.println("\nCurrent State Table:");
            printTable(flipFlopType, numVars);
            System.out.println(); // Add a blank line for better readability
            
            // Use the next state as the present state for next iteration
            currentState = next;
        }
    }

    /**
     * Gets and validates binary input from the user.
     * Ensures the input is the correct length and contains only 0s and 1s.
     * 
     * @param prompt The prompt message to display
     * @param length The required length of the binary input
     * @return String containing the valid binary input or "done"
     */
    private static String getBinaryInput(String prompt, int length) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.next();
            if (s.equalsIgnoreCase("done")) return s;
            if (s.length() == length && s.matches("[01]+")) return s;
            System.out.println("Invalid input. Please enter exactly " + length + " bits (0 or 1). Try again.");
        }
    }

    /**
     * Calculates the next state based on the flip-flop type, current state, and input.
     * Implements the logic for each type of flip-flop:
     * - RS: Reset-Set logic with invalid state handling
     * - D: Direct input copying
     * - JK: Toggle logic with hold state
     * - T: Toggle based on input
     * 
     * @param type The type of flip-flop (1-4)
     * @param present Current state of the flip-flop
     * @param ffInput Input to the flip-flop
     * @param n Number of variables
     * @return String representing the next state
     */
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

    /**
     * Prints the state transition table in a formatted manner.
     * The table includes:
     * - Present state
     * - Next state
     * - Flip-flop inputs
     * 
     * @param flipFlopType The type of flip-flop (1-4)
     * @param numVars Number of variables in the flip-flop (1-3)
     */
    private static void printTable(int flipFlopType, int numVars) {
        // Fixed column widths for consistency
        int stateColWidth = 20;  // Fixed width for state columns
        int inputColWidth = 30;  // Fixed width for input column

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