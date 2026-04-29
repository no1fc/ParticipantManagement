package com.jobmoa.app.CounselMain.biz.bean;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@Getter
public class PaginationBean {

    private int totalButton;
    private int startButton;
    private int endButton;
    private int startPage;
    private int endPage;

    public void paginationProject(int page,int pageRows,int limitButton,int totalCount){
        // 실제 버튼 개수
        this.totalButton = (int) Math.ceil(totalCount * 1.0 / pageRows);
        log.info("totalButton : [{}]", this.totalButton);
        // 시작 버튼
        this.startButton = (page - 1) / limitButton * limitButton + 1;
        // 끝 버튼
        this.endButton = Math.min(this.startButton + limitButton - 1, this.totalButton);
        log.info("startButton : [{}]", this.startButton);
        log.info("endButton : [{}]", this.endButton);
        log.info("totalButton : [{}]", this.totalButton);
        /*
            1,2,3,4,5
            1 -> 0 : 10
            (page - 1) * 10 : => +10
            2 -> 10 : 20
            (page - 2) * 10 : => +10
            3 -> 20 : 30
         */
        // 시작 페이지
        this.startPage = (page - 1) * pageRows;
        // 끝 페이지
        this.endPage = this.startPage+pageRows;
    }

}
