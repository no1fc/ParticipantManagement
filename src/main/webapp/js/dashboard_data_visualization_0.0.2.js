$(document).ready(function(){
    //임시데이터
    const Datas = initData;

    let counselChart = $('#counselorChart');
    let counselModel = $('#counselorModal');
    let view = $("#viewText");


    //첫번째 프레임 필터

    //두번째 프레임
    // ApexCharts를 사용한 복합 그래프 구현
    let options = {
        series: [{
            name: '당해년도',
            type: 'column',
            data: Datas.thisSuccess.data
        }, {
            name: '전년도',
            type: 'line',
            data: Datas.previousSuccess.data
        }],
        chart: {
            height: 350,
            type: 'line',
            events: {
                dataPointSelection: function(event, chartContext, config) { // click 대신 dataPointSelection 사용
                    const branchIndex = config.dataPointIndex;
                    const branchName = Datas.thisSuccess.branch[branchIndex];

                    // 전체 지점이 아닌 경우에만 모달 표시
                    if (branchIndex !== 0) {
                        showConsultantModal(branchName);
                    }
                }
            }
        },
        stroke: {
            width: [0, 3] // column은 0, line은 3
        },
        xaxis: {
            categories: Datas.thisSuccess.branch,
        },
        yaxis: {
            title: {
                text: '성공금액 (만원)'
            },
            labels: {
                formatter: function(val) {
                    return val.toLocaleString() + '만원';
                }
            }
        },
        dataLabels: {
            enabled: true,
            formatter: function(val) {
                return val.toLocaleString() + '만원';
            },
            minAngle: 0
        },
        title: {
            text: '지점별 성공금 현황',
            align: 'left'
        }
    };


    /**
     * 지점별 상담사 성공금 현황을 모달로 표시하는 함수
     * @param {string} branchName - 선택된 지점명
     */
    function showConsultantModal(branchName) {
        let startDate = $("#dashBoardStartDate").val();
        let endDate = $("#dashBoardEndDate").val();
        let jsonData = {
            consultants: [],
            amounts: []
        };

        fetch('/dashBoardSuccess.login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                dashboardBranch: branchName,
                dashBoardStartDate: startDate,
                dashBoardEndDate: endDate
            })
        }).then(async r => {
            let result = await r.json();

            let resultCheck;

            try {
                resultCheck = JSON.parse(result);
            } catch (e) {
                console.error('Failed to parse JSON response:', e);
                resultCheck = result;
            }

            resultCheck.forEach(element => {
                jsonData.consultants.push(element.consultants);
                jsonData.amounts.push(element.amounts/10000);
            })
            // console.log(jsonData);
        })

        view.html('<div id="spinner-border" class="text-center">데이터를 불러오는 중입니다.<div class="spinner-border" role="status"></div></div>')
        console.log(jsonData);
        // 모달 제목 업데이트
        $('#counselorModal .modal-title').text(`${branchName} 상담사 성공금 현황`);

        // 상담사 차트 옵션
        const consultantChartOptions = {
            series: [{
                name: '성공금',
                data: jsonData.amounts
            }],
            chart: {
                type: 'bar',
                height: 350,
                toolbar: {
                    show: true,
                    tools: {
                        download: true,
                        selection: false,
                        zoom: false,
                        zoomin: false,
                        zoomout: false,
                        pan: false,
                        reset: false
                    }
                }
            },
            plotOptions: {
                bar: {
                    borderRadius: 4,
                    horizontal: false,
                    columnWidth: '55%'
                }
            },
            dataLabels: {
                enabled: true,
                formatter: function(val) {
                    return val.toLocaleString() + '만원';
                }
            },
            xaxis: {
                categories: jsonData.consultants,
                title: {
                    text: '상담사명'
                }
            },
            yaxis: {
                title: {
                    text: '성공금액 (만원)'
                },
                labels: {
                    formatter: function(val) {
                        return val.toLocaleString();
                    }
                }
            },
            colors: ['#2E93fA'],
            tooltip: {
                y: {
                    formatter: function(val) {
                        return val.toLocaleString() + ' 만원';
                    }
                }
            }
        };

        // 모달 표시
        counselModel.modal('show');
        // 차트 생성 및 렌더링
        const consultantChart = new ApexCharts(
            document.querySelector("#counselorChart"),
            consultantChartOptions
        );

        view.empty();
        consultantChart.render();

    }

    // 모달 닫힐 때 차트 정리
    counselModel.on('hidden.bs.modal', function () {
        counselChart.empty();
    });


    // 차트 렌더링
    new ApexCharts(document.querySelector("#dashboard_Success_chart"), options).render();

})