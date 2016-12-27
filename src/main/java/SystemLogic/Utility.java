package SystemLogic;

/**
 * Created by Dark on 12/13/2016.
 */

import DataAccessLayer.JdbcDAO;

import java.util.List;
import java.util.Map;

import static SystemLogic.Constants.*;

public class Utility {
    public static String checkPassword(String pass, String repeatPass) {
        Boolean validated = pass.equals(repeatPass);
        String msg = MSG_PSWD_OK;
        if(!validated) {
            msg = MSG_PSWD_MISMATCH;
        }
        else {
            validated = pass.length() >= 3;
            if(!validated) {
                msg = MSG_PSWD_TOO_SHORT;
            }
        }
        return msg;
    }

    public static int hashPassword(String pass) {
        int hash = 0;
        for(int i = 0; i < pass.length(); i++) {
            hash += (int)pass.charAt(i)*(i + 1);
        }
        return hash;
    }

    public static String userToXml(Map<String,Object> userData) {
        String res = "<User>\n";
        for(String attr:userData.keySet()) {
            res = res.concat(String.format("<%s>%s</%s>\n", attr, userData.get(attr), attr));
        }
        res = res.concat("</User>");
        return res;
    }

    public static String postsToXml(List<Map<String,Object>> posts) {
        String res = "<Posts>\n";
        for(Map<String, Object> post:posts) {
            res = res.concat(postToXml(post));
        }
        res = res.concat("</Posts>");
        return res;
    }

    private static String postToXml(Map<String, Object> post) {
        String res = "<Post>\n";
        for(String attr:post.keySet()) {
            res = res.concat(String.format("<%s>%s</%s>\n", attr, post.get(attr), attr));
        }
        res = res.concat("</Post>\n");
        return res;
    }


    public static boolean validatePassword(String login, int providedPass) {
        return providedPass == JdbcDAO.getPassHash(login);
    }

    public static String postsAndCommentsToXml(List<Map<String, Object>> posts) {
        String res = "<Posts>\n";
        for(Map<String, Object> post:posts) {
            res = res.concat(postAndCommentsToXml(post));
        }
        res = res.concat("</Posts>");
        return res;
    }

    private static String postAndCommentsToXml(Map<String, Object> post) {
        String res = "<Post>\n";
        for(String attr:post.keySet()) {
            if(attr.equalsIgnoreCase("comments")) {
                res = res.concat(Utility.commentsToXML((List<Map<String, Object>>)post.get(attr)));
            }
            else {
                res = res.concat(String.format("<%s>%s</%s>\n", attr, post.get(attr), attr));
            }
        }
        res = res.concat("</Post>\n");
        return res;
    }

    private static String commentsToXML(List<Map<String, Object>> comments) {
        String res = "<Comments>\n";
        for(Map<String, Object> comment:comments) {
            res = res.concat(Utility.commentToXML(comment));
        }
        return res.concat("</Comments>\n");
    }

    private static String commentToXML(Map<String, Object> comment) {
        String res = "<Comment>\n";
        for(String attr:comment.keySet()) {
            res = res.concat(String.format("<%s>%s</%s>\n", attr, comment.get(attr), attr));
        }
        res = res.concat("</Comment>\n");
        return res;
    }
}
