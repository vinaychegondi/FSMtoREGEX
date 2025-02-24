//Programming Assignment 02
//Course: CS5313
//Name: Vinay Chegondi
//CWID: A20446822

import java.util.*;
import java.io.*;

public class FSMTOREGEX {
    public static void main(String[] args) {

        // System.out.println("fsm to regex");
        if (args.length != 1) {
            System.out.println("Usage: java FSMTOREGEX <dfsm specification file> ");
            return;
        }
        // file names for the arguments
        String inputFileName = args[0];
        // String outputFileName = args[1];

        // System.out.println("output file name: " + outputFileName);
        System.out.println("input file name: " + inputFileName);

        String[] alphabet = null;
        Set<Integer> validStates = new HashSet<>();
        int[][] transitionTable = null;
        // int[][] transitionTable1 = null;

        Set<Integer> acceptingStates = new HashSet<>();

        try {

            BufferedReader input = new BufferedReader(new FileReader(inputFileName));

            // read and print the alphabet line
            String line = input.readLine();
            // System.out.println("alphabets:");
            // System.out.println(line);

            // check if file is empty
            if (line == null || line.trim().isEmpty()) {
                System.out.println("Error: DFSM file is empty or missing the alphabet");
                return;
            }
            alphabet = line.trim().split("\\s+");

            // check if all alphabets are valid single characters
            for (String symbol : alphabet) {
                if (symbol.length() != 1 || !Character.isLetter(symbol.charAt(0))) {
                    System.out.println("Error: invalid alphabet symbol:'" + symbol + "'");
                    return;
                }
            }

            line = input.readLine();
            if (line != null && !line.trim().isEmpty()) {
                System.out.println("Error: Expected an empty line after the alphabet");
                return;
            }

            // read and print the transition table
            List<int[]> transitions = new ArrayList<>();
            Set<Integer> reachableStates = new HashSet<>();
            reachableStates.add(1); // Assuming state 1 is the start state

            while ((line = input.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.trim().split("\\s+");
                int[] transitionRow = new int[parts.length];

                for (int i = 0; i < parts.length; i++) {
                    try {
                        int value = Integer.parseInt(parts[i]);
                        if (value < 0) {
                            System.out.println("Error: transition value '" + value + "' cannot be negative");
                            return;
                        }
                        transitionRow[i] = value;
                        // If this transition leads to a new state, mark it as reachable
                        reachableStates.add(value);

                        validStates.add(value); // adding valid state to set
                    } catch (NumberFormatException e) {
                        System.out.println("Error: transition value '" + parts[i] + "' is not a valid integer");
                        return;
                    }
                }
                transitions.add(transitionRow);
            }

            // Check if the transition table is empty
            if (transitions.isEmpty()) {
                System.out.println("Error: Transition table is empty");
                return;
            }
            transitionTable = transitions.toArray(new int[transitions.size()][]);

            for (int[] row : transitionTable) {
                if (row.length != alphabet.length) {
                    System.out.println("Error: Transition table row length does not match alphabet length");
                    return;
                }
            }

            // Print the given transition table

            // System.out.println("input transition table");
            // for (int[] row : transitions) {
            // for (int val : row) {
            // System.out.print(val + " ");
            // }
            // System.out.println();
            // }

            // read and print accepting state.
            if ((line = input.readLine()) != null && !line.trim().isEmpty()) {
                // Check for leading spaces
                if (line.startsWith(" ")) {
                    System.out.println("Error: Accepting states line contains leading spaces");
                    return;
                }

                String[] states = line.trim().split("\\s+");
                for (String state : states) {
                    try {
                        int stateNumber = Integer.parseInt(state);

                        // Only add to accepting states if it is reachable
                        if (reachableStates.contains(stateNumber)) {
                            acceptingStates.add(stateNumber);
                        } else {
                            // System.out.println("Ignoring unreachable accepting state: " + stateNumber);
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Error: Accepting state '" + state + "' is not an integer");
                        return;
                    }
                }

                // Print the accepting states
                // System.err.println("accepting state");
                // for (Integer state : acceptingStates) {
                // System.out.print(state + " ");
                // }

                // System.out.println(); // for new line after the states
            } else {
                System.out.println("Error: Accepting states line is empty");
            }
            // Now eliminate unreachable states from the transition table
            // System.out.println("Reachable states: " + reachableStates);
            // List<int[]> filteredTransitions = new ArrayList<>();
            // for (int i = 0; i < transitionTable.length; i++) {
            // if (reachableStates.contains(i + 1)) { // Assuming state numbering starts
            // from 1
            // filteredTransitions.add(transitionTable[i]);
            // }
            // }
            // Convert filtered transitions back into an array
            // transitionTable1 = filteredTransitions.toArray(new
            // int[filteredTransitions.size()][]);

            // Expand the transition table to include new start and accept states
            // The new table will have 2 additional states: a new start state and a new
            // accept state
            int numStates = transitionTable.length + 2;
            String[][] newTransitionTable = new String[numStates][numStates];

            // Initialize new transition table with 'phi'
            // System.out.println("new transition table with phi:");
            for (int i = 0; i < numStates; i++) {
                Arrays.fill(newTransitionTable[i], "phi");
            }

            // Print the new transition table after initialization
            // for (int i = 0; i < numStates; i++) {
            // System.out.println(Arrays.toString(newTransitionTable[i]));
            // }

            // Populate the new transition table with transitions from the old table
            for (int rowIn = 0; rowIn < transitionTable.length; rowIn++) {
                for (int i = 0; i < transitionTable[rowIn].length; i++) {
                    int sourceState = rowIn + 1;
                    int targetState = transitionTable[rowIn][i];
                    // If a transition already exists, add the new symbol with '+'
                    if (!newTransitionTable[sourceState][targetState].equals("phi")) {
                        newTransitionTable[sourceState][targetState] += "+" + alphabet[i];
                    } else {
                        newTransitionTable[sourceState][targetState] = alphabet[i];
                    }
                }
            }
            // Print the populated new transition table
            // System.out.println("Transition Table with phi and alphabets:");
            // for (int i = 0; i < newTransitionTable.length; i++) {
            // System.out.println(Arrays.toString(newTransitionTable[i]));
            // }

            // Adding epsilon transitions for the start and final states
            newTransitionTable[0][1] = "eps";
            for (int state : acceptingStates) {
                newTransitionTable[state][numStates - 1] = "eps";
            }

            // Print the new transition table with epsilons
            // System.out.println("Transition Table with epsilons:");
            // for (String[] row : newTransitionTable) {
            // for (String val : row) {
            // System.out.print(val + " ");
            // }
            // System.out.println();
            // }

            // Eliminate states to generate the regex
            List<Integer> statesToEliminate = new ArrayList<>();
            for (int i = 1; i < numStates - 1; i++) {
                statesToEliminate.add(i);
            }
            // Print the states to eliminate/rip
            // System.out.println("States to eliminate: " + statesToEliminate);

            for (int eliminateState : statesToEliminate) {
                List<Integer> statesToKeep = new ArrayList<>();
                for (int i = 0; i < numStates; i++) {
                    if (i != eliminateState) {
                        statesToKeep.add(i);
                    }
                }
                // print start and final states
                // System.out.println("States to Keep:" + statesToKeep);

                List<Integer> incoming = new ArrayList<>();
                List<Integer> outgoing = new ArrayList<>();
                for (int eachRow : statesToKeep) {
                    // checking if there is transition from currentstate to elimainateState and not
                    // empty,
                    // then add eachrow ton incoming list
                    if (!newTransitionTable[eachRow][eliminateState].equals("phi")) {
                        incoming.add(eachRow);
                    }
                    // checking if there is transition from eliminateState to currentState and not
                    // empty,
                    // then add eachrow to outgoing list
                    if (!newTransitionTable[eliminateState][eachRow].equals("phi")) {
                        outgoing.add(eachRow);
                    }
                }

                for (int incomingState : incoming) {
                    for (int outgoingState : outgoing) {
                        // Initializing a StringBuilder to build the regular expression for R'(p, q)
                        StringBuilder regex = new StringBuilder("(");

                        // R(p, q): Adding the existing transition from incomingState to outgoingState
                        // if it is not phi
                        if (!newTransitionTable[incomingState][outgoingState].equals("phi")) {
                            regex.append(newTransitionTable[incomingState][outgoingState]).append("+");
                        }
                        // R(p, rip): Adding the transition from incomingState to eliminateState if it
                        // is
                        // not phi
                        if (!newTransitionTable[incomingState][eliminateState].equals("phi")) {
                            regex.append(newTransitionTable[incomingState][eliminateState]).append(".");
                        }
                        // R(rip, rip)*: Add the selfloop on eliminateState with a Kleene star (*) if
                        // it is not phi

                        if (!newTransitionTable[eliminateState][eliminateState].equals("phi")) {
                            regex.append("(").append(newTransitionTable[eliminateState][eliminateState]).append(")*.");
                        }
                        // R(rip, q): Add the transition from eliminateState to outgoingState if it is
                        // not phi

                        if (!newTransitionTable[eliminateState][outgoingState].equals("phi")) {
                            regex.append(newTransitionTable[eliminateState][outgoingState]);
                        }

                        regex.append(")");
                        // Updating the transition from incomingState to outgoingState with the newly
                        // regex

                        newTransitionTable[incomingState][outgoingState] = regex.toString();
                    }
                }
            }

            // Set eliminated state entries to 'phi'
            for (int eliminateState : statesToEliminate) {
                for (int rowIn = 0; rowIn < numStates; rowIn++) {
                    newTransitionTable[rowIn][eliminateState] = "phi";
                }
                Arrays.fill(newTransitionTable[eliminateState], "phi");
            }

            // Print the final regular expression
            String generatedRegex = newTransitionTable[0][numStates - 1];
            System.out.println("Generated Regular Expression: " + generatedRegex);

            // Minimize the regular expression
            // String minimizedRegex = minimizeRegex(generatedRegex);
            // System.out.println("Minimized Regular Expression: " + minimizedRegex);

        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found");
        } catch (IOException e) {
            System.out.println("Error: I/O error");
        }
    }

    // public static String minimizeRegex(String regex) {

    // return regex;

    // }
}
