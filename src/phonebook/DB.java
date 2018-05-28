
package phonebook;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class DB {
    
   // final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    final String URL = "jdbc:derby:ContactsDB;create=true";
    final String USERNAME = "";
    final String PASSWORD = "";
    
   //létrehozzuk a kapcsolatot
    Connection conn = null;
    Statement createStatement = null;
    DatabaseMetaData dbmd = null;

    public DB(){
   
        //megpróbáljuk életrekelteni
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("A híd létrejött.");
        } catch (SQLException ex) {
            System.out.println("A híd nem jött létre."+ex);
            
        }
        //ha életre kelt, csinálunk egy createStatementet
        
        if (conn != null){
            try {
                createStatement = conn.createStatement();
            } catch (SQLException ex) {
                System.out.println("Valami baj van a createstatementnél");
            }
        }
         

        //Megnézzük, hogy üres-e az adatbázis
        DatabaseMetaData dbmd = null;
         try {
            dbmd = conn.getMetaData();
        } catch (SQLException ex) {
            System.out.println("Valami baj van a metaadatok lekérdezésénél");
        }
        
        //Ha nincs adattábla, létrehozzuk azt 
        try {
            ResultSet rs = dbmd.getTables(null, "APP", "CONTACTS", null);
            if(!rs.next()){
                //Létrehozzuk a mezőket (ID,Vezetéknév,Keresztnév,e-mail)
                createStatement.execute("create table contacts(id INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), lastname varchar(20), firstname varchar(20), email varchar(30))");
            }
        } catch (SQLException ex) {
            System.out.println("Valami baj van az adattábla létrehozásakor\n" +ex);
        }
}
    
    //Összes kontakt beolvasása Person pojo-kat tartalmazó ArrayListbe
    public ArrayList<Person> getAllContacts(){
        String sql = "select * from contacts";
        ArrayList<Person> users = null;
        try {
            ResultSet rs =  createStatement.executeQuery(sql);
            users = new ArrayList<>(); 
        
            while (rs.next()){
               Person actualPerson = new Person(rs.getInt("id"),rs.getString("lastname"), rs.getString("firstname"), rs.getString("email"));          
               users.add(actualPerson);
            }
        } catch (SQLException ex) {
            System.out.println("Valami baj van a kiolvasáskor");

        }
    return users;    
}
    //Új kontakt hozzáadása prepared statementtel
    public void addContact(Person person){
    try {
             
             String sql = "insert into contacts (lastname,firstname,email) values(?,?,?)";           
             PreparedStatement preparedStatement =  conn.prepareStatement(sql);
             preparedStatement.setString(1, person.getLastName());
             preparedStatement.setString(2, person.getFirstName());
             preparedStatement.setString(3, person.getEmail());
             preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("Valami baj van a contact létrehozásakor");
        }
}
    //Módosítás az adattáblában
    public void updateContact(Person person){
    try {
             
             String sql = "update contacts set lastname= ? , firstname = ? , email = ? where id = ?";           
             PreparedStatement preparedStatement =  conn.prepareStatement(sql);
             preparedStatement.setString(1, person.getLastName());
             preparedStatement.setString(2, person.getFirstName());
             preparedStatement.setString(3, person.getEmail());
             preparedStatement.setInt(4, Integer.parseInt(person.getId()));
             
             preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("Valami baj van a contact létrehozásakor");
        }
}
}
