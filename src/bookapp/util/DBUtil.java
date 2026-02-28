//package to handle database connection
package bookapp.util;

import java.sql.*;

public class DBUtil
{
    //database location
    private static final String URL = "jdbc:mysql://localhost:3306/book_inventory?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "15@!dan11"; //enter your own database passwords

    //method to actually connect to the database
    public static Connection getConnection() throws SQLException 
    {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}