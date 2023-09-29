package Service;
import Model.Account;
import Model.Message;

import java.sql.SQLException;
import java.util.List;

import DAO.SocialMediaDAO;

public class SocialMediaService {
    //uses DAO to do functions
    private SocialMediaDAO DAO = new SocialMediaDAO();

    public SocialMediaService(){

    }

    public Account createAccount(String username, String password) throws SQLException {
        if(username.length() > 0 && password.length() > 3){
            return DAO.DAOcreateAccount(username,password);
        }
        return null;
    }

    public Account loginAccount(String username,String password) throws SQLException {
        return DAO.DAOloginAccount(username, password);
    }

    public Message sendMessage(int posted_by, String message_text, long time_posted_epoch) throws SQLException {
        if(message_text.length() < 255 && message_text.length() > 0){
            return DAO.DaoSendMessage(posted_by, message_text, time_posted_epoch);
        }
        return null;
    }

    public List<Message> getMessages(){
        return DAO.DaoGetAllMessages();
    }

    public Message getMessageById(int id){
        return DAO.DaoGetMessageById(id);
    }

    public Message deleteMessage(int id){
        return DAO.DaoDeleteMessage(id);
    }

    public Message updateMessage(String new_message, int id){
        if(new_message.length() > 0 && new_message.length() < 255){
            return DAO.DaoUpdateMessage(new_message, id);
        }
        return null;
    }

    public List<Message> getMessagesByUser(int id){
        return DAO.DaoGetMessagesByUser(id);
    }


}
