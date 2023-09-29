package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.h2.util.json.JSONObject;
import org.h2.util.json.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.SocialMediaService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {

    private SocialMediaService socialMediaService = new SocialMediaService();

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // THE FOLLOWING ENDPOINTS ARE ALL THE ENDPOINTS REQUIRED FOR THE PROJECT AND MUST BE COMPLETED
        //complete handler methods below this startAPI() method

        //start page
        app.get("/", this::startHandler);

        //register new user - return 200 if OK, 401 if failure
        app.post("/register", this::registerHandler);

        //login - return 200 if OK, 401 if failure
        app.post("/login", this::loginHandler);

        //send a message - return 200 if OK, 400 if failure
        app.post("/messages",this::sendMessageHandler);

        //get all messages - always return 200 (even if empty message list)
        app.get("/messages",this::getMessagesHandler);

        //get message by its message id (TODO: review PathParams to complete) - always return 200 even if given id is empty
        app.get("/messages/{message_id}",this::getMessageByIdHandler);


        //delete a message with a given id - always return 200 even if no message with given id
        app.delete("/messages/{message_id}", this::deleteMessageHandler);

        //update a message with a given id - return 200 if OK, 400 if failure
        app.patch("/messages/{message_id}",this::updateMessageHandler);

        //get all messages from an account with the given id - always return 200 even if no messages
        app.get("/accounts/{account_id}/messages",this::getAllMessagesfromAccount);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void startHandler(Context context) {
        context.html("App started");
    }

    //use objectmapper to read context.json
    //create account in Service, which uses DAO to query database
    //return 400 if null, 200 otherwise
    private void registerHandler(Context context) throws JsonProcessingException, SQLException {
        
        ObjectMapper map = new ObjectMapper();
        Account readAccount = map.readValue(context.body(), Account.class);

        Account createdAccount = socialMediaService.createAccount(readAccount.getUsername(), readAccount.getPassword());

        if(createdAccount != null){
            context.json(map.writeValueAsString(createdAccount));
            context.status(200);
        }else{
            context.status(400);
        }
    }

    private void loginHandler(Context context) throws JsonProcessingException, SQLException {
        ObjectMapper map = new ObjectMapper();
        Account readAccount = map.readValue(context.body(),Account.class);

        Account loginAccount = socialMediaService.loginAccount(readAccount.getUsername(), readAccount.getPassword());
        if(loginAccount != null){
            context.json(map.writeValueAsString(loginAccount));
            context.status(200);
        }else{
            context.status(401);
        }

    }

    private void sendMessageHandler(Context context) throws JsonProcessingException, SQLException {
        ObjectMapper map = new ObjectMapper();

        Message readMessage = map.readValue(context.body(),Message.class);

        Message newMessage = socialMediaService.sendMessage(readMessage.getPosted_by(),
        readMessage.getMessage_text(),readMessage.getTime_posted_epoch());

        if(newMessage != null){
            context.json(map.writeValueAsString(newMessage));
            context.status(200);
        } else{
            context.status(400);
        }
    }

    private void getMessagesHandler(Context context) throws JsonProcessingException, SQLException {
        ObjectMapper map = new ObjectMapper();

        List<Message> allmessages = socialMediaService.getMessages();
        context.json(map.writeValueAsString(allmessages));
        context.status(200);
    }

    private void getMessageByIdHandler(Context context) throws JsonProcessingException, SQLException {
        ObjectMapper map = new ObjectMapper();

        Message foundMessage = socialMediaService.getMessageById(Integer.parseInt(context.pathParam("message_id")));
        if(foundMessage != null){
            context.json(map.writeValueAsString(foundMessage));
        }
        context.status(200);
    }

    private void deleteMessageHandler(Context context) throws JsonProcessingException, SQLException {
        ObjectMapper map = new ObjectMapper();

        Message deletedMessage = socialMediaService.deleteMessage(Integer.parseInt(context.pathParam("message_id")));

        if(deletedMessage != null){
            context.json(map.writeValueAsString(deletedMessage));
        }

        context.status(200);
    }

    private void updateMessageHandler(Context context) throws JsonProcessingException, SQLException {
        ObjectMapper map = new ObjectMapper();

        Message temp = map.readValue(context.body(),Message.class);

        Message updatedMessage = socialMediaService.updateMessage(temp.getMessage_text(), 
                                Integer.parseInt(context.pathParam("message_id")));

        if(updatedMessage != null){
            context.json(map.writeValueAsString(updatedMessage));
            context.status(200);
        } else{
            context.status(400);
        }

    }

    private void getAllMessagesfromAccount(Context context) throws JsonProcessingException, SQLException {
        ObjectMapper map = new ObjectMapper();

        List<Message> messages = socialMediaService.getMessagesByUser(Integer.parseInt(context.pathParam("account_id")));

        context.json(map.writeValueAsString(messages));
        context.status(200);
    }


}