package com.jobmoa.app.CounselMain.biz.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginBean {
    private String memberUserID;
    private String memberBranch;
    private String memberUserName;
    private String memberRole;
    private String memberUniqueNumber;


    public String memberUserID(){
        return memberUserID;
    }
    public String memberBranch(){
        return memberBranch;
    }
    public String memberUserName(){
        return memberUserName;
    }

    public String memberRole(){
        return memberRole;
    }
    public String memberUniqueNumber(){
        return memberUniqueNumber;
    }
}