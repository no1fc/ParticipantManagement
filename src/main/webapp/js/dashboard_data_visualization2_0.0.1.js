$(document).ready(function(){
    //임시데이터
    const Datas = initData;

    //세번째 프레임
    let stackedBarOptions = {
        series: [{
            name: '해당',
            type: 'column',
            data: Datas.correInventive.data
        },
            {
                name: '미해당',
                type: 'column',
                data: Datas.notCorreInventive.data
            },
            {
                name: '서비스 미제공',
                type: 'column',
                data: Datas.noServiceInventive.data
            },
            {
                name: '서비스 미제공 비율',
                type: 'line',
                data: Datas.noServiceInventive.data.map((val, idx) => {
                    const total = val + Datas.correInventive.data[idx] + Datas.notCorreInventive.data[idx];
                    return ((val / total) * 100).toFixed(1);
                })
            }],
        chart: {
            type: 'line',
            height: 350,
            stacked: true,

        },
        // 기본 색상 설정
        colors: ['#2196F3', '#FFC107', '#FF5252', '#D32F2F'],
        xaxis: {
            categories: Datas.correInventive.branch,
        },
        plotOptions: {
            bar: {
                horizontal: false, // 세로 방향으로 변경
                dataLabels: {
                    formatter: function(val) {
                        return val + '건';
                    }
                }
            }
        },
        yaxis: [
            {
                title: {
                    text: '전체 건수'
                },
                labels: {
                    formatter: function(val, opts) {
                        // seriesIndex를 사용하여 현재 위치 확인
                        const currentIndex = opts?.seriesIndex || 0;

                        // y축 값에 따라 다른 단위 적용
                        if (currentIndex === 3) {  // 4번째 데이터
                            return Math.floor(val) + '%';
                        }
                        return Math.floor(val) + '건';
                    }
                },
                min:0
            }
            ],
        dataLabels: {
            enabled: true,
            formatter: function(val, { seriesIndex }) {
                if (seriesIndex === 3) {
                    return Math.floor(val) + '%';
                }
                return Math.floor(val) + '건';
            },
            textAnchor: 'middle',
            minAngle: 0,
            style: {
                colors: ['#000000','#000000','#000000','#ff0000'],
                fontWeight: 500
            }
        },
        title: {
            text: '인센 현황',
            align: 'left'
        }
    };

    // 차트 렌더링
    new ApexCharts(document.querySelector("#dashboard_inventive_chart"), stackedBarOptions).render();
})