$(document).ready(function () {
    // DOM 요소 가져오기
    const tagsContainer = document.getElementById('keywords-container');
    const tagInput = document.getElementById('keyword-input');
    const keywordCountSpan = document.getElementById('keyword-count');
    const keywordListDiv = document.getElementById('keyword-list');
    const suggestedContainer = document.getElementById('suggested-keywords-container');
    const suggestedSection = document.getElementById('suggested-section');
    const errorMessage = document.getElementById('error-message');
    const description = document.getElementById('description');
    const hiddenContainer = document.getElementById('keywords-hidden-container');
    const jobCategoryLarge = document.getElementById('jobCategoryLarge');

// --- 설정값 ---
    const MIN_KEYWORD_LENGTH = 2;
    const MAX_KEYWORD_LENGTH = 25;
    const MIN_TAG_COUNT = 1; // 최소 1개는 필요하다고 가정
    const MAX_TAG_COUNT = 5;
// ---------------

    // 키워드를 저장할 배열
    let tags = [];
    // 추천 키워드 배열 목록
    let allSuggestedKeywords = [];

    // 추천 키워드 전체 목록
    const allKeywords = {
        "경영·사무·금융·보험":[
            '의사소통능력','문서작성능력','분석력','기획력','책임감','문제해결능력','대인관계능력','조직이해능력','직업윤리','수리능력','자원관리능력','성실성','논리적사고','리더십','꼼꼼함','정확성','신뢰성','윤리의식','정리정돈능력','서비스마인드'
            ],
        "연구 및 공학기술":[
            '문제해결능력','논리적사고','분석력','프로그래밍·코딩역량','기술능력','자기주도성','협업및커뮤니케이션','빠른학습력','책임감','성실성','꼼꼼함','끈기','정보능력','수리능력','창의성','데이터처리및활용역량','시스템·도구활용능력','품질관리및보안인식','연구설계능력','비판적사고'
            ],
        "교육·법률·사회복지·경찰·소방·군인":[
            '의사소통능력','설득력','분석력','책임감','적응력','비판적사고','발표능력','리더십','협업능력','정확성','윤리의식','공감능력','법률이해력','체력','정의감','서비스마인드','대인관계능력','자기개발능력','학습의지','시간관리능력'
            ],
        "보건·의료":[
            '의사소통능력','공감능력','정확성','윤리의식','스트레스관리능력','체력','책임감','관찰력','적응력','서비스마인드','팀워크','시간관리능력','신뢰성','학습의지','냉정함','정리정돈능력','리더십','자기개발능력','대인관계능력','정보능력'
            ],
        "예술·디자인·방송·스포츠":[
            '창의성','시각적감각','트렌드이해','도구활용능력','스토리텔링능력','의사소통능력','협업능력','관찰력','적응력','감수성','시간관리능력','프레젠테이션능력','비판적사고','문화이해','체력','끈기','자기개발능력','융합능력','서비스마인드','리더십'
            ],
        "미용·여행·숙박·음식·경비·돌봄·청소":[
            '서비스마인드','대인관계능력','책임감','체력','위생관리능력','친절성','스트레스관리능력','시간관리능력','공감능력','관찰력','꼼꼼함','성실성','안전의식','협력성','정리정돈능력','끈기','긍정적태도','적응력','의사소통능력','자기개발능력'
            ],
        "영업·판매·운전·운송":[
            '의사소통능력','설득력','대인관계능력','서비스마인드','신뢰성','끈기','책임감','시간관리능력','협상력','안전의식','체력','정확성','팀워크','긍정적태도','자율성','운전능력','적응력','관찰력','자기개발능력','정보능력'
            ],
        "건설·채굴":[
            '체력','안전의식','책임감','협력성','기술능력','정확성','끈기','관찰력','적응력','시간관리능력','성실성','자원관리능력','꼼꼼함','도구활용능력','팀워크','의사소통능력','학습의지','정리정돈능력','문제해결능력'
            ],
        "설치·정비·생산-기계·금속·재료":[
            '기술능력','정확성','안전의식','체력','책임감','협력성','관찰력','꼼꼼함','끈기','품질관리의식','도구활용능력','자원관리능력','시간관리능력','성실성','학습의지','적응력','의사소통능력','문제해결능력'
            ],
        "설치·정비·생산-전기·전자·정보통신":[
            '기술능력','정확성','안전의식','논리적사고','책임감','꼼꼼함','관찰력','학습의지','적응력','협력성','정보능력','도구활용능력','체력','끈기','품질관리의식','자기개발능력','의사소통능력','문제해결능력'
            ],
        "설치·정비·생산-화학·환경·섬유·의복·식품가공":[
            '안전의식','정확성','품질관리의식','책임감','체력','관찰력','꼼꼼함','위생관리능력','기술능력','협력성','적응력','끈기','성실성','시간관리능력','도구활용능력','자원관리능력','학습의지','의사소통능력'
            ],
        "설치·정비·생산-인쇄·목재·공예 및 제조 단순":[
            '꼼꼼함','정확성','체력','성실성','책임감','기술능력','관찰력','안전의식','협력성','끈기','적응력','도구활용능력','시간관리능력','자원관리능력','학습의지','정리정돈능력','의사소통능력'
            ],
        "농림어업직":[
            '체력','성실성','끈기','책임감','안전의식','관찰력','적응력','자연이해능력','기술능력','시간관리능력','자원관리능력','협력성','학습의지','정확성','꼼꼼함','문제해결능력','자기개발능력'
            ]
    };

    // 숨겨진 input들을 현재 tags와 동기화
    function syncHiddenInputs() {
        if (!hiddenContainer) return;
        hiddenContainer.innerHTML = ''; // 초기화
        tags.forEach(t => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'recommendedKeywords'; // 같은 name으로 여러 개 => List<String> 바인딩
            // input.id = 'keyword-input';
            input.classList.add('hidden-keyword-input');
            input.value = t;
            hiddenContainer.appendChild(input);
        });
    }

// 설명 문구 업데이트
    description.textContent = `참여자와 관련된 키워드를 입력하거나 추천 키워드를 클릭하세요. (최대 ${MAX_TAG_COUNT}개)`;

// 입력창 및 추천 키워드 상태 업데이트 함수
    function updateInputState() {
        if (tags.length >= MAX_TAG_COUNT) {
            tagInput.disabled = true;
            tagInput.placeholder = '최대 개수에 도달했습니다.';
            suggestedSection.style.display = 'none'; // 추천 섹션 숨기기
        } else {
            tagInput.disabled = false;
            tagInput.placeholder = `${MIN_KEYWORD_LENGTH}~${MAX_KEYWORD_LENGTH}자 사이 키워드 입력...`;
            suggestedSection.style.display = 'block'; // 추천 섹션 보이기
        }
    }

    // 키워드 배열이 변경될 때마다 UI를 업데이트하는 함수
    function updateKeywordsDisplay() {
        keywordCountSpan.textContent = tags.length;
        keywordListDiv.textContent = tags.length > 0 ? tags.join(', ') : '입력된 키워드가 없습니다.';
        updateInputState();
        syncHiddenInputs(); // 동기화 호출
    }

// 에러 메시지를 표시하는 함수
    function showError(message) {
        errorMessage.textContent = message;
        tagsContainer.classList.add('shake');
        setTimeout(() => {
            errorMessage.textContent = '';
            tagsContainer.classList.remove('shake');
        }, 1500);
    }

// 추천 키워드를 렌더링하는 함수
    function renderSuggestedKeywords() {
        if(allSuggestedKeywords == null) return;
        suggestedContainer.innerHTML = ''; // 기존 추천 키워드 비우기
        const availableKeywords = allSuggestedKeywords.filter(keyword => !tags.includes(keyword));

        availableKeywords.forEach(keyword => {
            const button = document.createElement('button');
            button.setAttribute('class', 'bg-slate-200 hover:bg-slate-300 text-slate-700 text-xs font-medium px-3 py-1.5 rounded-full transition-colors duration-200');
            button.textContent = `+ ${keyword}`;
            button.addEventListener('click', () => {
                addTag(keyword);
            });
            suggestedContainer.appendChild(button);
        });
    }

// 태그를 추가하는 로직을 함수로 분리
    function addTag(label) {
        // 최대 개수 검사
        if (tags.length >= MAX_TAG_COUNT) {
            showError(`최대 ${MAX_TAG_COUNT}개의 키워드만 추가할 수 있습니다.`);
            return;
        }

        const newTagLabel = label.trim().replace(/,/g, '');

        // 글자 수 유효성 검사
        if (newTagLabel.length < MIN_KEYWORD_LENGTH || newTagLabel.length > MAX_KEYWORD_LENGTH) {
            showError(`키워드는 ${MIN_KEYWORD_LENGTH}~${MAX_KEYWORD_LENGTH}자 사이로 입력해주세요.`);
            return;
        }
        if (tags.includes(newTagLabel)) {
            showError('이미 추가된 키워드입니다.');
            return;
        }

        // 배열에 새로운 태그 추가
        tags.push(newTagLabel);
        // 새로운 태그를 화면에 생성
        const newTagElement = createTag(newTagLabel);
        tagsContainer.insertBefore(newTagElement, tagInput);

        // UI 업데이트
        updateKeywordsDisplay();
        renderSuggestedKeywords(); // 추천 키워드 목록 갱신
    }

    /*
     * 외부(백엔드에서 받은 초기값 등)에서 키워드 배열/문자열을 주입할 수 있도록 공개 API 제공
     * - 배열: ['자기주도성', '책임감', ...]
     * - 문자열: "[자기주도성, 책임감]" 또는 "자기주도성, 책임감"
     */
    window.addKeywordsFromBackend = function(input) {
        // 이미 최대치인 경우 스킵
        if (tags.length >= MAX_TAG_COUNT) return;

        const toArray = (val) => {
            if (Array.isArray(val)) return val;
            if (val == null) return [];
            // 양 끝 대괄호 제거 후 쉼표 분리
            return String(val)
                .replace(/^\s*\[|\]\s*$/g, '')
                .split(',')
                .map(s => s.replace(/['"]/g, '').trim())
                .filter(Boolean);
        };

        const keywords = toArray(input);

        for (const kw of keywords) {
            if (tags.length >= MAX_TAG_COUNT) break; // 최대 5개 제한 준수
            // addTag는 내부 유효성(중복/길이) 체크 수행
            addTag(kw);
        }
    };

    // 태그를 생성하고 화면에 추가하는 함수
    function createTag(label) {
        const div = document.createElement('div');
        div.setAttribute('class', 'flex items-center justify-center bg-indigo-100 text-indigo-700 text-sm font-medium px-3 py-1.5 rounded-full');

        const span = document.createElement('span');
        span.textContent = label;

        const closeBtn = document.createElement('button');
        closeBtn.setAttribute('class', 'ml-2 bg-indigo-200 hover:bg-indigo-300 text-indigo-700 rounded-full w-4 h-4 flex items-center justify-center text-xs font-bold transition-colors duration-200');
        closeBtn.textContent = '×';
        closeBtn.addEventListener('click', () => {
            const tagToRemove = div;
            const tagLabel = tagToRemove.querySelector('span').textContent;

            tagToRemove.remove();
            tags = tags.filter(tag => tag !== tagLabel);

            // UI 업데이트
            updateKeywordsDisplay();
            renderSuggestedKeywords(); // 추천 키워드 목록 갱신
        });

        div.appendChild(span);
        div.appendChild(closeBtn);

        return div;
    }

    // 입력 필드에서 키보드 이벤트 처리 (keydown으로 Enter의 form submit 방지)
    tagInput.addEventListener('keydown', (e) => {
        errorMessage.textContent = ''; // 키 입력 시 에러 메시지 초기화
        if (e.key === 'Enter' || e.key === ',') {
            e.preventDefault();
            if(e.target.value.trim() !== '') {
                addTag(e.target.value);
            }
            e.target.value = ''; // 입력 필드 비우기
        }
        else if (e.key === 'Backspace' && e.target.value === '' && tags.length > 0) {
            const lastTag = tagsContainer.querySelector('div:last-of-type');
            if(lastTag) {
                const tagLabel = lastTag.querySelector('span').textContent;
                tags = tags.filter(tag => tag !== tagLabel);
                lastTag.remove();

                updateKeywordsDisplay();
                renderSuggestedKeywords();
            }
        }
    });

    //다중 희망직무의 모든 대분류에서 추천 키워드를 합산
    function updateSuggestedKeywordsFromWishJobs(){
        const $wishItems = $('#wishJobListContainer .wish-job-item');
        if ($wishItems.length === 0) {
            allSuggestedKeywords = [];
            renderSuggestedKeywords();
            return;
        }
        // 모든 희망직무의 대분류 값을 수집 (중복 제거)
        const categories = new Set();
        $wishItems.each(function () {
            const large = $(this).find('.wish-large-input').val();
            if (large) categories.add(large);
        });
        // 각 대분류의 추천 키워드를 합산 (중복 제거)
        const merged = [];
        categories.forEach(function (cat) {
            const keywords = allKeywords[cat];
            if (keywords) {
                keywords.forEach(function (kw) {
                    if (!merged.includes(kw)) merged.push(kw);
                });
            }
        });
        allSuggestedKeywords = merged;
        renderSuggestedKeywords();
    }

    //대분류 변경에 따른 키워드 변경 (하위 호환 유지)
    function jobCategoryLargeKeywordChange($val){
        updateSuggestedKeywordsFromWishJobs();
    }

    $('#jobCategoryLarge').on('change', function () {
        jobCategoryLargeKeywordChange($(this).val());
    })

    // 초기 UI 상태 설정
    updateKeywordsDisplay();
    // 초기 추천 키워드 렌더링
    updateSuggestedKeywordsFromWishJobs();
})

