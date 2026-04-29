// chart.js 중앙 텍스트 플러그인 정의 및 등록
const drawCenterTextPlugin = {
    id: 'drawCenterText',
    beforeDraw: function (chart) {
        const width = chart.width;
        const height = chart.height;
        const ctx = chart.ctx;

        // 중앙 텍스트 내용 가져오기
        const text = chart.options.plugins.drawCenterText?.text || "";
        const denominator = chart.options.plugins.drawCenterText?.denominator || 100;
        // 폰트 및 텍스트 정보 설정
        ctx.restore();
        var fontSize = (height / denominator).toFixed(2); // 글자 크기 지정
        ctx.font = fontSize + "em sans-serif";
        ctx.textBaseline = "middle";

        // 중앙에 들어갈 텍스트와 위치 계산
        const textX = Math.round((width - ctx.measureText(text).width) / 2);
        const textY = height / 2;

        // 텍스트 출력
        ctx.fillText(text, textX, textY);
        ctx.save();
    }
};