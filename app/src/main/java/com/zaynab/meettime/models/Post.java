package com.zaynab.meettime.models;


import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {

    public Post() {
        super();
    }

    public ParseUser getOwner() {
        return getParseUser("owner");
    }

    public void setOwner(ParseUser usr) {
        put("owner", usr);
    }

    public Meeting getMeeting() {
        return ((Meeting) getParseObject("meeting"));
    }

    public void setMeeting(Meeting meeting) {
        put("meeting", meeting);
    }

    /*
     * post.isLaunched()-> owner has launched meeting
     * else -> owner has joined meeting
     */
    public Boolean isLaunched() {
        return getBoolean("launched");
    }

    public void setLaunched(boolean b) {
        put("launched", b);
    }

    public String getCaption() {
        return getString("caption");
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

    public ParseRelation<ParseUser> getLikes() {
        return getRelation("likes");
    }

    public void likeBy(ParseUser usr) {
        getLikes().add(usr);
        saveInBackground();
    }

    public void unlikeBy(ParseUser usr) {
        getLikes().remove(usr);
        saveInBackground();
    }

    public int getLikesCount() {
        return getInt("likesCount");
    }

    public void setLikesCount(int count) {
        put("likesCount", count);
    }

    public ParseRelation<Comment> getComments() {
        return getRelation("comments");
    }

    public void addComment(Comment comment) {
        getComments().add(comment);
        saveInBackground();
    }

    public void deleteComment(Comment comment) {
        getComments().remove(comment);
        saveInBackground();
    }

    public int getCommentsCount() {
        return getInt("commentsCount");
    }

    public void setCommentsCount(int count) {
        put("commentsCount", count);
    }
}
