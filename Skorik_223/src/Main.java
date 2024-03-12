import static services.AdminMenu.*;
import static services.Authentication.*;
import static services.CustomerMenu.*;

public class Main {
    public static void main(String[] args) {
        boolean userIsAdmin = false;
        String userName = "";
        String input = logOrRegOrAdmin();
        switch (input) {
            case "1":
                userName = login();
                break;
            case "2":
                userName = registration();
                break;
            case "3":
                adminLogin();
                userIsAdmin = true;
                break;
            default:
                System.out.println("До свидания!");
                return;
        }

        if (userIsAdmin) {
            runAdminMenu();
        } else {
            runCustomerMenu(userName);
        }
    }
}