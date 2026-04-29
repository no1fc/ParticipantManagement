$(document).ready(function(){
    let SESSION_TIME = $("#sessionTimeHidden").val();
    SESSION_TIME = Number(SESSION_TIME);

    let now = new Date(SESSION_TIME);

    let nowMonth = now.getMonth() + 1;
    nowMonth = nowMonth < 10 ? "0" + nowMonth : nowMonth;
    let nowDate = now.getDate();
    nowDate = nowDate < 10 ? "0" + nowDate : nowDate;

    let nowHour = now.getHours();
    nowHour = nowHour < 10 ? "0" + nowHour : nowHour;
    let nowMinute = now.getMinutes();
    nowMinute = nowMinute < 10 ? "0" + nowMinute : nowMinute;

    let sessionDay = now.getFullYear() + "-" + nowMonth + "-" + nowDate;
    let sessionTime = nowHour + ":" + nowMinute;
    $("#sessionTime").text(sessionDay +" "+ sessionTime);
})