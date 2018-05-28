package phonebook;

import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class viewController implements Initializable {

    @FXML
    TableView table;
    @FXML
    TextField inputLastName;
    @FXML
    TextField inputFirstName;
    @FXML
    TextField inputEmail;
    @FXML
    Button addNewContact;
    @FXML
    StackPane menuPane;
    @FXML
    Pane contactPane;
    @FXML
    Pane exportPane;
    @FXML
    Button exportBTN;
    @FXML
    TextField inputExport;
    @FXML
    AnchorPane anchor;
    @FXML
    SplitPane mainSplit;
    
    DB db = new DB();

    private final String MENU_CONTACTS = "Kontaktok";
    private final String MENU_LIST = "Lista";
    private final String MENU_EXPORT = "Exportálás";
    private final String MENU_EXIT = "Kilépés";

    //Observable list feltöltése az adatbázisból
    private final ObservableList<Person> adat
            = FXCollections.observableArrayList(db.getAllContacts());

    //Táblázat beállítása
    public void setTableData() {
        
        //Vezetéknév oszlop
        TableColumn lastNameCol = new TableColumn("Vezetéknév");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
        
            //Módosítás kezelése az oszlopban
        lastNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> t) {
                Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());
                       actualPerson.setLastName(t.getNewValue());
                       
                       db.updateContact(actualPerson);
            }
        }
        );

        
        //Keresztnév oszlop
        TableColumn firstNameCol = new TableColumn("Keresztnév");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        
            //Módosítás kezelése az oszlopban
        firstNameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> t) {
                Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());
                       actualPerson.setFirstName(t.getNewValue());
                       db.updateContact(actualPerson);
            }
        }
        );
        //e-mail cím oszlop
        TableColumn emailCol = new TableColumn("Email cím");
        emailCol.setMinWidth(200);
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
        
            //Módosítás kezelése az oszlopban
        emailCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Person, String> t) {
                Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow());
                       actualPerson.setEmail(t.getNewValue());
                       db.updateContact(actualPerson);
            }
        }
        );
            //Oszlopok meghívása és adatokkal való feltöltése
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol);
        table.setItems(adat);
    }
    
    //Bal oldali menü létrehozása
    private void setMenuData() {
        TreeItem<String> treeItemRoot1 = new TreeItem<>("Menü");
        TreeView<String> treeView = new TreeView<>(treeItemRoot1);
        treeView.setShowRoot(false);

        TreeItem<String> nodeItemA = new TreeItem<>(MENU_CONTACTS);
        TreeItem<String> nodeItemB = new TreeItem<>(MENU_EXIT);

        //nodeItemA.setExpanded(true);
        Node contactsNode = new ImageView(new Image(getClass().getResourceAsStream("/contacts.png")));
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("/export.png")));
        TreeItem<String> nodeItemA1 = new TreeItem<>(MENU_LIST, contactsNode);
        TreeItem<String> nodeItemA2 = new TreeItem<>(MENU_EXPORT, exportNode);

        nodeItemA.getChildren().addAll(nodeItemA1, nodeItemA2);
        treeItemRoot1.getChildren().addAll(nodeItemA, nodeItemB);

        menuPane.getChildren().add(treeView);
        
        //Kattintásra kinyílik a menü
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                String selectedMenu;
                selectedMenu = selectedItem.getValue();

                if (null != selectedMenu) {
                    switch (selectedMenu) {
                        case MENU_CONTACTS:
                            selectedItem.setExpanded(true);
                            break;
                        case MENU_LIST:
                            contactPane.setVisible(true);
                            exportPane.setVisible(false);
                            break;
                        case MENU_EXPORT:
                            contactPane.setVisible(false);
                            exportPane.setVisible(true);
                            break;
                        case MENU_EXIT:
                            System.exit(0);
                            break;
                    }
                }

            }
        });

    }
    //Új kontakt hozzáadása a listához
    @FXML
    private void addContact(ActionEvent event) {

        String email = inputEmail.getText();
        if (email.contains("@") && email.length() > 3 && email.contains(".")) {
            Person newContact= new Person(inputLastName.getText(), inputFirstName.getText(), email);            
            adat.add(newContact);
            db.addContact(newContact);
            inputLastName.clear();
            inputFirstName.clear();
            inputEmail.clear();
        } else {
            alert("Adj meg egy valódi e-mail címet!");
        }
    }
    // Lista exportálása PDF-be
    @FXML
    private void exportList(ActionEvent event) {
        //Fájlnév ellenőrzése
        String fileName = inputExport.getText();
        fileName = fileName.replaceAll("\\s+", "");
        if (fileName!= null && !fileName.equals(""))

        {
        PdfGeneration pdfCreator = new PdfGeneration();
        pdfCreator.pdfGenerate(fileName, adat);
        }else{
         alert("Adj meg egy fájlnevet!");   
        }
    }


//Hibaüzenet
    private void alert(String text){
        mainSplit.setDisable(true);
        mainSplit.setOpacity(0.4);
        
        Label label = new Label(text);
        Button alertButton = new Button("OK");
        VBox vbox = new VBox(label,alertButton);
        vbox.setAlignment(Pos.CENTER);
        
        alertButton.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                mainSplit.setDisable(false);
                mainSplit.setOpacity(1);
                vbox.setVisible(false);
            }
            
        });
        
        anchor.getChildren().add(vbox);
        anchor.setTopAnchor(vbox,300.0);
        anchor.setLeftAnchor(vbox,300.0);
        
    
    }
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTableData();
        setMenuData();
        //alert("teszt");
        
        

    }

}
