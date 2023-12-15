package com.example.adviser.vo;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String userEmail;
    private String userPw;
    private String userName;
    private String userNick;
    private String userPhone;
    private String userBirthyear;
    private String userGender;
    private String userloginType;
    private String joinedAt;
    private String approvedAt;
    private String userAuth;
    private String userRole;
    private String userProfit;
    private String userToken;

    public User(String userEmail, String userPw, String userName, String userNick, String userPhone, String userBirthyear, String userGender, String userloginType, String joinedAt, String approvedAt, String userAuth, String userRole, String userProfit, String userToken) {
        this.userEmail = userEmail;
        this.userPw = userPw;
        this.userName = userName;
        this.userNick = userNick;
        this.userPhone = userPhone;
        this.userBirthyear = userBirthyear;
        this.userGender = userGender;
        this.userloginType = userloginType;
        this.joinedAt = joinedAt;
        this.approvedAt = approvedAt;
        this.userAuth = userAuth;
        this.userRole = userRole;
        this.userProfit = userProfit;
        this.userToken = userToken;
    }

    public User() {
        this.userEmail = userEmail;
        this.userPw = userPw;
        this.userName = userName;
        this.userNick = userNick;
        this.userPhone = userPhone;
        this.userBirthyear = userBirthyear;
        this.userGender = userGender;
        this.userloginType = userloginType;
        this.joinedAt = joinedAt;
        this.approvedAt = approvedAt;
        this.userAuth = userAuth;
        this.userRole = userRole;
        this.userProfit = userProfit;
        this.userToken = userToken;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserBirthyear() {
        return userBirthyear;
    }

    public void setUserBirthyear(String userBirthyear) {
        this.userBirthyear = userBirthyear;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserloginType() {
        return userloginType;
    }

    public void setUserloginType(String userloginType) {
        this.userloginType = userloginType;
    }

    public String getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(String joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(String userAuth) {
        this.userAuth = userAuth;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getUserProfit() {
        return userProfit;
    }

    public void setUserProfit(String userProfit) {
        this.userProfit = userProfit;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public JSONObject createJSONObject(User user) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userEmail", user.getUserEmail());
            jsonObject.put("userName", user.getUserName());
            jsonObject.put("userNick", user.getUserNick());
            jsonObject.put("userPhone", user.getUserPhone());
            // 필요한 다른 필드들도 추가

            // 예시로 추가된 필드
            jsonObject.put("userloginType", user.getUserloginType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}