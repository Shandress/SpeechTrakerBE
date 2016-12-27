package SystemLogic;

import DataAccessLayer.DAO;
import DataAccessLayer.JdbcDAO;
import Exceptions.DBException;
import org.apache.velocity.VelocityContext;
import org.boon.json.JsonFactory;
import org.boon.json.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static SystemLogic.Constants.*;

public class Controller {

    private DAO dao;
    ObjectMapper mapper = JsonFactory.create();
    public Controller(DAO dao) {

        super();
        this.dao = dao;
    }

    public String addPerson(String login, String fullName, int pass, String email, String group) {
        Map<String, Object> data = new HashMap<>();
        data.put("login", login);
        data.put("fullName", fullName);
        data.put("passHash", pass);
        data.put("email", email);
        data.put("group", group);
        try {
            dao.addPerson(data);
            return MSG_USER_ADDED;
        }
        catch (DBException dbEx) {
            String msg = dbEx.getMessage();
            if(msg.startsWith("Duplicate entry")) {
                return USER_ALREADY_EXISTS;
            }
            return dbEx.getMessage();
        }

    }

    public String getUser(String login, String uToken) throws DBException {
        try {
            Map<String, Object> userData = dao.getPerson(login);
            userData.put("token", uToken);
            return  Utility.userToXml(userData);
        }
        catch (DBException dbEx) {
            throw dbEx;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return GET_USER_ERROR;
    }

    public VelocityContext setUserPosts(String login, VelocityContext vc) throws DBException {
        List<Map<String, Object>> userPosts = dao.getUserPosts(login);
        vc.put("userPosts", userPosts);
        return vc;

    }

    public String getUserPosts(String login) throws DBException {
        try {
            List<Map<String, Object>> userPosts = dao.getUserPosts(login);
            return  Utility.postsToXml(userPosts);
        }
        catch (DBException dbEx) {
            throw dbEx;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return GET_USER_ERROR;
    }

    public String deleteUser(String login) {
        try {
            dao.deleteUser(login);
            return MSG_USER_DELETED;
        } catch (DBException e) {
            return e.getMessage();
        }
        catch (Exception e) {
            return MSG_ERROR;
        }
    }

    public boolean userExists(String login) {
        return JdbcDAO.exists(login);
    }

    public String getPostsAndComments(List<Map<String, Object>> posts) throws DBException {
        return Utility.postsAndCommentsToXml(posts);
    }
}