package com.csvsoft.smark.entities;

import java.util.LinkedList;
import java.util.List;

public class UserCredential {
    private String userId;
    private String userName;
    private String password;
    private List<UserRole> userRoles;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void addUserRole(UserRole userRole){
        if(this.userRoles==null){
            this.userRoles = new LinkedList<>();
        }
        this.userRoles.add(userRole);
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public boolean isReadOnly(){
        if(this.userRoles==null){
            return true;
        }
       return  this.userRoles.stream().filter(ur->ur.getRoleName().toLowerCase().contains("readonly")).count()!=0;
    }
}
