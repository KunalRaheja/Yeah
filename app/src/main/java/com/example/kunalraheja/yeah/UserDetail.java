package com.example.kunalraheja.yeah;

/**
 * Created by Kunal Raheja on 13-07-2016.
 */
public class UserDetail {

    public UserDetail(){
        //
    }


    private String uName, status = "Hi there !!", uPhone, email , bday , photo_url="";



    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String location) {
        this.status = location;
    }

    public String getPhone() {
        return uPhone;
    }

    public void setPhone(String uId) {
        this.uPhone = uId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
}
