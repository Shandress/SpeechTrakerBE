import DataAccessLayer.DAO;
import DataAccessLayer.JdbcDAO;
import Exceptions.DBException;
import SystemLogic.Constants;
import SystemLogic.Controller;
import SystemLogic.Utility;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.security.Key;
import java.util.*;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by Dark on 12/10/2016.
 */
public class Main {
    private final static DAO dao = new JdbcDAO();
    private final static Controller controller = new Controller(dao);
    private static Key key = MacProvider.generateKey();

    public static void main(String[] args) {

        //port(5050);
        get("/", (req, res) -> "ggg");

        post("/", (req, res) -> "ddddff");

        post("/viewTopics", (req, res) -> {
            Boolean mobile = req.queryParams("mobile") != null;
            VelocityEngine ve = initVelocity();
            StringWriter sr = new StringWriter();
            VelocityContext vc = new VelocityContext();
            Template vt = ve.getTemplate("templates/topics.vm");
            Claims claims;
            String login = req.queryParams("login");

            String token = (mobile)
                    ? req.queryParams("token")
                    : req.cookie("access_token");

            List<Map<String, Object>> posts = new ArrayList<>();
            try {
                claims = Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(key.getEncoded()))
                        .parseClaimsJws(token).getBody();
            }
            catch (Exception e) {
                return "Authentication failed";
            }
            if(login.equalsIgnoreCase(claims.getSubject())) {
                posts = dao.getPosts();
                for(Map<String, Object> post:posts) {
                    post.put("comments", dao.getPostComments(post.get("subject").toString()));
                }
            }
            else {
                return "Authentication failed";
            }

            if(!mobile) {
                vc.put("login", login);
                vc.put("link", "http://localhost:63342/SpeechProject/login.html");
                vc.put("topics", posts);
                vt.merge(vc, sr);
                return sr.toString();
            }
            else {

                return controller.getPostsAndComments(posts);
            }

        });

        post("/addPost", (req, res) -> {
            String content = req.queryParams("content");
            String subject = req.queryParams("subject");
            String author = req.queryParams("login");
            String token = req.cookie("access_token");
            Claims claims;
            try {
                claims = Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(key.getEncoded()))
                        .parseClaimsJws(token).getBody();
            }
            catch (Exception e) {
                return "Authentication failed";
            }
            if(author.equalsIgnoreCase(claims.getSubject())) {
                dao.addPost(author, content, subject);
            }
            else {
                return "Authentication failed";
            }
            return "Post added";
        });

        post("/addComment", (req, res) -> {
            String login = req.queryParams("login");
            String topic = req.queryParams("subject");
            String content = req.queryParams("content");
            String token = req.cookie("access_token");

            Claims claims;
            try {
                    claims = Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(key.getEncoded()))
                        .parseClaimsJws(token).getBody();
            }
            catch (Exception e) {
                return "Authentication failed";
            }
            if(login.equalsIgnoreCase(claims.getSubject())) {
                dao.addComment(login, topic, content);
            }
            else {
                return "Authentication failed";
            }
            return "Comment added";
        });
        post("/reg", (req, res) -> {
            VelocityEngine ve = initVelocity();
            StringWriter sr = new StringWriter();

            String login = req.queryParams("login");
            String email = req.queryParams("email");
            String pass = req.queryParams("pass");
            String name = req.queryParams("name");
            String group = req.queryParams("group");
            String repeatPass = req.queryParams("repeatPass");
            int passHash = 0;

            String msg = Utility.checkPassword(pass, repeatPass);
            boolean validated = msg.equalsIgnoreCase(Constants.MSG_PSWD_OK);
            if (validated) {
                passHash = Utility.hashPassword(pass);
            }
            msg = controller.addPerson(login, name, passHash, email, group);
            boolean userAdded = msg.equalsIgnoreCase(Constants.MSG_USER_ADDED);
            VelocityContext vc = new VelocityContext();
            vc.put("userAdded", userAdded);
            if (userAdded) {
                vc.put("login", login);
                vc.put("name", name);
                String url = "http://localhost:63342/SpeechProject/login.html";
                vc.put("link", url);
            }
            Template vt = ve.getTemplate("templates/addUser.vm");
            vc.put("msg", msg);
            vt.merge(vc, sr);
            return sr.toString();
        });

        post("/login", (req, res) -> {
                if(req.cookies().keySet().contains("access_token")) {
                req.cookies().remove("access_token");
            }
            Boolean mobile = req.queryParams("mobile") != null;
            VelocityEngine ve = initVelocity();
            StringWriter sr = new StringWriter();
            VelocityContext vc = new VelocityContext();
            Template vt = ve.getTemplate("templates/userPage.vm");

            String login = req.queryParams("login");
            String param = req.queryParams("pass");
            int pass = -1;
            if(param != null) {

                pass = (mobile)
                    ? Integer.parseInt(param)
                    : Utility.hashPassword(param);
            }
            String result = "", msg = "";
            Boolean success = false;

            if(controller.userExists(login)) {
                try {
                    if(Utility.validatePassword(login, pass)) {
                        result = Jwts.builder()
                                .setSubject(login)
                                .signWith(SignatureAlgorithm.HS512, key)
                                .compact();
                        if(!mobile) {
                            vc = controller.setUserPosts(login, vc);
                            vc.put("login", login);
                            vc.put("link", "http://localhost:4567/logout");
                        }

                        res.header("Set-Cookie", String.format("access_token=%s", result));
                        success = true;
                    }
                    else {
                        msg = Constants.INCORRECT_PASSWORD;
                    }
                }
                catch (DBException dbEx) {
                    msg = (dbEx.getMessage().startsWith("Duplicate entry"))
                            ? Constants.USER_ALREADY_EXISTS
                            : dbEx.getMessage();
                }
            }
            else {
                msg = Constants.NO_SUCH_USER;
            }
            if(!mobile) {
                vc.put("loggedIn", success);
                vc.put("msg", msg);
                vt.merge(vc, sr);
                return sr.toString();
            }
            else {

                return String.format("%s+%s",
                        controller.getUserPosts(login),
                        controller.getUser(login, result));
            }

        });
    }

    private static Properties setProperties() {
        Properties p = new Properties();
        p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        p.setProperty("resource.loader", "file");
        p.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return p;
    }
    private static VelocityEngine initVelocity() throws Exception {
        Properties p = setProperties();
        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        return ve;
    }
}