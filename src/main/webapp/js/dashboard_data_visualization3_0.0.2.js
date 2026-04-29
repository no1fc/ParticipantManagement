$(document).ready(function(){
    //데이터 초기화
    const Datas = initData;

    function preprocessData(data) {
        return data.map((item) => {
            // data 속성이 있는 경우
            if (item.data) {
                return {
                    ...item,
                    data: item.data.map(value => value === 0 ? 0.1 : value)
                };
            }
            // 단순 배열인 경우
            return item === 0 ? 0.1 : item;
        });
    }

    // 네번째 프레임
    let options = {
        series: preprocessData(Datas.inventiveSituation),
        chart: {
            type: 'bar',
            height: 350,
            toolbar: {
                show: true
            },
            //모달 생성 이벤트 제작 후 수정 완료
            events: {
                dataPointSelection: function(event, chartContext, config) { // click 대신 dataPointSelection 사용
                    const branchIndex = config.dataPointIndex;
                    // const branchName = Datas.thisSuccess.branch[branchIndex];

                    // 전체 지점 클릭시 모달 표시
                    if (branchIndex === 0) {

                        showInventiveModal("A");
                        showInventiveModal("B");
                    }
                }
            }
        },
        plotOptions: {
            bar: {
                horizontal: false,
                columnWidth: '55%',
                borderRadius: 2,
                // 0값도 그래프 표시를 위한 설정
                minHeight: 2,
                dataLabels: {
                    position: 'center'
                }
            }
        },
        stroke: {
            show: true,
            width: 2,
            colors: ['transparent']
        },
        xaxis: {
            min:0,
            categories: ['전체 지점','A 사업부','B 사업부'],
        },
        // Y축 설정 수정
        yaxis: {
            min: 0,  // y축 최소값 설정
            tickAmount: 5,  // y축 눈금 개수
            title: {
                text: '미해당 (건)'
            },
            labels: {
                formatter: function(val) {
                    return val.toLocaleString() + " 건";
                }
            }
        },
        fill: {
            opacity: 1
        },
        // 기본 색상 설정
        colors: ['#ff0000', '#fca895', '#ffb0b0','#934e4e','#d34859', '#a45050','#772525'],
        tooltip: {
            y: {
                formatter: function(val) {
                    return val + "건"
                }
            }
        },
        legend: {
            position: 'bottom',
            horizontalAlign: 'center'
        },
        // 강조 효과를 위한 states 설정
        states: {
            active: {
                allowMultipleDataPointsSelection: false,
                filter: {
                    type: 'none'
                }
            }
        },
        // 데이터 라벨 수정
        dataLabels: {
            enabled: true,
            formatter: function(val) {
                // 0 포함하여 모든 값 표시
                return (val.toLocaleString()==0.1?0:val.toLocaleString()) + " 건";
            },
            textAnchor: 'middle',
            style: {
                colors: ['#000000'],
                fontWeight: 500
            },
            offsetY: 0
        },
        title: {
            text: '인센 미해당 현황',
            align: 'left'
        },

    };



    /**
     * 지점별 상담사 성공금 현황을 모달로 표시하는 함수
     * @param {string} branchName - 선택된 지점명
     */
    function showInventiveModal(branchName) {

        // 모달 표시
        $('#inventiveSituationModal').modal('show');
        
        let startDate = $("#dashBoardStartDate").val();
        let endDate = $("#dashBoardEndDate").val();

        let inventiveFalseStatusData =
            {
                branch:[],
                inventiveSituation:
                    [
                        {
                            name: '서비스 미제공',
                            data: []
                        },
                        {
                            name: '1개월 미만 퇴사',
                            data: []
                        },
                        {
                            name: '파견업체',
                            data: []
                        },
                        {
                            name: 'IAP수립7일이내취업',
                            data: []
                        },
                        {
                            name: '주 30시간 미만',
                            data: []
                        },
                        {
                            name: '최저임금 미만',
                            data: []
                        },
                        {
                            name: '기타(해외취업포함)',
                            data: []
                        },
                    ]};

        fetch('/dashBoardInventive.login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                businessUnit: branchName,
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

            resultCheck.forEach((item) => {
            // result.forEach((item) => {
                inventiveFalseStatusData.branch.push(item.branch);
                inventiveFalseStatusData.inventiveSituation.at(0).data.push(item.noService);
                inventiveFalseStatusData.inventiveSituation.at(1).data.push(item.lessThanOneMonth);
                inventiveFalseStatusData.inventiveSituation.at(2).data.push(item.dispatchCompany);
                inventiveFalseStatusData.inventiveSituation.at(3).data.push(item.iapSevenDays);
                inventiveFalseStatusData.inventiveSituation.at(4).data.push(item.underThirtyHours);
                inventiveFalseStatusData.inventiveSituation.at(5).data.push(item.underMinWage);
                inventiveFalseStatusData.inventiveSituation.at(6).data.push(item.etc);
            });

            // 모달 제목 업데이트
            $('#inventiveSituationModal .modal-title').text('지점별 미해당 현황');
            // 상담사 차트 옵션
            const consultantChartOptions = {
                series: preprocessData(inventiveFalseStatusData.inventiveSituation),
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
                            pan: false,
                            reset: false
                        }
                    },
                    events: {
                        dataPointSelection: function(event, chartContext, config) { // click 대신 dataPointSelection 사용
                            const index = config.dataPointIndex;
                            const branchName = config.w.config.xaxis.categories[index];
                            console.log("선택 지점 :" + branchName);
                            searchNoService(branchName);
                        }
                    }
                },
                plotOptions: {
                    bar: {
                        borderRadius: 4,
                        horizontal: false,
                        columnWidth: '70%'
                    }
                },
                dataLabels: {
                    enabled: true,
                    formatter: function(val) {
                        return Math.floor(val) + '건';
                    },
                    style: {
                        colors: ['#000000'],
                        fontWeight: 500
                    },
                },
                xaxis: {
                    categories: inventiveFalseStatusData.branch,
                },
                yaxis: {
                    title: {
                        text: '미해당 건 수'
                    },
                    labels: {
                        formatter: function(val) {
                            return val.toLocaleString();
                        }
                    }
                },
                // 기본 색상 설정
                colors: ['#ff0000', '#fca895', '#ffb0b0','#934e4e','#d34859', '#a45050','#772525'],
                tooltip: {
                    y: {
                        formatter: function(val) {
                            return Math.floor(val) + ' 건';
                        }
                    }
                },
                title: {
                    text: branchName+'사업부 인센 미해당 현황',
                    align: 'left'
                },
            };

            // 차트 생성 및 렌더링
            const inventiveSituationAChart = new ApexCharts(
                document.querySelector("#inventiveSituation"+branchName+"Chart"),
                consultantChartOptions
            );
            //차트 표시
            inventiveSituationAChart.render();
        }).catch(err => {
            console.log(err);
        })

    }

    /*  테스트용
    /!**
        * 상담사 데이터를 가져오는 함수 (실제 구현 시 API 호출로 대체)
        * @param {string} branchName - 지점명
        * @returns {Promise} - 상담사 데이터
        *!/
       function ajaxInventiveData(branchName) {
           let startDate = $("#dashBoardStartDate").val();
           let endDate = $("#dashBoardEndDate").val();

           const inventiveFalseStatusData =
               {branch:[],
                   inventiveSituation:
                   [
                       {
                           name: '서비스 미제공',
                           data: []
                       },
                       {
                           name: '1개월 미만 퇴사',
                           data: []
                       },
                       {
                           name: '파견업체',
                           data: []
                       },
                       {
                           name: 'IAP수립7일이내취업',
                           data: []
                       },
                       {
                           name: '주 30시간 미만',
                           data: []
                       },
                       {
                           name: '최저임금 미만',
                           data: []
                       },
                       {
                           name: '기타(해외취업포함)',
                           data: []
                       },
                   ]};

           fetch('dashBoardInventive.login', {
               method: 'POST',
               headers: {
                   'Content-Type': 'application/json'
               },
               body: JSON.stringify({
                   businessUnit: branchName,
                   dashBoardStartDate: startDate,
                   dashBoardEndDate: endDate
               })
           }).then(async r => {
               let result = await r.json();
               JSON.parse(result).forEach((item) => {
                       inventiveFalseStatusData.branch.push(item.branch);
                       inventiveFalseStatusData.inventiveSituation.at(0).data.push(item.noService);
                       inventiveFalseStatusData.inventiveSituation.at(1).data.push(item.lessThanOneMonth);
                       inventiveFalseStatusData.inventiveSituation.at(2).data.push(item.dispatchCompany);
                       inventiveFalseStatusData.inventiveSituation.at(3).data.push(item.iapSevenDays);
                       inventiveFalseStatusData.inventiveSituation.at(4).data.push(item.underThirtyHours);
                       inventiveFalseStatusData.inventiveSituation.at(5).data.push(item.underMinWage);
                       inventiveFalseStatusData.inventiveSituation.at(6).data.push(item.etc);
                   });
           })
           console.log("비동기 완료 : " + inventiveFalseStatusData);

           return new Promise((resolve) => {
               // 테스트용 더미 데이터 (실제 구현시 API 호출로 대체)
               let timeoutNumber= setTimeout(() => {
                   resolve(inventiveFalseStatusData
                   //{
                       // branch:['남부','서부','인서','인남','동대문'],
                       // inventiveSituation:
                       //     [
                       //         {
                       //             name: '서비스 미제공',
                       //             data: []
                       //         },
                       //         {
                       //             name: '1개월 미만 퇴사',
                       //             data: []
                       //         },
                       //         {
                       //             name: '파견업체',
                       //             data: []
                       //         },
                       //         {
                       //             name: 'IAP수립7일이내취업',
                       //             data: []
                       //         },
                       //         {
                       //             name: '주 30시간 미만',
                       //             data: []
                       //         },
                       //         {
                       //             name: '최저임금 미만',
                       //             data: []
                       //         },
                       //         {
                       //             name: '기타(해외취업포함)',
                       //             data: []
                       //         },
                       //     ]
                   //}
                   );
               }, 5000);
           });
       }*/

    function searchNoService(branchName) {
        // 모달 표시
        $('#noServiceModal').modal('show');
        fetch('noServiceAjax.login',{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                participantBranch: branchName,
                searchStartDate: $("#dashBoardStartDate").val(),
                searchEndDate: $("#dashBoardEndDate").val()
            })
        }).then(async r => {
            const noServiceTableBody = $('#noServiceTableBody');
            const noServiceModalLabel = $('#noServiceModalLabel');
            console.log(r);
            let result = await r.json();
            console.log(result);
            let searchHtml;
            noServiceModalLabel.text(branchName + " 서비스 미제공 리스트")

            result.forEach((item) => {
                searchHtml += "<tr><td>";
                searchHtml += item.participantRegDate;
                searchHtml += "</td><td>";
                searchHtml += item.participantJobNo;
                searchHtml += "</td><td>";
                searchHtml += item.participantUserName;
                searchHtml += "</td><td>";
                searchHtml += item.participantPartic;
                searchHtml += "</td><td>";
                searchHtml += item.participantInItCons;
                searchHtml += "</td><td>";
                searchHtml += item.participantStartDate;
                searchHtml += "</td><td>";
                searchHtml += item.participantIncentive;
                searchHtml += "</td></tr>";
            })
            console.log(searchHtml);

            noServiceTableBody.html(searchHtml);
        })
    }

    // 모달 닫힐 때 차트 정리
    $('#inventiveSituationModal').on('hidden.bs.modal', function () {
        $('#inventiveSituationAChart').empty();
        $('#inventiveSituationBChart').empty();
    });

    $('#noServiceModal').on('hidden.bs.modal', function () {
        $('#noServiceTableBody').empty();
        $('#noServiceModalLabel').empty();
    });

    // 차트 렌더링
    new ApexCharts(document.querySelector("#dashboard_inventive_situation_chart"), options /*donutOptions*/).render();
})