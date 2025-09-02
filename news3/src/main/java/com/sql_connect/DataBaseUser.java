package com.sql_connect;

import android.annotation.SuppressLint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseUser {

    protected static String classes="com.mysql.jdbc.Driver";
    protected static String ip="193.124.33.173";
    protected static String port="3306";
    protected static String db="article";
    protected static String username="article_user";
    protected static String password="article";
    private String table="users";
    protected static String conUrl="jdbc:mysql://"+ip+":"+port+"/"+db;
    protected static String columnLogin="login";
    protected static String columnEmail="email";
    protected static String columnId="id";
    protected static String columnPassword="password";
    public String errorDb;

    public void changeRole(String login,String role) {
        try{
            String newRole="UPDATE user_role JOIN users ON user_role.user_id = users.id SET user_role.roles = '"+role+"' WHERE users.login = '"+login+"'";
            Statement statement=Conn().createStatement();
            statement.executeUpdate(newRole);
            statement.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String,String>> getDataOneUser(String login)  {
        errorDb=null;
        List<Map<String,String>>userArticles=null;
        userArticles=new ArrayList<Map<String, String>>();
        String q="SELECT articles.* FROM users LEFT OUTER JOIN articles ON users.id = articles.user WHERE login = '"+login+"'";
        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd.MM.yyyy");
        try {
            Statement statement=Conn().createStatement();
            ResultSet resultSet=statement.executeQuery(q);
            while (resultSet.next()) {
                if (resultSet.getString("id")!=null) {
                    Map<String, String> dtName = new HashMap<String, String>();

                    dtName.put("id", resultSet.getString("id"));
                    dtName.put("title", resultSet.getString("title"));
                    dtName.put("intro", resultSet.getString("intro"));
                    dtName.put("text", resultSet.getString("text"));

                    calendar.setTime(resultSet.getDate("createdate"));
                    dtName.put("createdate", simpleDateFormat.format(calendar.getTime()));
                    dtName.put("user", resultSet.getString("user"));
                    userArticles.add(dtName);
                }
            }
            statement.close();
        }catch (Exception ex){
            this.errorDb= ex.toString();
        }
        return userArticles;
    }

    public void deleteUser(String login) {
        try{
            String q="DELETE FROM "+table+" WHERE login = '"+login+"'";
            Statement statement=Conn().createStatement();
            statement.executeUpdate(q);
            statement.close();
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void editUser(String login,String newLogin,String newEmail,String role) {
        try{
            String q= "UPDATE "+table+" SET login = '"+newLogin+"', email = '"+newEmail+"' WHERE login = '"+login+"'";
            String r="UPDATE user_role JOIN users ON user_role.user_id = users.id SET user_role.roles = '"+role+"' WHERE users.login = '"+newLogin+"'";
            Statement statement=Conn().createStatement();
            statement.executeUpdate(q);
            statement.executeUpdate(r);
            statement.close();
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public List<Map<String,String>> getAllUsers(){
        errorDb=null;
        List<Map<String,String>>data=null;
        data=new ArrayList<Map<String, String>>();
        String q="SELECT users.login,users.email, COUNT(articles.`user`) AS " +
                "count, user_role.roles FROM users LEFT OUTER JOIN " +
                "articles ON articles.user = users.id LEFT OUTER JOIN " +
                "user_role ON user_role.user_id = users.id GROUP BY users.login,users.email, user_role.roles";
        try {
            Statement statement=Conn().createStatement();
            ResultSet resultSet = statement.executeQuery(q);
            while (resultSet.next()) {
                Map<String, String> dtName = new HashMap<String, String>();
                dtName.put("2", resultSet.getString("login"));
                dtName.put("count", resultSet.getString("count"));
                dtName.put("roles", resultSet.getString("roles"));
                dtName.put("email", resultSet.getString("email"));
                data.add(dtName);
            }
            statement.close();
        }catch (Exception ex){
            this.errorDb= ex.toString();
        }
        return data;
    }

        public String[] authUser(String login, String pass) {
            String role = null;
            String id=null;
            String[] user = null;
        String q="SELECT id, (SELECT roles FROM user_role WHERE user_id = users.id) FROM `"+table+"` WHERE "+columnLogin+" = '"+login+"' AND "+columnPassword+" = '"+pass+"'";
        try {
            Statement statement=Conn().createStatement();
            ResultSet resultSet = statement.executeQuery(q);
            while (resultSet.next()) {
                role = resultSet.getString("(SELECT roles FROM user_role WHERE user_id = users.id)");
                id = resultSet.getString("id");
                user= new String[]{id, role};
            }
            statement.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
        }

    public void insertUser(String login,String email,String password) {
        try{
            String cU="INSERT INTO `"+table+"` ("+columnLogin+", "+columnEmail+", "+columnPassword+") VALUES ('"+login+"', '"+email+"', '"+password+"')";
            String cR="INSERT INTO user_role (user_id, roles) VALUES (LAST_INSERT_ID(), '"+UserRole.USER+"')";
            Statement statement=Conn().createStatement();
            statement.executeUpdate(cU);
            statement.executeUpdate(cR);
            statement.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int isExistUser(String login)  {
        String q="SELECT id FROM users WHERE login = '"+login+"'";
        int i = 0;
            try {
            Statement statement=Conn().createStatement();
            ResultSet resultSet=statement.executeQuery(q);
                while (resultSet.next()){
                    i++;
                }
            statement.close();
            } catch (Exception e) {
            throw new RuntimeException(e);
            }
        return i;
    }

    public Connection Conn(){
        Connection conn=null;
        try {
            Class.forName(classes);
            conn= DriverManager.getConnection(conUrl,username,password);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}
