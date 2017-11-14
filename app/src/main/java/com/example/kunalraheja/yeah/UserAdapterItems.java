package com.example.kunalraheja.yeah;

/**
 * Created by Kunal Raheja on 26-07-2016.
 */
public class UserAdapterItems {
   public String uname , email , url ,uid;
    public boolean friend;

    public UserAdapterItems(String name, String email , String url , boolean friend,String uid) {
        this.uname = name;
        this.email = email;
        this.friend=friend;
        this.url=url;
        this.uid=uid;
    }

}
