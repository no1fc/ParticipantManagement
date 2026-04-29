// 한국 시/도별 구/군 데이터
const locationData = {
    "서울특별시": [
        "종로구", "중구", "용산구", "성동구", "광진구", "동대문구", "중랑구", "성북구",
        "강북구", "도봉구", "노원구", "은평구", "서대문구", "마포구", "양천구", "강서구",
        "구로구", "금천구", "영등포구", "동작구", "관악구", "서초구", "강남구", "송파구", "강동구"
    ],
    "부산광역시": [
        "중구", "서구", "동구", "영도구", "부산진구", "동래구", "남구", "북구",
        "해운대구", "사하구", "금정구", "강서구", "연제구", "수영구", "사상구", "기장군"
    ],
    "대구광역시": [
        "중구", "동구", "서구", "남구", "북구", "수성구", "달서구", "달성군"
    ],
    "인천광역시": [
        "중구", "동구", "미추홀구", "연수구", "남동구", "부평구", "계양구", "서구", "강화군", "옹진군"
    ],
    "광주광역시": [
        "동구", "서구", "남구", "북구", "광산구"
    ],
    "대전광역시": [
        "동구", "중구", "서구", "유성구", "대덕구"
    ],
    "울산광역시": [
        "중구", "남구", "동구", "북구", "울주군"
    ],
    "세종특별자치시": [
        "세종시"
    ],
    "경기도": [
        "수원시", "성남시", "용인시", "안양시", "안산시", "과천시", "광명시", "광주시", "군포시", "부천시",
        "시흥시", "김포시", "안성시", "오산시", "의왕시", "이천시", "평택시", "하남시", "화성시", "여주시",
        "양평군", "고양시", "구리시", "남양주시", "동두천시", "양주시", "의정부시", "파주시", "포천시", "연천군", "가평군"
    ],
    "강원도": [
        "춘천시", "원주시", "강릉시", "동해시", "태백시", "속초시", "삼척시", "홍천군", "횡성군", "영월군",
        "평창군", "정선군", "철원군", "화천군", "양구군", "인제군", "고성군", "양양군"
    ],
    "충청북도": [
        "청주시", "충주시", "제천시", "보은군", "옥천군", "영동군", "증평군", "진천군", "괴산군", "음성군", "단양군"
    ],
    "충청남도": [
        "천안시", "공주시", "보령시", "아산시", "서산시", "논산시", "계룡시", "당진시", "금산군", "부여군",
        "서천군", "청양군", "홍성군", "예산군", "태안군"
    ],
    "전라북도": [
        "전주시", "군산시", "익산시", "정읍시", "남원시", "김제시", "완주군", "진안군", "무주군", "장수군",
        "임실군", "순창군", "고창군", "부안군"
    ],
    "전라남도": [
        "목포시", "여수시", "순천시", "나주시", "광양시", "담양군", "곡성군", "구례군", "고흥군", "보성군",
        "화순군", "장흥군", "강진군", "해남군", "영암군", "무안군", "함평군", "영광군", "장성군", "완도군", "진도군", "신안군"
    ],
    "경상북도": [
        "포항시", "경주시", "김천시", "안동시", "구미시", "영주시", "영천시", "상주시", "문경시", "경산시",
        "군위군", "의성군", "청송군", "영양군", "영덕군", "청도군", "고령군", "성주군", "칠곡군", "예천군", "봉화군", "울진군", "울릉군"
    ],
    "경상남도": [
        "창원시", "진주시", "통영시", "사천시", "김해시", "밀양시", "거제시", "양산시", "의령군", "함안군",
        "창녕군", "고성군", "남해군", "하동군", "산청군", "함양군", "거창군", "합천군"
    ],
    "제주특별자치도": [
        "제주시", "서귀포시"
    ]
};

// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', function() {
    const citySelect = document.getElementById('participantLocationCity');
    const districtSelect = document.getElementById('participantLocationDistrict');

    // 시/도 선택 시 구/군 목록 업데이트
    citySelect.addEventListener('change', function() {
        const selectedCity = this.value;

        // 구/군 select 초기화
        districtSelect.innerHTML = '<option value="">거주지(구)를 선택하세요</option>';

        // 선택된 시/도가 있고 해당 데이터가 존재하는 경우
        if (selectedCity && locationData[selectedCity]) {
            // 해당 시/도의 구/군 목록을 추가
            locationData[selectedCity].forEach(function(district) {
                const option = document.createElement('option');
                option.value = district;
                option.textContent = district;
                districtSelect.appendChild(option);
            });

            // 구/군 select 활성화
            districtSelect.disabled = false;
        } else {
            // 시/도가 선택되지 않은 경우 구/군 select 비활성화
            districtSelect.disabled = true;
        }

        // 유효성 검사 클래스 제거 (새로 선택할 때)
        districtSelect.classList.remove('is-invalid');
    });

    // 페이지 로드 시 초기 상태 설정
    if (citySelect.value) {
        // 이미 선택된 시/도가 있는 경우 구/군 목록 업데이트
        citySelect.dispatchEvent(new Event('change'));
    } else {
        // 시/도가 선택되지 않은 경우 구/군 select 비활성화
        districtSelect.disabled = true;
    }
});

// 폼 검증을 위한 추가 함수 (옵션)
function validateLocationSelects() {
    const citySelect = document.getElementById('participantLocationCity');
    const districtSelect = document.getElementById('participantLocationDistrict');

    let isValid = true;

    // 시/도 검증
    if (!citySelect.value) {
        citySelect.classList.add('is-invalid');
        isValid = false;
    } else {
        citySelect.classList.remove('is-invalid');
    }

    // 구/군 검증
    if (!districtSelect.value) {
        districtSelect.classList.add('is-invalid');
        isValid = false;
    } else {
        districtSelect.classList.remove('is-invalid');
    }

    return isValid;
}