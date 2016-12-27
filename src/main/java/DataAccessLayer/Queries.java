package DataAccessLayer;



public abstract class Queries {
    //USERS
    public static final String GET_USER_INFO = "SELECT * FROM user WHERE login=?";
    public static final String GET_ALL_USERS = "SELECT * FROM user";
    public static final String INSERT_USER = "INSERT INTO user VALUES ( ?, ?, ?, ?, ?)";
    public static final String DELETE_USER = "DELETE FROM user WHERE login=?";
    public static final String UPDATE_USER = "UPDATE user SET name=?, email=?, pass=?, group=? WHERE login=?";
    public static final String GET_PASS_HASH = "SELECT passHash FROM user WHERE login=?";
    public static final String CHANGE_PASS = "UPDATE user SET pass=? WHERE login=?";
    //GROUPS
    public static final String GET_ALL_GROUPS = "SELECT * FROM groups";
    //POSTS
    public static final String GET_USER_POSTS = "SELECT * FROM post WHERE author=?";
    public static final String INSERT_POST = "INSERT INTO post VALUES (?, NOW(), ?, ?)";
    public static final String DELETE_POST = "DELETE FROM post WHERE content=?";
    public static final String DELETE_USER_POSTS = "DELETE FROM post WHERE author=?";
    public static final String GET_POSTS = "SELECT * FROM post";
    //COMMENTS
    public static final String INSERT_COMMENT = "INSERT INTO comment VALUES(DEFAULT, ?, ?, NOW(), ?)";
    public static final String DELETE_COMMENT = "DELETE FROM comment WHERE id=?";
    public static final String GET_POST_COMMENTS = "SELECT * FROM comment WHERE topic=?";
}
