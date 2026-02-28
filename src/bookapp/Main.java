//main code that contains the GUI
package bookapp;

import bookapp.dao.BookDAO;
import bookapp.model.Book;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

public class Main extends Application 
{

    private final BookDAO dao = new BookDAO(); //connects to database
    private final ObservableList<Book> data = FXCollections.observableArrayList(); //updates automatically 
    private TableView<Book> table; //shows all the books

    //input boxes for user input
    private TextField titleField = new TextField(); 
    private TextField authorField = new TextField();
    private TextField borrowedField = new TextField();
    private TextField searchField = new TextField();

    //start() methoded by launch() method in main when program runs
    @Override
    public void start(Stage stage) 
    {
        stage.setTitle("My Book Collection"); //stage title

        //create a table
        //PropertyValueFactory tells JavaFX which getter to call from Book
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> borrowedCol = new TableColumn<>("Borrowed By");
        borrowedCol.setCellValueFactory(cell -> {
            String b = cell.getValue().getBorrowedBy();
            return new SimpleStringProperty(b != null ? b : "");
        });

        //updates the displayed table whenever a change occurs
        table.getColumns().addAll(idCol, titleCol, authorCol, borrowedCol);
        table.setItems(data);

        //loads any selected row from the displayed table into the form field
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) populateForm(newSel);
        });

        //creates the form for user input
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(8);
        form.setVgap(8);
        form.add(new Label("Title:"), 0, 0); form.add(titleField, 1, 0);
        form.add(new Label("Author:"), 0, 1); form.add(authorField, 1, 1);
        form.add(new Label("Borrowed by:"), 0, 2); form.add(borrowedField, 1, 2);

        //creates all the buttons
        HBox buttons = new HBox(8);
        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear");
        //adds all the buttons horizaontally
        buttons.getChildren().addAll(addBtn, updateBtn, deleteBtn, clearBtn);

        //when each button is clicked, respective methods are called
        addBtn.setOnAction(e -> onAdd());
        updateBtn.setOnAction(e -> onUpdate());
        deleteBtn.setOnAction(e -> onDelete());
        clearBtn.setOnAction(e -> clearForm());

        //vertical layout with the form on top and the buttons below
        VBox left = new VBox(10, form, buttons);

        //creates the search button and view all button which on click, calls respective methods
        Button searchBtn = new Button("Search");
        Button viewAllBtn = new Button("View All");
        HBox searchBox = new HBox(8, new Label("Search:"), searchField, searchBtn, viewAllBtn);
        searchBox.setPadding(new Insets(10));
        searchBtn.setOnAction(e -> onSearch());
        viewAllBtn.setOnAction(e -> loadAll());

        BorderPane root = new BorderPane(); //borderpane layout
        root.setLeft(left); //form and buttons
        root.setCenter(table); //book table
        root.setTop(searchBox); //search bar

        //display the GUI
        Scene scene = new Scene(root, 800, 450);
        stage.setScene(scene);
        stage.show();

        loadAll();
    }

    //when any row is selected, it loads it into the form
    private void populateForm(Book b) 
    {
        titleField.setText(b.getTitle());
        authorField.setText(b.getAuthor());
        borrowedField.setText(b.getBorrowedBy() != null ? b.getBorrowedBy() : "");
    }

    //it clears all data currently in the form
    private void clearForm() 
    {
        table.getSelectionModel().clearSelection();
        titleField.clear(); authorField.clear(); borrowedField.clear();
    }

    //method to read entered data and add it as a row to the database
    private void onAdd() 
    {
        try 
        {
            Book b = readFormToBook();
            int id = dao.addBook(b);
            if (id > 0) 
            {
                b.setId(id);
                data.add(0, b);
                clearForm();
                showInfo("Book added.");
            } 
            else 
                showError("Add failed.");
        } 
        catch (Exception ex) 
        { showError(ex.getMessage()); }
    }

    //method to update any book
    private void onUpdate() 
    {
        Book sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) 
        { showError("Select a book to update."); return; }
        try 
        {
            Book b = readFormToBook();
            b.setId(sel.getId());
            boolean ok = dao.updateBook(b);
            if (ok) 
            {
                int idx = data.indexOf(sel);
                data.set(idx, b);
                clearForm();
                showInfo("Updated.");
            } 
            else 
                showError("Update failed.");
        } 
        catch (Exception ex) 
        { showError(ex.getMessage()); }
    }

    //method to remove any book from the database
    private void onDelete() 
    {
        Book sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) 
        { 
            showError("Select a book to delete."); 
            return; 
        }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected book?", ButtonType.YES, ButtonType.NO);
        a.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) 
            {
                try 
                {
                    if (dao.deleteBook(sel.getId())) 
                    {
                        data.remove(sel);
                        clearForm();
                        showInfo("Deleted.");
                    } 
                    else 
                        showError("Delete failed.");
                } 
                catch (SQLException ex) 
                { showError(ex.getMessage()); }
            }
        });
    }

    //method to search a book
    private void onSearch() 
    {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) 
        { 
            loadAll(); 
            return; 
        }
        try 
        {
            List<Book> found = dao.searchBooks(kw);
            data.setAll(found);
        } 
        catch (SQLException e) 
        { showError(e.getMessage()); }
    }

    //method to load all books from the database
    private void loadAll() 
    {
        try 
        {
            data.setAll(dao.getAllBooks());
        } 
        catch (SQLException e) 
        { showError(e.getMessage()); }
    }

    //method to validate user input which returns a book object
    private Book readFormToBook() 
    {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String borrowed = borrowedField.getText().trim();
        if (title.isEmpty() || author.isEmpty()) throw new IllegalArgumentException("Title and Author are required.");
        return new Book(title, author, borrowed.isEmpty() ? null : borrowed);
    }

    //methods to show popups for errors or any information
    private void showError(String msg) 
    {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK); a.showAndWait();
    }
    private void showInfo(String msg) 
    {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK); a.showAndWait();
    }

    //main method in which launch() invokes start() to run the GUI
    public static void main(String[] args) 
    {
        launch();
    }
}
