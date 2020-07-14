package com.zaynab.meettime.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {

    public Comment() {
        super();
    }

    public String getText() {
        return getString("commentText");
    }

    public void setText(String comment) {
        put("commentText", comment);
    }

    public ParseUser getOwner() {
        return getOwner();
    }

    public void setOwner(ParseUser usr) {
        put("owner", usr);
    }

    public Post getPost() {
        return getPost();
    }

    public void setPost(Post post) {
        put("post", post);
    }
}
