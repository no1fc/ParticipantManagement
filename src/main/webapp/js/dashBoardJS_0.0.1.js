//dashBoard chart data js 파일
//dashBoard Doughnut chart data
function chart_doughnut_data(id,data,drawCenterText_text,denominator){
    return new Chart(id, {
        type: 'doughnut',
        data: {
            labels: data.title,
            datasets: [{
                data: data.text,
                backgroundColor: ['#ffffff','#f67676'],
                hoverBackgroundColor: ['#e04e4e'],
                border:1,
                borderWidth: 1,
                borderColor: '#000000',
                hoverBorderColor: '#000000',
            }],
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false,
                },
                datalabels: {
                    formatter: function (value, context) {
                        let result = Math.round(value / context.chart.getDatasetMeta(0).total * 100);
                        return result != 0? result + "%" : "";
                    },
                    color: '#fff',
                },
                drawCenterText:{
                    text:drawCenterText_text,
                    denominator:denominator,
                }
            },
        },
    });
}


//dashBoard Bar chart data
function chart_bar_data_succes(id,labels,data){
    let data_label = JSON.parse(JSON.stringify(data)).title;
    let data_text = JSON.parse(JSON.stringify(data)).text;

    console.log(data_label);
    console.log(data_text);
    return new Chart(id, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: data_label[0],
                    data: data_text[0],
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1,
                },
                {
                    label: data_label[1],
                    data: data_text[1],
                    backgroundColor: 'rgba(255, 159, 64, 0.2)',
                    borderColor: 'rgba(255, 159, 64, 1)',
                    borderWidth: 1
                },
                {
                    label: data_label[2],
                    data: data_text[2],
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    display: false,
                },
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                },
            }
        }
    });
}

function chart_bar_data_my(id,labels,data){
    let data_label = JSON.parse(JSON.stringify(data)).title;
    let data_text = JSON.parse(JSON.stringify(data)).text;

    console.log(data_label);
    console.log(data_text[0]);
    return new Chart(id, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: data_label[0],
                    data: [data_text[0]],
                    backgroundColor: 'rgba(54, 162, 235, 0.2)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1,
                },
                {
                    label: data_label[1],
                    data: [data_text[1]],
                    backgroundColor: 'rgba(255, 159, 64, 0.2)',
                    borderColor: 'rgba(255, 159, 64, 1)',
                    borderWidth: 1
                },
                {
                    label: data_label[2],
                    data: [data_text[2]],
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    display: false,
                },
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom',
                    labels: {
                        font:{
                            size:9,
                        }
                    }
                },
            }
        }
    });
}