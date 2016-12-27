package DataModel;

/**
 * Created by Dark on 12/13/2016.
 */
public class User {
    private int id;
    private String login;
    private String email;
    private String fullName;
    private int password;

    public User(int id, String login, String fullName, int password, String email) {
        setId(id);
        setEmail(email);
        setLogin(login);
        this.password = password;
        setName(fullName);
    }

    public User(User other) {
        setId(other.id);
        setEmail(other.email);
        setLogin(other.login);
        this.password = other.password;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }


    public boolean checkPassword(int password) {
        return this.password == password;
    }
    public int getPassword() {
        return this.password;
    }



    public String getName() {
        return fullName;

    }

    public String getNickname() {
        return login;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        email = value;
    }

    public void setName(String value) {
        fullName = value;
    }

    public void setLogin(String value) {
        login = value;
    }

    public String toJson() {
        return String.format("{ 'id:'%d', 'login':'%s', 'name':'%s', 'pass':'%d', 'email':'%s' }",
                id, login, fullName, password, email);
    }
}


