// Main Dashboard Apex Chart Option Config
function apexChartDoughnut(title,series,labels, colors, isBuble) {
    let percent = ''
    let chartData = []

    // 모든 시리즈 데이터에 대해 유효성 검사 실행
    series.map(function(item) {
        console.log("apexChartDoughnut : " + item);
        // 숫자로 변환 후 유효성 검사
        let num = Number(item);
        chartData.push(isNaN(num) ? 0.00 : num.toFixed(2));
    });

    if(isBuble){
        percent = '%'
    }

    //donut Chart Option Start
    const options = {
        series:chartData.map(Number), //백단에서 값을 넘겨받아와야함
        labels:labels, //백단에서 값을 넘겨받아와야함
        legend: {
            show: true,
            position: 'bottom'
        },
        // title: {
        //     text: title,
        //     align: 'center'
        // },
        chart: {
            type: 'donut',
            height: 450,
            width: '100%'
        },
        colors: colors,
        responsive: [{
            breakpoint: 480
        }],
        plotOptions: { 
            pie:{
                customScale: 1,
                donut:{// donut 내부 값을 출력하고 크기를 지정하는 옵션
                    size: '50%', // 내부 크기 지정
                    labels: {
                        show: true,
                        name: { // 내분 값에 대한 font 크기 색 폰트 크기 설정
                            show: true,
                            fontSize: '30%',
                            fontFamily: 'Open Sans',
                            fontWeight: 500,
                            color: '#000000',
                        },
                        value: {
                            formatter: function(val) {
                                return Math.ceil(val) + percent;
                            },
                            style: {
                                fontSize: '11px',         // 작은 폰트
                                fontWeight: 'normal',     // 일반 두께
                            },
                            offsetY: -5,                  // 살짝 위로
                        }
                    }
                }
            }
        },
        dataLabels: {
            enabled: true,
            style: {
                fontSize: '11px',
                colors: ['rgba(24,1,1,0.94)'],
            },
            formatter: function(val, opts) {

                // 첫 번째 시리즈의 dataLabel을 숨김
                if (series.length == 2 && opts.seriesIndex === 1) {
                    return '';
                }
                return Math.ceil(val) + '%';
            }
        }
    }
    //donut Chart Option End

    return options;
}

function PerformanceScoreStatus(data, series_name, dataLabels_text, colors, xaxis_categories, yaxis_title, yaxis_max, yaxis_min,
                                yaxis_stepSize, yaxis_tickAmount, yaxis_toFixed, title, annotations_y_data, events) {
    // 차트 옵션 설정
    return {
        series: [{
            name: series_name,
            data: data // [전체 평균, 지점 평균, 내 점수]
        }],
        chart: {
            type: 'bar',
            height: 350,
            width: '100%',
            toolbar: {
                show: false
            },
            events: events || {}
        },
        plotOptions: {
            bar: {
                horizontal: false,
                columnWidth: '100%',
                borderRadius: 5,
                distributed: true
            }
        },
        dataLabels: {
            enabled: true,
            formatter: function (val) {
                return val + dataLabels_text;
            }
        },
        colors: colors,
        xaxis: {
            categories: xaxis_categories,
            labels: {
                show: false, // X 레이블 사용시 true로 변경
                style: {
                    fontSize: '12px'
                }
            }
        },
        yaxis: {
            title: {
                text: yaxis_title
            },
            max: yaxis_max || 100,
            min: yaxis_min || 0,
            stepSize: yaxis_stepSize || 1,
            tickAmount: yaxis_tickAmount || 1,
            labels: {
                formatter: function (val) {
                    return val.toFixed(yaxis_toFixed || 1);
                }
            }
        },
        // --- 4개의 등급 기준선(Annotations) 설정 ---
        annotations: {
            yaxis: [
                {
                    y: annotations_y_data[0],
                    borderColor: '#000000',
                    label: {
                        borderColor: '#00cb30',
                        style: { color: '#000000', background: '#00cb30' },
                        text: 'A',
                    },
                    strokeDashArray: 4, // 실선
                },
                {
                    y: annotations_y_data[1],
                    borderColor: '#000000',
                    label: {
                        borderColor: '#0064A6',
                        style: { color: '#000000', background: '#0064A6' },
                        text: 'B',
                    },
                    strokeDashArray: 4,
                },
                {
                    y: annotations_y_data[2],
                    borderColor: '#000000',
                    label: {
                        borderColor: '#EFFF00',
                        style: { color: '#000000', background: '#EFFF00' },
                        text: 'C',
                    },
                    strokeDashArray: 4,
                },
                // {
                //     y: annotations_y_data[3],
                //     borderColor: '#000000',
                //     label: {
                //         borderColor: '#ff0707',
                //         style: { color: '#000000', background: '#ff0707' },
                //         text: 'D',
                //     },
                //     strokeDashArray: 4,
                // }
            ]
        },
        title: {
            text: title || '',
            align: 'center',
            margin: 10,
            style: {
                fontSize: '16px'
            }
        }
    }
}