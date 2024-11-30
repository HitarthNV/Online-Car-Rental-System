import java.sql.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat; 
import java.util.Date;

class CarRentalSystem  {  
    static Connection con;
    static Scanner sc; 

     public static  boolean Login(String username, String password) {
    try {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement preparedStatement = con.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        
        ResultSet resultSet = preparedStatement.executeQuery();
        
        if(resultSet.next()) {
            System.out.println("congratulations your account exists!");
            
            return true; // User exists
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    System.out.println(" Sorry your account does not exists");
    return false;
        // Invalid username or password
}


 public  static void createAccount(Connection connection, Scanner sc) throws SQLException {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        // Insert the new user into the database
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Account created successfully.");
            } else {
                System.out.println("Account creation failed. Please try again.");
            }
        }
        System.out.println("redirecting to login method");
        Login(username, password);
    }




    public static void displayAllCars(Connection connection) throws SQLException {
        // Retrieve and display all cars from the "cars" table
        String query = "SELECT * FROM cars";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                System.out.println("Car ID: " + resultSet.getInt("car_id"));
                System.out.println("Company: " + resultSet.getString("company_name"));
                System.out.println("Model: " + resultSet.getString("model_name"));
                System.out.println("Colour: " + resultSet.getString("colour"));
                System.out.println("Number Plate: " + resultSet.getString("number_plate"));
                System.out.println("Available: " + resultSet.getString("available"));
                System.out.println("Per day Rent price :"+resultSet.getInt("rent_price"));
                System.out.println();
            }
        }
    }


     public static void displayAvailableCars(Connection connection) throws SQLException {
        // Retrieve and display available cars from the "cars" table
        String query = "SELECT * FROM cars WHERE available = 'yes'";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                System.out.println("Car ID: " + resultSet.getInt("car_id"));
                System.out.println("Company: " + resultSet.getString("company_name"));
                System.out.println("Model: " + resultSet.getString("model_name"));
                System.out.println("Colour: " + resultSet.getString("colour"));
                System.out.println("Number Plate: " + resultSet.getString("number_plate"));
                System.out.println("Per day Rent price :"+resultSet.getInt("rent_price"));
                System.out.println();
            }
        }
    }


private static void bookCar(Connection connection, Scanner scanner, int userId) throws SQLException {
        System.out.print("Enter the Car ID you want to book: ");
        int carId = scanner.nextInt();

        // Check if the car is available
        String availabilityQuery = "SELECT * FROM cars WHERE car_id = ? AND available = 'yes'";
        try (PreparedStatement availabilityStatement = connection.prepareStatement(availabilityQuery)) {
            availabilityStatement.setInt(1, carId);
            ResultSet availabilityResultSet = availabilityStatement.executeQuery();

            if (availabilityResultSet.next()) {
                // Car is available, proceed with booking
                double rentalRate = getRentalRate(connection, carId); // Retrieve rent price based on car ID
                System.out.print("Enter the number of days you want to rent the car: ");
                int days = scanner.nextInt();
                      

                // Prompt the user for rental start and end dates
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate, endDate;
                try {
                    System.out.print("Enter the rental start date (YYYY-MM-DD): ");
                    startDate = sdf.parse(scanner.next());
                    System.out.print("Enter the rental end date (YYYY-MM-DD): ");
                    endDate = sdf.parse(scanner.next());
                } catch (Exception e) {
                    System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
                    return;
                }

                // Calculate the total cost
                
double totalCost = rentalRate * days;
                // Update the availability of the car
                String updateQuery = "UPDATE cars SET available = 'no' WHERE car_id = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setInt(1, carId);
                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        // Insert a record into the rental_car table
                        insertRentalRecord(connection, userId, carId, startDate, endDate, totalCost);

                        // Generate a receipt
                        generateReceipt(carId, days, rentalRate, totalCost, startDate, endDate);

                        System.out.println("Car booked successfully. Receipt generated.");
                    } else {
                        System.out.println("Booking failed. Please try again.");
                    }
                }
            } else {
                System.out.println("Car is not available for booking.");
            }
        }
    }

    // Retrieve the rental rate from the cars table based on car ID
    private static double getRentalRate(Connection connection, int carId) throws SQLException {
        String rateQuery = "SELECT rent_price FROM cars WHERE car_id = ?";
        try (PreparedStatement rateStatement = connection.prepareStatement(rateQuery)) {
            rateStatement.setInt(1, carId);
            ResultSet rateResultSet = rateStatement.executeQuery();
            if (rateResultSet.next()) {
                return rateResultSet.getDouble("rent_price");
            }
        }
        throw new SQLException("Car not found or rent price not available.");
    }

    // Insert a new record into the rental_car table
    private static void insertRentalRecord(Connection connection, int userId, int carId, Date startDate, Date endDate, double totalCost) throws SQLException {
       
        String insertQuery = "INSERT INTO rental_cars_details (user_id, car_id, start_date, end_date, total_cost) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
            insertStatement.setInt(1, userId);
            insertStatement.setInt(2, carId);
            insertStatement.setDate(3, new java.sql.Date(startDate.getTime()));
            insertStatement.setDate(4, new java.sql.Date(endDate.getTime()));
            insertStatement.setDouble(5, totalCost);
            int rowsInserted = insertStatement.executeUpdate();
            if (rowsInserted <= 0) {
                throw new SQLException("Failed to insert rental record.");
            }
        }
    }

    // Generate a receipt including rental dates
    private static void generateReceipt(int carId, int days, double rentalRate, double totalCost, Date startDate, Date endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String receiptFileName = "G:\\recipt.txt";

        try (FileWriter fileWriter = new FileWriter(receiptFileName);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println("Receipt for Car Rental");
            printWriter.println("Car ID: " + carId);
            printWriter.println("Number of days: " + days);
            printWriter.println("Rental Rate per day: " + rentalRate + " rupees");
            printWriter.println("Rental Start Date: " + sdf.format(startDate));
            printWriter.println("Rental End Date: " + sdf.format(endDate));
            printWriter.println("Total Cost: " +  (rentalRate * days) + " rupees");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

private static void returnCar(Connection con, Scanner sc) throws SQLException {
    System.out.print("Enter the Car ID you want to return: ");
    int carId = sc.nextInt();

    // Check if the car is currently rented
    String rentalQuery = "SELECT * FROM cars WHERE car_id = ? AND available = 'no'";
    try (PreparedStatement rentalStatement = con.prepareStatement(rentalQuery)) {
        rentalStatement.setInt(1, carId);
        ResultSet rentalResultSet = rentalStatement.executeQuery();

        if (rentalResultSet.next()) {
            // Car is rented, proceed with return
            String updateQuery = "UPDATE cars SET available = 'yes' WHERE car_id = ?";
            try (PreparedStatement updateStatement = con.prepareStatement(updateQuery)) {
                updateStatement.setInt(1, carId);
                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Car returned successfully.");
                } else {
                    System.out.println("Car return failed. Please try again.");
                }
            }
        } else {
            System.out.println("Car is not currently rented.");
        }
    }
}
public static void cancelBooking(Connection con, Scanner sc) throws SQLException {
    System.out.print("Enter the Car ID you want to cancel the booking for: ");
    int carId = sc.nextInt();

    // Check if the car is currently booked by the user
    String cancelQuery = "UPDATE cars SET available = 'yes' WHERE car_id = ?";
    try (PreparedStatement cancelStatement = con.prepareStatement(cancelQuery)) {
        cancelStatement.setInt(1, carId);
        int rowsAffected = cancelStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Booking canceled successfully.");
        } else {
            System.out.println("Booking cancellation failed. Please try again.");
        }
    }
} 
public static boolean deleteAccount(int userId) {
    try {
        // Check if the user exists
        String checkUserQuery = "SELECT * FROM users WHERE user_id = ?";
        PreparedStatement checkUserStatement = con.prepareStatement(checkUserQuery);
        checkUserStatement.setInt(1, userId);
        ResultSet userResultSet = checkUserStatement.executeQuery();
        
        if (userResultSet.next()) {
            // User exists, proceed to delete
            String deleteQuery = "DELETE FROM users WHERE user_id = ?";
            PreparedStatement deleteStatement = con.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, userId);
            
            int rowsDeleted = deleteStatement.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println(" Account deleted successfully");
                return true;

            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    System.out.println(" Account deletion failed");
    return false; 
} 
    public static void main(String[] args) throws Exception {
       
        String dburl ="jdbc:mysql://localhost:3306/ Car Rental System";
        String dbuser="root";
        String dbpass="";
        String driver="com.mysql.cj.jdbc.Driver";
          con=DriverManager.getConnection(dburl, dbuser, dbpass);
        Statement st =con.createStatement();
      if (con!=null){
        System.out.println("GREAT SUCESS CAPTAIN");

      }
else   {
         System.out.println("not done try again");

        }
        
       
        sc = new Scanner(System.in);
boolean exit = false;

while (!exit) {
    boolean loggedIn = false; // Track if the user is logged in

    // Display login and create account options until  user has logged in
    while (!loggedIn) {
        System.out.println("\nWelcome to the Car Rental System");
        System.out.println("1. Login");
        System.out.println("2. Create an Account");
        System.out.println("3. Exit");
        System.out.println("Enter your choice: ");

        int loginChoice = sc.nextInt();
        sc.nextLine();

        switch (loginChoice) {
            case 1:
                System.out.println("Enter your username:");
                String username = sc.nextLine();
                System.out.println("Enter your password:");
                String password = sc.nextLine();
                loggedIn = Login(username, password);
                break;
            case 2:
                createAccount(con, sc);
                break;
            case 3:
                exit = true;
                System.exit(0);
                break;
                
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    // After logging in, display other methods
    while (loggedIn) {
        System.out.println("\nWelcome to the Car Rental System");
        System.out.println("1. View All Cars");
        System.out.println("2. View Available Cars");
        System.out.println("3. Book a Car");
        System.out.println("4. Return a Car");
        System.out.println("5. Cancel Booking");
        System.out.println("6. Delete Account");
        System.out.println("7. Exit");
        System.out.println("8. Logout");
        System.out.println("Enter your choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1:
                displayAllCars(con);
                break;
            case 2:
                displayAvailableCars(con);
                break;
            case 3:
                System.out.println("Enter your user ID:");
                int id = sc.nextInt();
                bookCar(con, sc, id);
                break;
            case 4:
                returnCar(con, sc);
                break;
            case 5:
                cancelBooking(con, sc);
                break;
            case 6:
                System.out.println("Enter the user id to delete:");
                int userid = sc.nextInt();
                deleteAccount(userid);
                break;
            case 7:
                exit = true;
                break;
            case 8:    // Logout and go back to login/create options
                loggedIn = false; 
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }
}
    }
}
    /*private static void generateReceipt(int carId, int days, double rentalRate, double totalCost) {
    try (FileWriter fileWriter = new FileWriter("receipt.txt");
         PrintWriter printWriter = new PrintWriter(fileWriter)) {
        printWriter.println("Receipt for Car Rental");
        printWriter.println("Car ID: " + carId);
        printWriter.println("Number of days: " + days);
        printWriter.println("Rental Rate per day: $" + rentalRate);
        printWriter.println("Total Cost: $" + totalCost);
        printWriter.println("Total Cost (calculated based on rent_price): $" + (rentalRate * days)); // Include the calculation
    } catch (Exception e) {
        e.printStackTrace();
    }
}
 */