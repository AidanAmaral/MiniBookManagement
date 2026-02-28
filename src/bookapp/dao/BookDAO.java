//package to handle database operations
package bookapp.dao;

import bookapp.model.Book;
import bookapp.util.DBUtil;
import java.sql.*;
import java.util.*;

public class BookDAO 
{

    public int addBook(Book b) throws SQLException 
    {
        //sql command
        String sql = "INSERT INTO books (title, author, borrowed_by) VALUES (?, ?, ?)";

        //connect to database and create a statement
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {
            ps.setString(1, b.getTitle()); //sets the title
            ps.setString(2, b.getAuthor());//sets the author

            //if book is borrowed by someone, it inserts that persons name else it makes it null
            if (b.getBorrowedBy() != null && !b.getBorrowedBy().isBlank()) 
                ps.setString(3, b.getBorrowedBy());
            else 
                ps.setNull(3, Types.VARCHAR);

            //runs the command
            ps.executeUpdate();

            //if successful, it returns the ID of the book
            try (ResultSet rs = ps.getGeneratedKeys())
            {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        }
    }

    public List<Book> getAllBooks() throws SQLException 
    {
        //empty list 
        List<Book> list = new ArrayList<>();
        //sql command
        String sql = "SELECT * FROM books ORDER BY id";
        //connect to the database and create a statement
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) 
        {
            //keep adding rows to the list
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Book> searchBooks(String keyword) throws SQLException 
    {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            //tries to find the keyword in either title or author
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) 
            {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public boolean updateBook(Book b) throws SQLException 
    {
        String sql = "UPDATE books SET title=?, author=?, borrowed_by=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());

            if (b.getBorrowedBy() != null && !b.getBorrowedBy().isBlank()) 
                ps.setString(3, b.getBorrowedBy());
            else 
                ps.setNull(3, Types.VARCHAR);

            ps.setInt(4, b.getId());

            //executes the statement and returns true if more than 0 rows were updated
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteBook(int id) throws SQLException 
    {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) 
        {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    //method to convert one row of the database into a book object
    private Book mapRow(ResultSet rs) throws SQLException 
    {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("borrowed_by")
        );
    }
}
