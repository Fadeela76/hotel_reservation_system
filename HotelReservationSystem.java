//hotel_reservation_system using java oops, jdbc and mysql
import java.sql.*;  // For JDBC operations
import java.util.*; // For Scanner and utility classes

public class HotelReservationSystem {

    // Database credentials and connection URL
    private static final String url = "jdbc:mysql://localhost:3306/hotel_reservation_system";
    private static final String username = "root";
    private static final String password = "Watermelon1@";

    public static void main(String[] args)  {

        //loading driver
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        //establishing connection
        try{
            Connection connection = DriverManager.getConnection(url, username, password);

            // Loop to display menu continuously
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");

                // Create Scanner for user input
                Scanner scanner = new Scanner(System.in);

                // Display available options
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");

                // Read user choice
                int choice = scanner.nextInt();

                // Handle user choice
                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        // Invalid input handling
                        System.out.println("Invalid choice. Try again.");
                }
            }

        }catch (SQLException e) {
            // Handle database connection or query errors
            System.out.println(e.getMessage());
        }
    }

    // Method to reserve a room
    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();
            scanner.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();
            System.out.print("Enter contact number: ");
            String contactNumber = scanner.next();

            // SQL INSERT query to add reservation
            String sql = "INSERT INTO reservation (guest_name, room_number, contact_number) " +
                    "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }
    }

    // Method to view all reservations
    private static void viewReservations(Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservation";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // Header of the table
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            // Display each reservation
            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            // Footer of the table
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
    }

    // Method to get room number by reservation ID and guest name
    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            // Read reservation ID and guest name
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            // SQL query to get room number for matching reservation
            String sql = "SELECT room_number FROM reservation " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                // Check and display result
                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to update reservation details
    private static void updateReservation(Connection connection, Scanner scanner) {
        try {
            // Prompt for reservation ID to update
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();

            // Check if reservation exists
            if (reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            // Prompt for new details
            scanner.nextLine();
            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            // SQL UPDATE query
            String sql = "UPDATE reservation SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                // Display update result
                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }
    }

    // Method to delete reservation by ID
    private static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            // Check if reservation exists
            if (reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            // SQL DELETE query
            String sql = "DELETE FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                // Confirm result of deletion
                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Utility method to check if a reservation ID exists
    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            // SQL query to search for reservation ID
            String sql = "SELECT reservation_id FROM reservation WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                // Return true if reservation does not exist
                return !resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return true; // Handle database errors as needed
        }
    }

    // Method to exit the program with a goodbye message
    public static void exit(){
        System.out.print("Exiting System");
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}

