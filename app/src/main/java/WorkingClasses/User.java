package WorkingClasses;
/**
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//library
import java.io.Serializable;
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class User implements Serializable { //To allow passing between intents
    //variables
    private String password;
    private String name;
    private String clientType;

    //empty constructor
    public User() { }

    //constructor with values
    public User(String password, String username, String clientType){
        this.password = password;
        this.name = username;
        this.clientType = clientType;
    }
    //getter and setter for username
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //getter and setter for password
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    //getter and setter for clientType
    public String getClientType(){ return clientType; }
    public void setClientType(String clientType){ this.clientType = clientType; }
}
