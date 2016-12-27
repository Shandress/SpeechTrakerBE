package DataAccessLayer;

import Exceptions.DBException;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static DataAccessLayer.Queries.*;

public class JdbcDAO implements DAO {

    private static MysqlDataSource dataSource;

    static {
        try {
            dataSource = new MysqlDataSource();
            dataSource.setUser("root");
            dataSource.setPassword("dsugfcbiwe7ftacwgf");
            dataSource.setServerName("localhost");
            dataSource.setDatabaseName("speechtrackerdb");
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }

    }
    @Override
    public void addPerson(Map<String, Object> data) throws DBException {

        QueryRunner query = new QueryRunner(dataSource);

        try	{
            String login = (String)data.get("login");
            String name = (String)data.get("fullName");
            String email = (String)data.get("email");
            int pass = Integer.parseInt(data.get("passHash").toString());
            String group = (String)data.get("group");
            int updates = query.update(INSERT_USER, login, name, email, pass, group);
        }
        catch(Exception sqle) {
            throw new DBException(sqle.getMessage(), sqle);
        }
    }

    @Override @SuppressWarnings("unchecked cast")
    public Map<String, Object> getPerson(String login) throws DBException {
        QueryRunner query = new QueryRunner(dataSource);
        ResultSetHandler rsh = new MapHandler();
        try	{
            return (Map<String, Object>)query.query(GET_USER_INFO, rsh, login);

        } catch(Exception sqle) {

            throw new DBException("Problem getting person", sqle);
        }
    }

    @Override @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUserPosts(String login) throws DBException{
        QueryRunner query = new QueryRunner(dataSource);
        ResultSetHandler rsh = new MapListHandler();

        try	{
            List<Map<String, Object>> posts = (List<Map<String, Object>>)
                    query.query(GET_USER_POSTS, rsh, login);
            return posts;

        } catch(SQLException ex) {
            throw new DBException("Error getting user posts", ex);
        }
    }

    @Override
    public void addPost(String user, String content, String subject) throws DBException {
        QueryRunner query = new QueryRunner(dataSource);

        try	{
            int updates = query.update(INSERT_POST, user, content, subject);
        }
        catch(Exception sqle) {
            throw new DBException(sqle.getMessage(), sqle);
        }
    }

    @Override
    public void deleteUserPosts(String user) throws DBException {
        QueryRunner query = new QueryRunner(dataSource);

        try	{
            int deleted =  query.update(DELETE_USER_POSTS, user);

        } catch(Exception sqle) {

            throw new DBException("Problem deleting user posts", sqle);
        }
    }

    @Override
    public void deletePost(String content) throws DBException {
        QueryRunner query = new QueryRunner(dataSource);

        try	{
            int deleted =  query.update(DELETE_POST, content);

        } catch(Exception sqle) {

            throw new DBException("Problem deleting post", sqle);
        }
    }

    @Override
    public void addComment(String userId, String topicId, String content) throws DBException {
        QueryRunner query = new QueryRunner(dataSource);

        try	{
            int updates = query.update(INSERT_COMMENT, userId, topicId, content);
        }
        catch(Exception sqle) {
            throw new DBException(sqle.getMessage(), sqle);
        }
    }

    @Override
    public void deleteComment(int cId) throws DBException {
        QueryRunner query = new QueryRunner(dataSource);

        try	{
            int deleted =  query.update(DELETE_COMMENT, cId);

        } catch(Exception sqle) {

            throw new DBException("Problem deleting post", sqle);
        }
    }

    @Override
    public List<Map<String, Object>> getPosts() throws DBException {
        QueryRunner query = new QueryRunner(dataSource);
        ResultSetHandler rsh = new MapListHandler();

        try	{
            List<Map<String, Object>> posts = (List<Map<String, Object>>)
                    query.query(GET_POSTS, rsh);
            return posts;

        } catch(SQLException ex) {
            throw new DBException("Error getting posts", ex);
        }
    }

    @Override
    public List<Map<String, Object>> getPostComments(String topic) throws DBException {
        QueryRunner query = new QueryRunner(dataSource);
        ResultSetHandler rsh = new MapListHandler();

        try	{
            List<Map<String, Object>> comments = (List<Map<String, Object>>)
                   query.query(GET_POST_COMMENTS, rsh, topic);
            return comments;

        } catch(SQLException ex) {
            throw new DBException("Error getting post comments", ex);
        }
    }

    @Override
    public void deleteUser(String login) throws DBException {

        QueryRunner query = new QueryRunner(dataSource);

        try	{
            int deleted =  query.update(DELETE_USER, login);

        } catch(Exception sqle) {

            throw new DBException("Problem deleting", sqle);
        }
    }

    public static int getPassHash(String login) {
        try {
            QueryRunner query = new QueryRunner(dataSource);
            ResultSetHandler rh = new ScalarHandler<>();
            int passHash = Integer.parseInt(
                    query.query(dataSource.getConnection(), GET_PASS_HASH, rh, login)
                            .toString()
            );
            return passHash;
        }
        catch (SQLException ex) {
            return -1;
        }
    }

    public static boolean exists(String login) {
        QueryRunner query = new QueryRunner(dataSource);
        ResultSetHandler rsh = new MapHandler();
        try	{
            return query.query(GET_USER_INFO, rsh, login) != null;
        } catch(Exception sqle) {
            return false;
        }
    }
}
