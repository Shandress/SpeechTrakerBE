package DataAccessLayer;

import Exceptions.DBException;

import java.util.List;
import java.util.Map;

/**
 * Created by Dark on 12/11/2016.
 */
public interface DAO {

    public void addPerson(Map<String, Object> data) throws DBException;

    public Map<String, Object> getPerson(String login) throws DBException;

    public void deleteUser(String login) throws DBException;

    public List<Map<String, Object>> getUserPosts(String login) throws DBException;

    public void addPost(String user, String content, String subject) throws DBException;

    public void deleteUserPosts(String user) throws DBException;

    public void deletePost(String content) throws DBException;

    public void addComment(String userId, String topicId, String content) throws DBException;

    public void deleteComment(int cId) throws DBException;

    public List<Map<String,Object>> getPosts() throws DBException;

    public List<Map<String, Object>> getPostComments(String topic) throws DBException;
}