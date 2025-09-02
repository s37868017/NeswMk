package com.sql_connect;

import android.annotation.SuppressLint;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBase{
    protected static String classes="com.mysql.jdbc.Driver";
    protected static String ip="193.124.33.173";
    protected static String port="3306";
    protected static String db="article";
    protected static String username="article_user";
    protected static String password="article";
    public String table="articles";
    protected static String conUrl="jdbc:mysql://"+ip+":"+port+"/"+db;
    protected static String columnName="title";
    protected static String columnIntro="intro";
    protected static String columnId="id";
    protected static String columnText="text";
    public String errorDb;

    public void insert(String title,String intro,String text,int id) {
        try{
            String q="INSERT INTO `"+table+"` (title, intro, text, user) VALUES ('"+title+"', '"+intro+"', '"+text+"', '"+id+"')";
            Statement statement=Conn().createStatement();
            statement.executeUpdate(q);
            statement.close();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteArticle() {
        try{
            String q="DELETE FROM "+table+" WHERE id = "+TextActivity.id;
            Statement statement=Conn().createStatement();
            statement.executeUpdate(q);
            statement.close();
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void editArticle(String title,String intro,String text,String id){
        try{
            String q="UPDATE "+table+" SET "+columnName+" = '"+title+"', "+columnIntro+" = '"+intro+"', "+columnText+" = '"+text+"' WHERE id = "+id;
            Statement statement=Conn().createStatement();
            statement.executeUpdate(q);
            statement.close();
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Map<String,String>> getData(){
        errorDb=null;
        List<Map<String,String>>data=null;
        data=new ArrayList<Map<String, String>>();
        String q="SELECT * , (SELECT login FROM users WHERE id = articles.user) FROM articles ORDER BY createdate DESC";
        Calendar calendar=Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd.MM.yyyy");
        try {
            Statement statement=Conn().createStatement();
            ResultSet resultSet = statement.executeQuery(q);
            while (resultSet.next()) {
                Map<String, String> dtName = new HashMap<String, String>();
                dtName.put("1", resultSet.getString(columnName));
                dtName.put("2", resultSet.getString(columnIntro));
                dtName.put("3", resultSet.getString(columnId));
                dtName.put("4",resultSet.getString("text"));
                if (resultSet.getString("createdate")!=null) {
                    calendar.setTime(resultSet.getDate("createdate"));
                    dtName.put("5", simpleDateFormat.format(calendar.getTime()));
                }else {
                    dtName.put("5", "не указана");
                }
                dtName.put("6",resultSet.getString("user"));
                dtName.put("7",resultSet.getString("(SELECT login FROM users WHERE id = articles.user)"));
                data.add(dtName);
            }
            statement.close();
        }catch (Exception ex){
            this.errorDb= ex.toString();
        }
        return data;
    }

    public String getText(){
        String textArticle = null;
        String q="SELECT `text` FROM "+table+" WHERE id = "+MainActivity.sItem;
        try {
            Statement statement=Conn().createStatement();
            ResultSet resultSet=statement.executeQuery(q);
            while (resultSet.next()) {
                textArticle = resultSet.getString("text");
            }
            statement.close();
        }catch (Exception ex){
            this.errorDb= ex.toString();
        }
        return textArticle;
    }

    public Connection Conn(){
        Connection conn=null;
        try {
            Class.forName(classes);
            conn=DriverManager.getConnection(conUrl,username,password);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}
