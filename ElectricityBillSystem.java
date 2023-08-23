package jdbc_maven_Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Customer {
    private int id;
    private String name;
    private int unitsConsumed;

    public Customer(int id, String name, int unitsConsumed) {
        this.id = id;
        this.name = name;
        this.unitsConsumed = unitsConsumed;
    }

    public String getName() {
        return name;
    }

    public int getUnitsConsumed() {
        return unitsConsumed;
    }

    public double calculateBill() {
        double ratePerUnit = 5.0;  
        return unitsConsumed * ratePerUnit;
    }
}

public class ElectricityBillSystem {
    private static List<Customer> customers = new ArrayList();
    private static Connection connection;

    public static void main(String[] args) {
        initializeDatabase();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Customer");
            System.out.println("2. Generate Bills");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addCustomer(scanner);
                    break;
                case 2:
                    generateBills();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    cleanup();
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void initializeDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/electricity?user=root\r\n"
            		+ "password=root");
            Statement statement = connection.createStatement();
            String createTableQuery = "CREATE TABLE IF NOT EXISTS customers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "units_consumed INTEGER)";
            statement.executeUpdate(createTableQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addCustomer(Scanner scanner) {
        System.out.print("Enter customer name: ");
        String name = scanner.next();

        System.out.print("Enter units consumed: ");
        int unitsConsumed = scanner.nextInt();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO customers (name, units_consumed) VALUES (?, ?)");
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, unitsConsumed);
            preparedStatement.executeUpdate();

            System.out.println("Customer added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void generateBills() {
        System.out.println("Generating Bills");
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int unitsConsumed = resultSet.getInt("units_consumed");

                Customer customer = new Customer(id, name, unitsConsumed);
                customers.add(customer);

                double billAmount = customer.calculateBill();
                System.out.println("Customer: " + customer.getName());
                System.out.println("Units Consumed: " + customer.getUnitsConsumed());
                System.out.println("Bill Amount: $" + billAmount);
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void cleanup() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

