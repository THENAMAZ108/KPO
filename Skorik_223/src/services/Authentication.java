package services;

import DB.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Authentication {
    private static boolean valueInUserTable(String value, int columnIndex) {
        String table = "SELECT * FROM user";
        try {
            Connection connection = ConnectionManager.open();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(table);
            while (resultSet.next()) {
                if (value.equals(resultSet.getString(columnIndex))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String logOrRegOrAdmin() {
        String input = "";
        while (!input.equals("1") &&
                !input.equals("2") &&
                !input.equals("3") &&
                !input.equals("4")) {
            System.out.println(
                    "Для входа в аккаунт введите: 1\n" +
                    "Для регистрации введите: 2\n" +
                    "Если вы администратор, введите: 3\n" +
                    "Для выхода из программы введите: 4");
            Scanner console = new Scanner(System.in);
            input = console.nextLine();
        }
        return input;
    }

    public static String registration() {
        System.out.println("<| Регистрация аккуанта |>");
        System.out.println("Введите имя пользователя:");

        Scanner console = new Scanner(System.in);
        String userName = console.nextLine();

        while (true) {
            if (userName.isEmpty()) {
                System.out.println("Имя не может быть пустой строкой.");
            } else if (valueInUserTable(userName, 1)){
                System.out.println("Это имя занято. Введите другое имя пользователя:");
            } else break;
            userName = console.nextLine();
        }

        System.out.println("Введите пароль:");

        String userPassword = console.nextLine();

        while (userPassword.isEmpty()) {
            System.out.println("Пароль не может быть пустой строкой.");
            userPassword = console.nextLine();
        }

        System.out.println("Вы успешно зарегистрировали аккаунт.");
        System.out.println("Ваше имя пользователя: '" + userName + "'\n" +
                "Ваш пароль: '" + userPassword + "'");

        String command = "INSERT INTO user VALUES " +
                "(" + "'" + userName + "'" + ", " + "'" + userPassword + "'" + ");";
        try {
            Connection connection = ConnectionManager.open();
            Statement statement = connection.createStatement();
            statement.execute(command);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userName;
    }

    public static String login() {
        try {
            Connection connection = ConnectionManager.open();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM user");
            if (resultSet.next()) {
                int rowCount = resultSet.getInt(1);
                if (rowCount == 0) {
                    System.out.println("На данный момент в системе не " +
                            "зарегистрирован ни один аккаунт.\n" +
                            "Зарегистрируйтесь.");
                    registration();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("<| Вход в аккаунт |>");
        System.out.println("Введите имя пользователя:");

        Scanner console = new Scanner(System.in);
        String userName = console.nextLine();

        while (!valueInUserTable(userName, 1)) {
            System.out.println("Пользователя с таким именем не существует.\n" +
                    "Введите существующее имя пользователя:");
            userName = console.nextLine();
        }

        System.out.println("Введите пароль:");

        String userPassword = console.nextLine();
        while (!valueInUserTable(userPassword, 2)) {
            System.out.println("Неправильный пароль.\n" +
                    "Введите правильный пароль:");
            userPassword = console.nextLine();
        }
        System.out.println("Вы успешно вошли в аккаунт '" + userName + "'.");
        return userName;
    }

    public static void adminLogin() {
        System.out.println("<| Вход администратора |>");
        System.out.println("Скажи друг и войди:");

        Scanner console = new Scanner(System.in);
        String code = console.nextLine();

        while (!code.equals("друг")) {
            System.out.println("Неправильный код доступа.\n" +
                    "Скажи друг и войди:");
            code = console.nextLine();
        }
        System.out.println("Вы успешно вошли как администратор.");
    }
}
