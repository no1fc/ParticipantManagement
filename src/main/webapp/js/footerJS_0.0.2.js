/**
 * @file 푸터 영역 스크립트 (관리자 이메일 링크)
 * @version 0.0.2
 * @requires jQuery
 */
$(document).ready(function () {
    // 사이트 관리자 이메일 링크 설정 (다우오피스 메일 작성 화면)
    const emailTag = $("#email-a-tag");
    emailTag.attr("href", "https://jobmoa.daouoffice.com/app/mail?work=write&toAddr=%EB%82%A8%EC%83%81%EB%8F%84%3Cnamsd%40jobmoa.com%3E");
});
