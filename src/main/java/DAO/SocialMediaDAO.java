package DAO;
import Model.Account;
import Model.Message;
import Util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONObject;
import org.h2.util.json.JSONString;

public class SocialMediaDAO {

    public SocialMediaDAO(){

    }
    
    //does actual SQL queries to databases to get stuff done, used by SocialMediaService

    public Account DAOcreateAccount(String username, String password) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO Account (username, password) VALUES (?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()){
                int generatedId = rs.getInt(1);
                return new Account(generatedId,username,password);
            }

        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage() + '\n');
        }
        return null;
    }

    public Account DAOloginAccount(String username, String password) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM Account WHERE username = ? AND password = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            preparedStatement.execute();
            ResultSet rs = preparedStatement.getResultSet();
            if(rs.next()){
                System.out.println("this happens");
                return new Account(rs.getInt(1),rs.getString(2),rs.getString(3));
            }

        }catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage() + '\n');
        }
        return null;
    }

    public Message DaoSendMessage(int posted_by, String message_text, long time_posted_epoch) throws SQLException {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

             preparedStatement.setInt(1, posted_by); //note: posted_by is the int id of the user
             preparedStatement.setString(2, message_text);
             preparedStatement.setLong(3, time_posted_epoch);

            preparedStatement.executeUpdate();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()){
                int generatedId = rs.getInt(1);
                return new Message(generatedId,posted_by,message_text,time_posted_epoch);
            }

        } catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage() + '\n');
        }
        return null;
    }

    public List<Message> DaoGetAllMessages() {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM Message;";
            Statement s = connection.createStatement();

            s.execute(sql);
            ResultSet rs = s.getResultSet();

            List<Message> rsmessages = new ArrayList<Message>();;
            while(rs.next()){
                Message tempmessage = new Message(rs.getInt(1),rs.getInt(2),
                rs.getString(3),rs.getLong(4));

                rsmessages.add(tempmessage);
            }
            return rsmessages;


        }catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage() + '\n');
        }
        return null;
    }

    public Message DaoGetMessageById(int id){
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM Message WHERE message_id = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            preparedStatement.execute();
            ResultSet rs = preparedStatement.getResultSet();
            if(rs.next()){
                return new Message(rs.getInt(1), rs.getInt(2),rs.getString(3),rs.getLong(4));
            }

        }catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage() + '\n');
        }
        return null;
    }


    public Message DaoDeleteMessage(int id){
        Message messageToDelete = DaoGetMessageById(id);
        if(messageToDelete != null){
            Connection connection = ConnectionUtil.getConnection();
            try {
                String sql = "DELETE FROM Message WHERE message_id = ?;";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setInt(1, id);

                preparedStatement.execute();

                return messageToDelete;

            }catch (SQLException e) {
                System.out.println("An error occurred: " + e.getMessage() + '\n');
            }
        }
        
        return null;
    }

    public Message DaoUpdateMessage(String new_message, int id){
        Message messageToUpdate = DaoGetMessageById(id);
        if(messageToUpdate != null){
            Connection connection = ConnectionUtil.getConnection();
            try {
                String sql = "UPDATE Message SET message_text = ? WHERE message_id = ?;";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, new_message);
                preparedStatement.setInt(2,id);

                preparedStatement.execute();

                ResultSet rs = preparedStatement.getResultSet();

                return new Message(messageToUpdate.getMessage_id(),
                    messageToUpdate.getPosted_by(),new_message,messageToUpdate.getTime_posted_epoch());
                

            }catch (SQLException e) {
                System.out.println("An error occurred: " + e.getMessage() + '\n');
            }
        }
        
        return null;
    }

    public List<Message> DaoGetMessagesByUser(int id){
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM Message WHERE posted_by = ?;";
            PreparedStatement s = connection.prepareStatement(sql);

            s.setInt(1, id);

            s.execute();
            ResultSet rs = s.getResultSet();

            List<Message> rsmessages = new ArrayList<Message>();
            while(rs.next()){
                Message tempmessage = new Message(rs.getInt(1),rs.getInt(2),
                rs.getString(3),rs.getLong(4));

                rsmessages.add(tempmessage);
            }
            return rsmessages;


        }catch (SQLException e) {
            System.out.println("An error occurred: " + e.getMessage() + '\n');
        }
        return null;
    }


    
}
