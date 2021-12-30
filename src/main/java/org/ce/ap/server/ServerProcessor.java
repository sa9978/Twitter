package main.java.org.ce.ap.server;

import main.java.org.ce.ap.ServiceWordsEnum;
import main.java.org.ce.ap.server.exceptions.InvalidCharacterNumberException;
import main.java.org.ce.ap.server.exceptions.InvalidUsernameException;
import main.java.org.ce.ap.server.exceptions.SignUpExceptions;
import main.java.org.ce.ap.server.impl.ObserverServiceImpl;
import main.java.org.ce.ap.server.impl.TimelineServiceImpl;
import main.java.org.ce.ap.server.impl.TweetingServiceImpl;
import main.java.org.ce.ap.server.impl.AuthenticationServiceImpl;
import org.json.*;
import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ServerProcessor {
    private AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl();
    private TweetManager tweetManager = TweetManager.getInstance();
    private static UserAccount userAccount;
    private JSONObject response = new JSONObject();


    public ArrayList<JSONObject> toJsonArrayTweet(ArrayList<Tweet> list) {
        ArrayList<JSONObject> jsonList = new ArrayList<>();
        for (Tweet tweet : list) {
            jsonList.add((tweet).toJson());
        }
        return jsonList;
    }

    public ArrayList<String> getIds(ArrayList<User> users) {
        ArrayList<String> ids = new ArrayList<>();
        for (User user : users) {
            ids.add(user.getUsername());
        }
        return ids;
    }

    public JSONObject processRequest(JSONObject jsonObject) { ;
        JSONObject jsonParameters = (JSONObject) jsonObject.get("parameterValues");
        System.out.println();
        Gson gson = new Gson();

        ServiceWordsEnum method = gson.fromJson(jsonObject.getString("method"), ServiceWordsEnum.class);
        System.out.println(method);

        switch (method) {
            case SIGNIN:
                System.out.println("here?");
                try {

                    User user = authenticationService.signIn(jsonParameters.getString("username"),
                            jsonParameters.getString("password"));

                    userAccount = new UserAccount(user);
                    response.put("hasError", false);
                    response.put("count", 1);
                    response.put("result", new JSONObject(user.toJson()));
                } catch (Exception e) {
                    response.put("hasError", true);
                    response.put("count",1);
                    response.put("errorCode", e.getClass().toString());
                } finally {
                    return response;
                }


//                tweetManager.getDataFromDatabase(userAccount.getUser());

            case SIGNUP:
                System.out.println("here!?");
                return signUp(jsonParameters);

            case TWEET:
                try {
                    Tweet tweet = userAccount.addNewTweet((String) jsonParameters.get("text"));
                    response.put("hasError", false);
                    response.put("count", 1);
                    response.put("result", new JSONObject(tweet.toJson()));
                }catch (InvalidCharacterNumberException e){
                    response.put("hasError", true);
                    response.put("errorCode","InvalidCharacterNumberException");
                }catch (Exception e){
                    response.put("hasError", true);
                    response.put("errorCode","AuthenticationException");
                }finally {
                    return response;
                }

            case REMOVETWEET:
                try {
                    userAccount.removeTweet((Tweet) jsonParameters.get("tweet"));
                    response.put("hasError", false);
                    response.put("count",0);
                }catch (Exception e){
                    response.put("hasError", true);
                    response.put("errorCode","NotAccessException");
                }finally {
                    return response;
                }

            case RETWEET:
                try {
                    Retweet retweet =userAccount.retweet((Tweet) jsonParameters.get("tweet"), (String) jsonParameters.get("text"));
                    response.put("hasError", false);
                    response.put("count", 1);
                    response.put("result", new JSONObject(retweet.toJson()));
                }catch (InvalidCharacterNumberException e){
                    response.put("hasError", true);
                    response.put("errorCode","InvalidCharacterNumberException");
                }catch (Exception e){
                    response.put("hasError", true);
                    response.put("errorCode","AuthenticationException");
                }finally {
                    return response;
                }

            case REMOVERETWEET:
                try {
                    userAccount.removeRetweet((Tweet) jsonParameters.get("tweet"), (Retweet) jsonParameters.get("retweet"));
                    response.put("hasError", false);
                    response.put("count",0);
                }catch (Exception e){
                    response.put("hasError", true);
                    response.put("errorCode","NotAccessException");
                }finally {
                    return response;
                }

            case LIKE:
                try {
                    userAccount.like((Tweet) jsonParameters.get("tweet"));
                    response.put("hasError", false);
                    response.put("count", 1);
                    response.put("result", new JSONObject(jsonParameters.get("tweet")));
                }catch (Exception e){
                    response.put("hasError", true);
                    response.put("errorCode","NotAccessException");
                }finally {
                    return response;
                }

            case DISLIKE:
                userAccount.unLike((Tweet) jsonParameters.get("tweet"));
                break;
            case REPLY:
                userAccount.reply((Tweet) jsonParameters.get("tweet"), (Tweet) jsonParameters.get("replyTweet"));
                break;
            case REMOVEREPLY:
                userAccount.removeReply((Tweet) jsonParameters.get("tweet"), (Tweet) jsonParameters.get("replyTweet"));
                break;

        }
        return null;
    }

    private JSONObject signUp(JSONObject jsonParameters){
        try {
            User user = authenticationService.signUp(jsonParameters.getString("firstName")
                    , jsonParameters.getString("lastName"), jsonParameters.getString("username"),
                    jsonParameters.getString("password"),
                    LocalDate.parse(jsonParameters.getString("birthDate")));
            userAccount = new UserAccount(user);
            response.put("hasError", false);
            response.put("count", 1);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(user.toJson());
            response.put("result", jsonArray);
        } catch (SignUpExceptions e) {
            response.put("hasError", true);
            response.put("count",e.getMessages().size());
            response.put("errorCode", e.getMessages());
        }catch (Exception e){
            System.out.println(e);
        }
        finally {
            System.out.println(response);
            return response;
        }
    }
    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user", userAccount.getUser().toJson());
        jsonObject.put("tweets", toJsonArrayTweet(userAccount.getTweets()));
        jsonObject.put("followers", getIds(userAccount.getUser().getFollowers()));
        jsonObject.put("followings", getIds(userAccount.getUser().getFollowings()));
        return jsonObject;
    }

}

