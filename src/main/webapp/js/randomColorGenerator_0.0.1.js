// 사용 예시
const availableColors = ['rgba(111,220,113,0.82)', 'rgba(0,255,187,0.77)', '#2196F3', '#00796B', '#4CAF50'];
function randomColor(){
    const randomIndex = Math.floor(Math.random() * availableColors.length);
    return availableColors[randomIndex];
}
//console.log('랜덤 선택된 색상:', randomColor());
