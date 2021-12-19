package main.java.org.ce.ap.server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import main.java.org.ce.ap.server.exceptions.InvalidCharacterNumberException;
import org.json.JSONObject;

public class Tweet implements JsonInterface{

    private final User author;
    private String text;
    private HashSet<User> likes;
    private ArrayList<Tweet> replies;
    private final LocalDateTime sendDate;
    private ArrayList<Retweet> retweets;

    /**
     * create a new object from tweet
     *
     * @param author is using for setting the tweet's author
     * @param text   is using for setting the tweet's text
     */
    public Tweet(User author, String text) throws InvalidCharacterNumberException {
        checkTweetCharacters(text);
        this.author = author;
        this.text = text;
        this.sendDate = LocalDateTime.now();
        this.likes = new HashSet<>();
        this.replies = new ArrayList<>();
        retweets = new ArrayList<>();
    }

    /**
     * @param liker is using for adding liker
     */
    public void likeTweet(User liker) {
        likes.add(liker);
    }

    /**
     * @param liker is using for removing liker
     */
    public void removeLike(User liker) {
        if (!likes.contains(liker)) {
            System.err.println("liker not found");
        }
        likes.remove(liker);

    }

    /**
     * add new reply for the tweet
     *
     * @param tweet the tweet's reply
     */
    public void addNewReply(Tweet tweet) {
        replies.add(tweet);
    }

    /**
     * remove the reply of the tweet
     *
     * @param tweet the tweet's reply
     */
    public void removeReply(Tweet tweet) {
        if (!likes.contains(tweet)) {
            System.err.println("reply not found");
        }
        replies.remove(tweet);
    }

    /**
     * get the tweet's text
     *
     * @return text field
     */
    public String getText() {
        return text;
    }

    /**
     * edit(set) the tweet's text
     *
     * @param text tweet's text
     */
    public void editText(String text) {
        this.text = text;
    }

    /**
     * get the tweet's likes list
     *
     * @return likes field
     */
    public HashSet<User> getLikes() {
        return likes;
    }

    /**
     * get the tweet's reply list
     *
     * @return replies field
     */
    public ArrayList<Tweet> getReplies() {
        return replies;
    }

    /**
     * get the tweet's sendDate
     *
     * @return sendDate field
     */
    public LocalDateTime getSendDate() {
        return sendDate;
    }

    /**
     * @return tweet's author
     */
    public User getAuthor() {
        return author;
    }

    /**
     * @param text tweet's text
     * @throws InvalidCharacterNumberException if the number is greater than 256 or the text is null
     */
    private void checkTweetCharacters(String text) throws InvalidCharacterNumberException {
        if (text.length() == 0 || text == null) {
            throw new InvalidCharacterNumberException("Text shouldn't be empty!");
        } else if (text.length() > 256) {
            throw new InvalidCharacterNumberException("The number of characters is invalid, it should be lower than 256 characters!");
        }
    }

    /**
     * @param retweet is the tweet that we want to publish this tweet again
     */
    public void addRetweet(Retweet retweet) {
        if (!retweets.contains(retweet))
            retweets.add(retweet);
    }

    public int getRetweetNumber() {
        return retweets.size();
    }

    public int getLikeNumber() {
        return likes.size();
    }

    /**
     * @param retweet is the tweet that we want to remove it
     */
    public void removeRetweet(Retweet retweet) {
        retweets.remove(retweet);
    }

    @Override
    public String toString() {
        String str = author + " : \t" + text + "\n";
        str += "retweets: " + retweets.size() + "\t" + "likes: " + likes.size() + "\n";
        if (LocalDateTime.now().getDayOfYear() - sendDate.getDayOfYear() < 7) {
            str += sendDate.getDayOfWeek() + "\t" + sendDate.getHour() + ":" + sendDate.getMinute() + ":" + sendDate.getSecond() + "\n";
        } else {
            str += sendDate.getDayOfMonth() + " " + sendDate.getMonth() + "\t" + sendDate.getHour() + "\n";
        }

        if (replies.size() != 0) {
            str += "replies :\n";
        }
        for (Tweet tweet : replies) {
            str = str + "\t" + tweet;
        }
        str += "----------------------------------------------------\n";
        return str;
    }
    public ArrayList<JSONObject> toJsonArrayTweet(ArrayList<Tweet> list){
        ArrayList<JSONObject> jsonList = new ArrayList<>();
        for (Tweet tweet : list)
        {
            jsonList.add((tweet).toJson());
        }

        return jsonList;
    }
    public ArrayList<JSONObject> toJsonArrayRetweet(ArrayList<Retweet> list){
        ArrayList<JSONObject> jsonList = new ArrayList<>();
        for (Retweet retweet : list)
        {
            jsonList.add((retweet).toJson());
        }

        return jsonList;
    }
    @Override
    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("author",author.toJson());
        jsonObject.put("text",text);
        jsonObject.put("likes",likes);
        jsonObject.put("replies",toJsonArrayTweet(replies));
        jsonObject.put("sendDate",sendDate);
        jsonObject.put("retweets",toJsonArrayRetweet(retweets));
        return jsonObject;
    }
}
