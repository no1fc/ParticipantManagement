package com.jobmoa.app.CounselMain.biz.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 세션 정보 저장 객체.
 * HTTPSession의 JOBMOA_LOGIN_DATA 속성에 저장되어 인증된 사용자의 계정, 지점, 권한 정보를 유지한다.
 */
@Getter
@Setter
public class LoginBean {
    private String memberUserID;
    private String memberBranch;
    private String memberUserName;
    private String memberRole;
    private String memberUniqueNumber;
    private String permissionGroup;


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