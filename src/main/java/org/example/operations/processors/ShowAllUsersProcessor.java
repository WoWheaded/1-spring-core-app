package org.example.operations.processors;

import org.example.operations.ConsoleOperationType;
import org.example.operations.OperationCommandProcessor;
import org.example.user.User;
import org.example.user.UserService;

import java.util.List;

public class ShowAllUsersProcessor implements OperationCommandProcessor {

    private final UserService userService;

    public ShowAllUsersProcessor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        List<User> users = userService.findAllUsers();
        System.out.println("list of all users found: ");
        users.forEach(System.out::println);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.SHOW_ALL_USERS;
    }
}
