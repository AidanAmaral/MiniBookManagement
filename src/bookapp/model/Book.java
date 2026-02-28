//package to handle book object
package bookapp.model;

public class Book 
{
    private int id;
    private String title;
    private String author; 
    private String borrowedBy;

    //default constructor
    public Book() {}

    //constructor that is used to display records
    public Book(int id, String title, String author, String borrowedBy) 
    {
        this.id = id; this.title = title; this.author = author; this.borrowedBy = borrowedBy;
    }

    //constructor that is used to store records (id is added by the database)
    public Book(String title, String author, String borrowedBy) 
    {
        this(0, title, author, borrowedBy);
    }

    // getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }

    public String getAuthor() { return author; }
    public void setAuthor(String a) { this.author = a; }

    public String getBorrowedBy() { return borrowedBy; }
    public void setBorrowedBy(String b) { this.borrowedBy = b; }
}
