package services;

import DB.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AdminMenu {

    public static void runAdminMenu() {
        while (true) {
            System.out.println(
                    "Для добавления блюда в меню введите: 1\n" +
                    "Для удаления блюда из меню введите: 2\n" +
                    "Для завершения работы програмы введите: 3");

            Scanner console = new Scanner(System.in);
            String input = console.nextLine();

            boolean stop = false;

            switch (input) {
                case "1":
                    System.out.println("<| Добавление блюда |>");
                    addDishToMenuTable();
                    break;
                case "2":
                    System.out.println("<| Удаление блюда |>");
                    deleteDishFromMenuTable();
                    break;
                case "3":
                    System.out.println("До свидания!");
                    stop = true;
                    break;
                default:
                    System.out.println("Такой опции не существует.\n" +
                            "Введите число от 1 до 3.");
            }
            if (stop) {
                break;
            }
        }
    }

    private static boolean isDigit(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean valueInMenuTable(String value) {
        String table = "SELECT * FROM menu";
        try {
            Connection connection = ConnectionManager.open();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(table);
            while (resultSet.next()) {
                if (value.equals(resultSet.getString(1))) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void addDishToMenuTable() {
        System.out.println("Введите название блюда:");

        Scanner console = new Scanner(System.in);
        String dishName = console.nextLine();

        while (true) {
            if (dishName.isEmpty()) {
                System.out.println("Название не может быть пустой строкой.");
            } else if (valueInMenuTable(dishName)){
                System.out.println("Это блюдо уже включено в меню.\nВведите другое название блюда:");
            } else break;
            dishName = console.nextLine();
        }

        System.out.println("Введите цену блюда:");
        String price = console.nextLine();
        int dishPrice;
        while (true) {
            if (isDigit(price)) {
                dishPrice = Integer.parseInt(price);
                if (dishPrice <= 0) {
                    System.out.println("Цена не может быть <= 0.\n" +
                            "Введите положительное число:");
                } else break;
            } else {
                System.out.println("Цена должна быть числом.\n" +
                        "Введите положительное число:");
            }
            price = console.nextLine();
        }

        System.out.println("Введите время приготовления в секундах:");
        String time = console.nextLine();
        int dishTime;
        while (true) {
            if (isDigit(time)) {
                dishTime = Integer.parseInt(time);
                if (dishTime <= 0) {
                    System.out.println("Время приготовления не может быть <= 0.\n" +
                            "Введите положительное число:");
                } else break;
            } else {
                System.out.println("Время приготовления должна быть числом.\n" +
                        "Введите положительное число:");
            }
            time = console.nextLine();
        }

        System.out.println("Введите количество:");
        String amount = console.nextLine();
        int dishAmount;
        while (true) {
            if (isDigit(amount)) {
                dishAmount = Integer.parseInt(amount);
                if (dishAmount <= 0) {
                    System.out.println("Количество не может быть <= 0.\n" +
                            "Введите положительное число:");
                } else break;
            } else {
                System.out.println("Количество должно быть числом.\n" +
                        "Введите положительное число:");
            }
            amount = console.nextLine();
        }

        String command = "INSERT INTO menu VALUES " +
                "(" +
                "'" + dishName + "'" + ", "
                + "'" + dishPrice + "'" + ", "
                + "'" + dishTime + "'" + ", "
                + "'" + dishAmount + "'" +
                ");";
        try {
            Connection connection = ConnectionManager.open();
            Statement statement = connection.createStatement();
            statement.execute(command);
            System.out.println("Блюдо '" + dishName + "' добавлено успешно.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDishFromMenuTable() {
        try {
            Connection connection = ConnectionManager.open();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM menu");
            if (resultSet.next()) {
                int rowCount = resultSet.getInt(1);
                if (rowCount == 0) {
                    System.out.println("На данный момент в меню нет " +
                            "ни одного блюда.");
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Введите название блюда:");

        Scanner console = new Scanner(System.in);
        String dishName = console.nextLine();

        while (!valueInMenuTable(dishName)) {
            System.out.println("Блюда с таким названием не существует.\n" +
                    "Введите существующее название блюда:");
            dishName = console.nextLine();
        }

        String command = "DELETE FROM menu WHERE dishName = " + "'" + dishName + "';";

        try {
            Connection connection = ConnectionManager.open();
            Statement statement = connection.createStatement();
            statement.execute(command);
            System.out.println("Блюдо '" + dishName + "' удалено успешно.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
