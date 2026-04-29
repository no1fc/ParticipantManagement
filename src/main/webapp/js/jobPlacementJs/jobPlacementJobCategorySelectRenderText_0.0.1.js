// jQuery 버전
$(jobCategorySelectRender);

function jobCategorySelectRender() {
// 중분류 목록
    const jobCategoryLargeArray = [
        {text: "경영·사무·금융·보험", value: "경영·사무·금융·보험"},
        {text: "연구 및 공학기술", value: "연구 및 공학기술"},
        {text: "교육·법률·사회복지·경찰·소방·군인", value: "교육·법률·사회복지·경찰·소방·군인"},
        {text: "보건·의료", value: "보건·의료"},
        {text: "예술·디자인·방송·스포츠", value: "예술·디자인·방송·스포츠"},
        {text: "미용·여행·숙박·음식·경비·돌봄·청소", value: "미용·여행·숙박·음식·경비·돌봄·청소"},
        {text: "영업·판매·운전·운송", value: "영업·판매·운전·운송"},
        {text: "건설·채굴", value: "건설·채굴"},
        {text: "설치·정비·생산-기계·금속·재료", value: "설치·정비·생산-기계·금속·재료"},
        {text: "설치·정비·생산-전기·전자·정보통신", value: "설치·정비·생산-전기·전자·정보통신"},
        {text: "설치·정비·생산-화학·환경·섬유·의복·식품가공", value: "설치·정비·생산-화학·환경·섬유·의복·식품가공"},
        {text: "설치·정비·생산-인쇄·목재·공예 및 제조 단순", value: "설치·정비·생산-인쇄·목재·공예 및 제조 단순"},
        {text: "농림어업직", value: "농림어업직"}
    ];

// 소분류 매핑: key는 중분류 value와 동일
    const jobCategoryMidArray = {
        "경영·사무·금융·보험": [
            {text: "행정·경영·금융·보험 관리직", value: "행정·경영·금융·보험 관리직"},
            {text: "교육·법률·복지·의료·예술·방송·정보통신 등 전문서비스 관리직", value: "교육·법률·복지·의료·예술·방송·정보통신 등 전문서비스 관리직"},
            {text: "미용·여행·숙박·음식 등 개인서비스 및 영업·판매·운송 관리직", value: "미용·여행·숙박·음식 등 개인서비스 및 영업·판매·운송 관리직"},
            {text: "건설·채굴·제조·생산 관리직", value: "건설·채굴·제조·생산 관리직"},
            {text: "행정·경영·회계·광고·상품기획 전문가", value: "행정·경영·회계·광고·상품기획 전문가"},
            {text: "정부·공공행정 사무", value: "정부·공공행정 사무"},
            {text: "경영지원 사무", value: "경영지원 사무"},
            {text: "회계·경리 사무", value: "회계·경리 사무"},
            {text: "무역·운송·자재·구매·생산·품질 사무", value: "무역·운송·자재·구매·생산·품질 사무"},
            {text: "안내·접수·고객상담 사무", value: "안내·접수·고객상담 사무"},
            {text: "통계·비서·사무보조·기타 사무", value: "통계·비서·사무보조·기타 사무"},
            {text: "금융·보험 전문가", value: "금융·보험 전문가"},
            {text: "금융·보험 사무 및 영업", value: "금융·보험 사무 및 영업"}
        ],
        "연구 및 공학기술": [
            {text: "인문·사회·자연·생명과학 연구 및 시험", value: "인문·사회·자연·생명과학 연구 및 시험"},
            {text: "컴퓨터하드웨어·통신공학", value: "컴퓨터하드웨어·통신공학"},
            {text: "컴퓨터시스템", value: "컴퓨터시스템"},
            {text: "소프트웨어", value: "소프트웨어"},
            {text: "데이터·네트워크 및 시스템 운영", value: "데이터·네트워크 및 시스템 운영"},
            {text: "정보 보안 및 통신·방송 송출", value: "정보 보안 및 통신·방송 송출"},
            {text: "건설·채굴 연구 및 공학기술", value: "건설·채굴 연구 및 공학기술"},
            {text: "기계·로봇·금속·재료 연구 및 공학기술", value: "기계·로봇·금속·재료 연구 및 공학기술"},
            {text: "전기·전자 연구 및 공학기술", value: "전기·전자 연구 및 공학기술"},
            {text: "화학·에너지·환경 연구 및 공학기술", value: "화학·에너지·환경 연구 및 공학기술"},
            {text: "섬유·식품·소방·방재·산업안전 연구 및 공학기술", value: "섬유·식품·소방·방재·산업안전 연구 및 공학기술"},
            {text: "제도사(3D프린팅포함) 및 기타 인쇄·목재 등 공학기술", value: "제도사(3D프린팅포함) 및 기타 인쇄·목재 등 공학기술"}
        ],
        "교육·법률·사회복지·경찰·소방·군인": [
            {text: "대학교수, 학교 및 유치원 교사", value: "대학교수, 학교 및 유치원 교사"},
            {text: "문리·어학 강사", value: "문리·어학 강사"},
            {text: "컴퓨터·기술·기능계 강사", value: "컴퓨터·기술·기능계 강사"},
            {text: "예능·학습지·기타 강사", value: "예능·학습지·기타 강사"},
            {text: "장학관·교육조교(RA포함) 및 교사·보육 보조", value: "장학관·교육조교(RA포함) 및 교사·보육 보조"},
            {text: "법률 전문가 및 법률 사무", value: "법률 전문가 및 법률 사무"},
            {text: "사회복지·상담·직업상담·시민단체활동", value: "사회복지·상담·직업상담·시민단체활동"},
            {text: "보육교사·생활지도원 및 종교직", value: "보육교사·생활지도원 및 종교직"},
            {text: "경찰·소방·교도·군인", value: "경찰·소방·교도·군인"}
        ],
        "보건·의료": [
            {text: "의사·한의사·치과의사", value: "의사·한의사·치과의사"},
            {text: "수의사·약사 및 한약사·간호사·영양사", value: "수의사·약사 및 한약사·간호사·영양사"},
            {text: "의료기사·치료사·재활사", value: "의료기사·치료사·재활사"},
            {text: "그 외 보건·의료 종사자", value: "그 외 보건·의료 종사자"}
        ],
        "예술·디자인·방송·스포츠": [
            {text: "작가, 통·번역 및 출판물 전문가", value: "작가, 통·번역 및 출판물 전문가"},
            {text: "기자 및 언론 전문가", value: "기자 및 언론 전문가"},
            {text: "학예사·사서·기록물관리사", value: "학예사·사서·기록물관리사"},
            {text: "창작·공연 (작가 및 연극 제외)", value: "창작·공연 (작가 및 연극 제외)"},
            {text: "디자이너 (미디어콘텐츠 제외)", value: "디자이너 (미디어콘텐츠 제외)"},
            {text: "미디어콘텐츠·UX/UI 디자이너", value: "미디어콘텐츠·UX/UI 디자이너"},
            {text: "연극·영화·방송 (방송기술 포함)", value: "연극·영화·방송 (방송기술 포함)"},
            {text: "공연·음반 기획 및 매니저", value: "공연·음반 기획 및 매니저"},
            {text: "스포츠·레크리에이션", value: "스포츠·레크리에이션"}
        ],
        "미용·여행·숙박·음식·경비·돌봄·청소": [
            {text: "미용 및 반려동물 미용·관리", value: "미용 및 반려동물 미용·관리"},
            {text: "결혼·장례 등 예식 서비스", value: "결혼·장례 등 예식 서비스"},
            {text: "여행·객실승무·숙박·오락 서비스", value: "여행·객실승무·숙박·오락 서비스"},
            {text: "주방장 및 조리사", value: "주방장 및 조리사"},
            {text: "식당 서비스(음식배달 포함)", value: "식당 서비스(음식배달 포함)"},
            {text: "경호·보안", value: "경호·보안"},
            {text: "경비원", value: "경비원"},
            {text: "돌봄 서비스", value: "돌봄 서비스"},
            {text: "청소·방역 및 가사 서비스", value: "청소·방역 및 가사 서비스"},
            {text: "검침·주차관리 및 기타 단순 서비스", value: "검침·주차관리 및 기타 단순 서비스"}
        ],
        "영업·판매·운전·운송": [
            {text: "부동산중개, 기술·의약품 및 해외 영업", value: "부동산중개, 기술·의약품 및 해외 영업"},
            {text: "자동차·제품·광고 영업 및 상품 중개", value: "자동차·제품·광고 영업 및 상품 중개"},
            {text: "텔레마케터(TM)", value: "텔레마케터(TM)"},
            {text: "소규모 판매점장 및 상점 판매", value: "소규모 판매점장 및 상점 판매"},
            {text: "통신서비스·온라인판매·상품대여·노점·이동판매 및 주유", value: "통신서비스·온라인판매·상품대여·노점·이동판매 및 주유"},
            {text: "매장 계산 및 매표", value: "매장 계산 및 매표"},
            {text: "판촉 및 기타 판매 종사자", value: "판촉 및 기타 판매 종사자"},
            {text: "항공기·선박·철도 조종 및 관제", value: "항공기·선박·철도 조종 및 관제"},
            {text: "자동차 운전(택시·버스·화물차·기타 자동차)", value: "자동차 운전(택시·버스·화물차·기타 자동차)"},
            {text: "물품이동장비 조작", value: "물품이동장비 조작"},
            {text: "택배·납품영업·선박갑판·하역 및 기타 운송", value: "택배·납품영업·선박갑판·하역 및 기타 운송"}
        ],
        "건설·채굴": [
            {text: "건설구조 기능(철골·철근·석공·목공·조적 등)", value: "건설구조 기능(철골·철근·석공·목공·조적 등)"},
            {text: "건축마감 기능(미장·단열·도배·새시·영선 등)", value: "건축마감 기능(미장·단열·도배·새시·영선 등)"},
            {text: "배관", value: "배관"},
            {text: "건설·채굴 기계 운전", value: "건설·채굴 기계 운전"},
            {text: "기타 건설 기능(광원,채석,철로설치 등)", value: "기타 건설 기능(광원,채석,철로설치 등)"},
            {text: "건설·채굴 단순 종사자", value: "건설·채굴 단순 종사자"}
        ],
        "설치·정비·생산-기계·금속·재료": [
            {text: "기계장비 설치·정비", value: "기계장비 설치·정비"},
            {text: "운송장비 정비", value: "운송장비 정비"},
            {text: "금형 및 공작기계 조작", value: "금형 및 공작기계 조작"},
            {text: "냉난방 설비·자동 조립라인·산업용 로봇 조작", value: "냉난방 설비·자동 조립라인·산업용 로봇 조작"},
            {text: "기계 및 운송장비 조립", value: "기계 및 운송장비 조립"},
            {text: "금속관련 기계·설비 조작", value: "금속관련 기계·설비 조작"},
            {text: "판금·제관·단조·주조", value: "판금·제관·단조·주조"},
            {text: "용접", value: "용접"},
            {text: "도장·도금", value: "도장·도금"},
            {text: "비금속제품 생산기계 조작", value: "비금속제품 생산기계 조작"}
        ],
        "설치·정비·생산-전기·전자·정보통신": [
            {text: "전기공(전기공사)", value: "전기공(전기공사)"},
            {text: "전기·전자 기기 설치·수리(사무용,가전제품,기타)", value: "전기·전자 기기 설치·수리(사무용,가전제품,기타)"},
            {text: "발전·배전 장비, 전기·전자 설비 조작(전기관리)", value: "발전·배전 장비, 전기·전자 설비 조작(전기관리)"},
            {text: "전기·전자 부품·제품 생산기계 조작", value: "전기·전자 부품·제품 생산기계 조작"},
            {text: "전기·전자 부품·제품 조립", value: "전기·전자 부품·제품 조립"},
            {text: "정보통신기기 설치·수리(컴퓨터,핸드폰)", value: "정보통신기기 설치·수리(컴퓨터,핸드폰)"},
            {text: "방송·통신장비 및 케이블 설치·수리", value: "방송·통신장비 및 케이블 설치·수리"}
        ],
        "설치·정비·생산-화학·환경·섬유·의복·식품가공": [
            {text: "석유·화학물 가공장치 조작", value: "석유·화학물 가공장치 조작"},
            {text: "고무·플라스틱 및 화학제품 생산기계 조작", value: "고무·플라스틱 및 화학제품 생산기계 조작"},
            {text: "환경 장치 조작(상하수도·재활용처리)", value: "환경 장치 조작(상하수도·재활용처리)"},
            {text: "섬유 제조 및 가공 기계 조작", value: "섬유 제조 및 가공 기계 조작"},
            {text: "패턴·재단·재봉", value: "패턴·재단·재봉"},
            {text: "의복 제조 및 수선", value: "의복 제조 및 수선"},
            {text: "제화, 기타 섬유·의복 기계 조작", value: "제화, 기타 섬유·의복 기계 조작"},
            {text: "제과·제빵 및 떡 제조", value: "제과·제빵 및 떡 제조"},
            {text: "식품가공 기능원(도축·정육,김치·밑반찬제조 등)", value: "식품가공 기능원(도축·정육,김치·밑반찬제조 등)"},
            {text: "식품가공 기계 조작", value: "식품가공 기계 조작"}
        ],
        "설치·정비·생산-인쇄·목재·공예 및 제조 단순": [
            {text: "인쇄기계·사진현상기 조작", value: "인쇄기계·사진현상기 조작"},
            {text: "목재·펄프·종이 생산", value: "목재·펄프·종이 생산"},
            {text: "가구·목제품 제조·수리", value: "가구·목제품 제조·수리"},
            {text: "공예 및 귀금속 세공", value: "공예 및 귀금속 세공"},
            {text: "악기·간판 및 기타 제조(드론조작 포함)", value: "악기·간판 및 기타 제조(드론조작 포함)"},
            {text: "제조 단순 종사자", value: "제조 단순 종사자"}
        ],
        "농림어업직": [
            {text: "작물재배", value: "작물재배"},
            {text: "낙농·사육", value: "낙농·사육"},
            {text: "임업 종사자", value: "임업 종사자"},
            {text: "어업 종사자", value: "어업 종사자"},
            {text: "농림어업 단순 종사자", value: "농림어업 단순 종사자"}
        ]
    };

    const $large = $('#jobCategoryLarge');
    const $mid = $('#jobCategoryMid');

    const PLACEHOLDER = {text: '카테고리 선택', value: ''};
    const LASTPLACEHOLDER = {text: '기타', value: '기타'};

    function setOptions($select, items, selected) {
        $select.empty();
        if (items.length !== 0) {
            $select.append($('<option>').val(PLACEHOLDER.value).text(PLACEHOLDER.text));
        }
        items.forEach(item => {
            const $opt = $('<option>').val(item.value).text(item.text);
            if (selected != null && String(selected) === String(item.value)) {
                $opt.prop('selected', true);
            }
            $select.append($opt);
        });
        if (items.length !== 0) {
            $select.append($('<option>').val(LASTPLACEHOLDER.value).text(LASTPLACEHOLDER.text));
        }
    }

    function getMidCategories(largeValue) {
        return jobCategoryMidArray[largeValue] || [];
    }

    function updateMidOptions(largeValue, selectedMid) {
        const subs = getMidCategories(largeValue);
        setOptions($mid, subs, selectedMid);
        $mid.prop('disabled', subs.length === 0);
    }

// 초기 선택값 복원
    const selectedLarge = $large.data('selected') || '';
    const selectedMid = $mid.data('selected') || '';

// 대분류 옵션(표시는 괄호 앞부분) 구성
    const midOptions = jobCategoryLargeArray
        .filter(i => i.value !== '')
        .map(i => ({text: i.text.split('(')[0], value: i.value}));

    setOptions($large, midOptions, selectedLarge);

    if (selectedLarge) {
        updateMidOptions(selectedLarge, selectedMid);
    } else {
        setOptions($mid, [], '');
        $mid.prop('disabled', true);
    }

    // 이벤트: 대분류 변경 시 중분류 갱신
    $large.on('change', function () {
        updateMidOptions(this.value, '');
    });

    $large.on('', function () {
        console.log('change');
    });
}