/**
 * 취업률 그래프 차트 생성
 * Chart.js를 사용한 단일 막대 그래프 (취업률, 알선취업률, 총점)
 */

    /**
     * 단일 막대 그래프 생성 함수
     */
    function createSingleBarChart(canvasId, label, value, grades, maxValue, minValue, unit) {
        const ctx = document.getElementById(canvasId);

        // 커스텀 플러그인: 등급선 및 값 표시
        const customPlugin = {
            id: 'customAnnotations',
            afterDraw: (chart) => {
                const ctx = chart.ctx;
                const yScale = chart.scales.y;
                const xScale = chart.scales.x;

                const gradeColors = {
                    A: 'rgba(34, 197, 94, 0.9)',
                    B: 'rgba(59, 130, 246, 0.9)',
                    C: 'rgba(251, 191, 36, 0.9)',
                    D: 'rgba(239, 68, 68, 0.9)'
                };

                // 등급 기준선 그리기
                Object.keys(grades).forEach((grade) => {
                    const yPos = yScale.getPixelForValue(grades[grade]);

                    ctx.save();
                    ctx.beginPath();
                    ctx.setLineDash([5, 5]);
                    ctx.strokeStyle = gradeColors[grade];
                    ctx.lineWidth = 2;
                    ctx.moveTo(xScale.left, yPos);
                    ctx.lineTo(xScale.right, yPos);
                    ctx.stroke();
                    ctx.restore();

                    // 등급 라벨
                    ctx.save();
                    ctx.font = 'bold 11px Arial';
                    ctx.fillStyle = gradeColors[grade];
                    ctx.textAlign = 'right';
                    ctx.fillText(grade + '등급', xScale.right - 5, yPos - 5);
                    ctx.restore();
                });

                // 막대 위에 값 표시
                const meta = chart.getDatasetMeta(0);
                meta.data.forEach((bar, index) => {
                    const data = chart.data.datasets[0].data[index];
                    ctx.save();
                    ctx.font = 'bold 14px Arial';
                    ctx.fillStyle = '#1f2937';
                    ctx.textAlign = 'center';
                    ctx.fillText(data + unit, bar.x, bar.y - 10);
                    ctx.restore();
                });
            }
        };

        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: [label],
                datasets: [{
                    label: label,
                    data: [value],
                    backgroundColor: 'rgba(79, 70, 229, 0.85)',
                    borderColor: 'rgba(79, 70, 229, 1)',
                    borderWidth: 0,
                    barThickness: 100,
                    borderRadius: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: { enabled: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: maxValue,
                        min: minValue,
                        ticks: {
                            font: { size: 11 },
                            callback: function(value) {
                                return value + unit;
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)',
                            drawBorder: false
                        }
                    },
                    x: {
                        grid: {
                            display: false,
                            drawBorder: false
                        },
                        ticks: {
                            font: { size: 12 }
                        }
                    }
                },
                layout: {
                    padding: {
                        top: 30,
                        right: 20
                    }
                }
            },
            plugins: [customPlugin]
        });
    }
