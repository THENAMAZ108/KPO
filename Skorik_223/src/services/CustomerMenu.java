package services;

import DB.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;

public class CustomerMenu {

    static HashMap<String, Integer> dishesToOrder = new HashMap<String, Integer>();
    public static void runCustomerMenu(String user) {
        String userName = user;
        while (true) {
            System.out.println(
                    "Для добавления блюда в заказ введите: 1\n" +
                            "Для составления заказа введите: 2\n" +
                            "Для завершения работы программы введите: 3");

            Scanner console = new Scanner(System.in);
            String input = console.nextLine();

            boolean stop = false;

            switch (input) {
                case "1":
                    System.out.println("<| Добавление блюда |>");
                    addDishToOrder();
                    break;
                case "2":
                    System.out.println("<| Создание заказа |>");
                    try {
                        createOrder(userName);
                        System.out.println("До свидания!");
                        stop = true;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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

    private static void showMenu() {
        String table = "SELECT * FROM menu";
        try {
            Connection connection = ConnectionManager.open();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(table);
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + " - " +
                        resultSet.getInt(2) + "руб.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addDishToOrder() {
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

        System.out.println("Выберите блюдо из списка, введя его название:");
        showMenu();

        String dishName;
        Scanner console = new Scanner(System.in);
        dishName = console.nextLine();

        while (!valueInMenuTable(dishName)) {
            showMenu();
            System.out.println("Блюда с таким названием не существует.\n" +
                    "Введите существующее название блюда:");
            dishName = console.nextLine();
        }
        try {
            Connection connection = ConnectionManager.open();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM menu");
            while (resultSet.next()) {
                if (resultSet.getString(1).equals(dishName)) {
                    if (resultSet.getInt(4) == 0) {
                        System.out.println("К сожалению это блюдо закончилось.");
                        break;
                    } else {
                        System.out.println("Вы добавили '" + resultSet.getString(1) + "'");
                        dishesToOrder.put(resultSet.getString(1), resultSet.getInt(3));
                        int updatedAmount = resultSet.getInt(4) - 1;
                        String command = "UPDATE menu SET amount = " + updatedAmount +
                                " WHERE dishName = '" + resultSet.getString(1) +"';";
                        statement.execute(command);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createOrder(String user) throws InterruptedException {
        String userName = user;
        if (dishesToOrder.isEmpty()) {
            System.out.println("Вы пока не добавили ни одного блюда в заказ.");
        } else {
            System.out.println("Заказ принят...");
            System.out.println("Ваш заказ состоит из следующих блюд:");
            System.out.println(dishesToOrder.keySet());

            Thread.sleep(5000);
            System.out.println("Заказ начали готовить...");

            int timeToCook = dishesToOrder.values().stream().mapToInt(Integer::intValue).sum();
            Thread.sleep(timeToCook * 1000L);

            System.out.println("Заказ готов. Приятного аппептита!");

            Scanner console = new Scanner(System.in);
            String grade = "";
            String feedback;
            System.out.println("Оцените, пожалуйста, заказ от 1 до 5:");
            grade = console.nextLine();

            while (!grade.equals("1") &&
                    !grade.equals("2") &&
                    !grade.equals("3") &&
                    !grade.equals("4") &&
                    !grade.equals("5")) {
                System.out.println("Введите число от 1 до 5:");
                grade = console.nextLine();
            }

            System.out.println("Напишите, пожалуйста, отзыв:");
            feedback = console.nextLine();
            while (feedback.isEmpty() || feedback.length() > 255) {
                System.out.println("Количество символов в отзыве должно быть в интервале [1, 255]");
                feedback = console.nextLine();
            }

            String command = "INSERT INTO orders VALUES " +
                    "(" + "'" + userName + "'" + ", " +
                    "'" + dishesToOrder.keySet() + "'" + ", "
                    + "'" + grade + "'" + ", "
                    + "'" + feedback + "'" +
                    ");";
            try {
                Connection connection = ConnectionManager.open();
                Statement statement = connection.createStatement();
                statement.execute(command);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
