package javacon;
import java.sql.*;
import java.util.Scanner;

public class BookManagementSystem {
	static final String JDBC_URL = "jdbc:mysql://localhost:3306/bookManagement";
    static final String USERNAME = "root";
    static final String PASSWORD = "r@m33";
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            Scanner scanner = new Scanner(System.in)) {
            int choice;
            System.out.println("----Login----");
        	System.out.println("Enter Your UserName and Password:");
        	String user = scanner.next();
            String pass = scanner.next();
            boolean isLoggedIn = login(connection, user, pass);
            if (isLoggedIn) {
                System.out.println("Login successfully!");
                do {
                    System.out.println("Choose an option:");
                    System.out.println("1. Add a book");
                    System.out.println("2. View all books");
                    System.out.println("3. Update a book");
                    System.out.println("4. Delete a book");
                    System.out.println("5. Exit");
                    System.out.print("Enter your choice: ");
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    switch (choice) {
                        case 1:
                            addBook(connection, scanner);
                            break;
                        case 2:
                            viewBooks(connection);
                            break;
                        case 3:
                            updateBook(connection, scanner);
                            break;
                        case 4:
                            deleteBook(connection, scanner);
                            break;
                        case 5:
                            System.out.println("Exiting program.");
                            break;
                        default:
                            System.out.println("Invalid choice!");
                    }
                } while (choice != 5);

                // Logout
                logout();
                
            } else {
                System.out.println("Login failed. Invalid username or password.");
            }

                    } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 // Login
    public static boolean login(Connection connection, String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); 
            }
        }
     }
    //logout
    public static void logout() {
    	System.out.println("Logout successful!");
    }

    // Add a book
    public static void addBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Enter book details:");
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author ID: ");
        int authorId = scanner.nextInt();
        System.out.print("Genre ID: ");
        int genreId = scanner.nextInt();
        System.out.print("Publication year: ");
        int year = scanner.nextInt();
        scanner.nextLine(); 

        String sql = "INSERT INTO books (title, author_id, genre_id, year) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setInt(2, authorId);
            statement.setInt(3, genreId);
            statement.setInt(4, year);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new book was inserted successfully!");
            }
        }
    }

    // View all books
    public static void viewBooks(Connection connection) throws SQLException {
        String sql = "SELECT b.title, a.name AS author, g.name AS genre, b.year FROM books b " +
                     "JOIN authors a ON b.author_id = a.id " +
                     "JOIN genres g ON b.genre_id = g.id";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            System.out.println("Books:");
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String genre = resultSet.getString("genre");
                int year = resultSet.getInt("year");
                System.out.println("Title: " + title + ", Author: " + author + ", Genre: " + genre + ", Year: " + year);
            }
        }
    }

    // Update a book
    public static void updateBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter the title of the book to update: ");
        String title = scanner.nextLine();
        System.out.print("Enter the new publication year: ");
        int year = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String sql = "UPDATE books SET year=? WHERE title=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, year);
            statement.setString(2, title);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Book updated successfully!");
            } else {
                System.out.println("Book not found.");
            }
        }
    }

    // Delete a book
    public static void deleteBook(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter the title of the book to delete: ");
        String title = scanner.nextLine();

        String sql = "DELETE FROM books WHERE title=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("Book not found.");
            }
        }
    }
}

