package com.newthinktank.derekbanas.mini_project_2.Attractions;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;

/**
 * Created by Huy on 8/5/2015.
 */
public class Attraction {
    public boolean isFavorite;
   public String name;
    public String phone;
    public String website;
    public String position;
    public int rating;
    public String opening;
    public String closing;
    public String icon;
    public  String image;
    public  String description;
    public  String address;

    public void setFavorite(boolean newValue)
    {
        isFavorite = newValue;
    }


    public Attraction(boolean isFavorite, String address, String name, String phone,
                      String website, String position, int rating,
                      String opening, String closing, String icon,
                      String image, String description) {
        this.isFavorite = isFavorite;
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.website = website;
        this.position = position;
        this.rating = rating;
        this.opening = opening;
        this.closing = closing;
        this.icon = icon;
        this.image = image;
        this.description = description;
    }

    public Attraction(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        name = jsonObject.getString("name");
        phone = jsonObject.getString("phone");
        website = jsonObject.getString("website");
        address = jsonObject.getString("address");
        description = jsonObject.getString("description");
        image = jsonObject.getString("image");
        icon = jsonObject.getString("icon");
        rating = jsonObject.getInt("rating");
        opening = jsonObject.getString("opening");
        closing =jsonObject.getString("closing");
        isFavorite =jsonObject.getBoolean("isFavorite");
        position=jsonObject.getString("position");
    }

    public String toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isFavorite",isFavorite);
        jsonObject.put("name", name);
        jsonObject.put("phone", phone);
        jsonObject.put("website",website);
        jsonObject.put("address",address);
        jsonObject.put("icon",icon);
        jsonObject.put("image",image);
        jsonObject.put("position",position);
        jsonObject.put("opening",opening);
        jsonObject.put("closing",closing);
        jsonObject.put("rating",rating);
        jsonObject.put("description",rating);
        return jsonObject.toString();
    }
}
