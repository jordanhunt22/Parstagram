package com.example.parstagram;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel(analyze={Post.class})
@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED = "createdAt";
    public static final String KEY_LIKES = "likes";

    public static ArrayList<String> postsLikedByCurrentuser = new ArrayList<String>();

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public JSONArray getLikes(){
        return getJSONArray(KEY_LIKES);
    }

    public void setLikes(String objectId){
        add(KEY_LIKES, objectId);
    }
}
