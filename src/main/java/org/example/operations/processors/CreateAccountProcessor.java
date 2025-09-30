package org.example.operations.processors;

import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;
import org.example.user.UserService;

import java.util.Scanner;

public class CreateAccountProcessor implements OperationCommandProcessor {
    private final UserService userService;
    private final AccountService accountService;
    private final Scanner scanner;

    public CreateAccountProcessor(UserService userService, AccountService accountService, Scanner scanner) {
        this.userService = userService;
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void processOperation() {
        System.out.println("Enter user id to create account: ");
        int userID = Integer.parseInt(scanner.nextLine());
        var user = userService.findUserById(userID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id " + userID));

        var account = accountService.createAccount(user);
        user.getAccountList().add(account);

        System.out.printf("Account created with id: %s for user %s %n", account.getId(), user.getLogin());

    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}
