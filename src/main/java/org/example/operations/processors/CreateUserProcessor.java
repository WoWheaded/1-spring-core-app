package org.example.operations.processors;

import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;
import org.example.user.User;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreateUserProcessor implements OperationCommandProcessor {

    private final UserService userService;
    private final Scanner scanner;

    public CreateUserProcessor(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter user login: ");
        String userLogin = scanner.nextLine();
        User user = userService.createUser(userLogin);
        System.out.println("User created: " + user.toString());

    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.USER_CREATE;
    }
}
