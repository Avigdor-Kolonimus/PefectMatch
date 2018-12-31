package DataHolder;
/**
 * The final assignment of Software Engineering
 * @author Alexey Titov and Shir Bentabou
 * @date 12.2018
 * @version 3.0
 */
//library
import WorkingClasses.User;

public class AuthenticatedUserHolder {
    //variables
    private User appUser = null;
    public static final AuthenticatedUserHolder instance = new AuthenticatedUserHolder();
    //empty constructor
    private AuthenticatedUserHolder() { }
    //getter and setter AppUser
    public User getAppUser() {
        return this.appUser;
    }
    public void setAppUser(User user) {
        this.appUser = user;
    }
}
