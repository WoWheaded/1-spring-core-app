package org.example;

import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;

import java.util.Map;
import java.util.Scanner;

public class OperationConsoleListener {

    private final Scanner scanner;
    private final Map<ConsoleOperationType, OperationCommandProcessor> processMap;


    public OperationConsoleListener(
            Scanner scanner,
            Map<ConsoleOperationType, OperationCommandProcessor> processMap) {
        this.scanner = scanner;
        this.processMap = processMap;
    }

    public void listenUpdates() {
        while (true) {
            var operationType = listenNextOperation();
            processNextOperation(operationType);
        }
    }

    public void start() {
        System.out.println("Console listener started");
    }

    public void endListen() {
        System.out.println("Console listener end listen");
    }

    private ConsoleOperationType listenNextOperation() {
        System.out.println("\nPLease type next operations: ");
        printAllAvailableOperations();
        System.out.println();
        while (true) {
            var operationType = scanner.nextLine();
            try {
                return ConsoleOperationType.valueOf(operationType.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Mo such command found");
            }
        }
    }

    private void printAllAvailableOperations() {
        processMap
                .keySet().
                forEach(System.out::println);
    }

    private void processNextOperation(ConsoleOperationType operation) {
        try {
            var processor = processMap.get(operation);
            processor.processOperation();
        } catch (Exception e) {
            System.out.printf("Error executing current operation: %s", e.getMessage());
        }
    }
}
