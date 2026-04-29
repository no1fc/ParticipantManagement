<%--
  Created by IntelliJ IDEA.
  User: no1fc
  Date: 25. 6. 27.
  Time: 오후 2:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="mytag" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="국민취업지원제도 참여자 목록 확인 페이지">
    <title>참여자 목록 확인 - 국민취업지원제도</title>
    <mytag:Logo/>
    <!-- jQuery JS -->
    <script src="https://code.jquery.com/jquery-3.7.1.min.js" integrity="sha256-/JqT3SQfawRcv/BIHPThkBvs0OEvtFFmqPF/lYI/Cxo=" crossorigin="anonymous"></script>

    <!-- Preconnect for performance -->
    <link rel="preconnect" href="https://cdn.jsdelivr.net"/>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet"/>

    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@300;400;500;600;700&display=swap" rel="stylesheet"/>

    <!-- Custom Style CSS  -->
    <link href="/css/jobPlacementCss/jobPlacementDefault_0.0.1.css" rel="stylesheet"/>

    <!-- jobPlacementListJS -->
    <script src="/js/jobPlacementJs/jobPlacementListJS_0.0.1.js"></script>

    <!-- InputLimits_0.0.1.js -->
    <script src="/js/InputLimits_0.0.1.js"></script>

    <!-- jobCategorySelectRenderText_0.0.2.js -->
    <script src="/js/jobPlacementJs/jobPlacementJobCategorySelectRenderText_0.0.1.js"></script>

</head>
<body>
<!-- Skip Navigation for Accessibility -->
<a href="#main-content" class="skip-link">메인 콘텐츠로 바로가기</a>

<mytag:jobPlacementView-header/>

<%--<mytag:jobPlacementView-nav pageController="list"/>--%>

<!-- Main Content -->
<main id="main-content" role="main" class="container">
  <div class="main-content">
    <!-- 페이지 헤더 -->
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h2 class="h4 mb-1">참여자 목록</h2>
        <p class="text-muted mb-0">잡모아 국민취업지원제도 참여자 정보를 확인하세요.</p>
      </div>
      <div class="d-flex align-items-center gap-3">
<%--        <button type="button"
                class="btn btn-modern btn-secondary"
                id="exportBtn">
          <i class="bi bi-download"></i> 내보내기
        </button>--%>
        <button type="button"
                class="btn btn-modern btn-primary"
                id="filterResetBtn">
            <i class="bi bi-arrow-clockwise"></i> 필터 초기화
        </button>
        <button type="button"
                class="btn btn-modern btn-primary"
                id="refreshListBtn">
          <i class="bi bi-search"></i> 검색 및 새로고침
        </button>
      </div>
    </div>

      <!-- 검색 및 필터 -->
      <div class="search-filters">
          <div class="row">
              <div class="col-md-7">
                  <label for="searchKeyword" class="form-label">
                      <i class="bi bi-search"></i> 검색
                  </label>
                  <div class="d-flex flex-row bd-highlight">
                      <select class="form-select w-25" id="searchType" name="searchType">
                          <option value="">전체</option>
                          <option value="구직번호">구직번호</option>
                          <option value="상담사">상담사</option>
                          <option value="참여자">참여자</option>
                      </select>
                      <input type="search"
                             class="form-control search-input"
                             id="searchKeyword"
                             name="searchKeyword"
                             placeholder="검색어를 입력해주세요.">
                  </div>
              </div>


              <div class="col-md-2">
                  <label for="ageRangeFilter" class="form-label">
                      <i class="bi bi-filter"></i> 청년ㆍ중장년
                  </label>
                  <select class="form-select" id="ageRangeFilter" name="ageRangeFilter">
                      <option value="">전체</option>
                      <option value="10">청년(~39)</option>
                      <option value="40">중년(~59)</option>
                      <option value="60">장년(60+)</option>
                  </select>
              </div>

              <div class="col-md-2">
                  <label for="genderFilter" class="form-label">
                      <i class="bi bi-filter"></i> 성별 필터
                  </label>
                  <select class="form-select" id="genderFilter" name="genderFilter">
                      <option value="">전체 성별</option>
                      <option value="남">남</option>
                      <option value="여">여</option>
                  </select>
              </div>

              <div class="col-md-1">
                  <label for="countFilter" class="form-label">
                      <i class="bi bi-filter"></i> 조회 개수
                  </label>
                  <select class="form-select" id="countFilter" name="pageRows">
                      <option value="10">10</option>
                      <option value="20">20</option>
                      <option value="30">30</option>
                      <option value="40">40</option>
                      <option value="50">50</option>
                  </select>
              </div>
          </div>

          <!-- 상세 검색 시작 -->
          <div class="row pt-3 d-flex justify-content-between">
                  <div class="col-md-6">
                      <label class="form-label">
                          <i class="bi bi-geo-alt"></i> 지역 선택
                      </label>
                      <div class="address-checkbox-group h-100">
                          <div class="address-checkbox-item address-checkbox-all">
                              <input type="checkbox" id="detailSearchAddressAll" value="" checked>
                              <label for="detailSearchAddressAll">전체 선택</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressSeoul" value="서울" checked>
                              <label for="detailSearchAddressSeoul">서울</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressGyeonggi" value="경기" checked>
                              <label for="detailSearchAddressGyeonggi">경기</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressIncheon" value="인천" checked>
                              <label for="detailSearchAddressIncheon">인천</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressDaejeon" value="대전" checked>
                              <label for="detailSearchAddressDaejeon">대전</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressSejong" value="세종특별자치시" checked>
                              <label for="detailSearchAddressSejong">세종</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressChungnam" value="충남" checked>
                              <label for="detailSearchAddressChungnam">충청남도</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressChungbuk" value="충북" checked>
                              <label for="detailSearchAddressChungbuk">충청북도</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressGwangju" value="광주" checked>
                              <label for="detailSearchAddressGwangju">광주</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressJeonnam" value="전남" checked>
                              <label for="detailSearchAddressJeonnam">전라남도</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressJeonbuk" value="전북특별자치도" checked>
                              <label for="detailSearchAddressJeonbuk">전북</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressDaegu" value="대구" checked>
                              <label for="detailSearchAddressDaegu">대구</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressGyeongbuk" value="경북" checked>
                              <label for="detailSearchAddressGyeongbuk">경상북도</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressBusan" value="부산" checked>
                              <label for="detailSearchAddressBusan">부산</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressUlsan" value="울산" checked>
                              <label for="detailSearchAddressUlsan">울산</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressGyeongnam" value="경남" checked>
                              <label for="detailSearchAddressGyeongnam">경상남도</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressGangwon" value="강원특별자치도" checked>
                              <label for="detailSearchAddressGangwon">강원도</label>
                          </div>
                          <div class="address-checkbox-item">
                              <input type="checkbox" class="detailSearchAddress" name="searchAddressFilter" id="detailSearchAddressJeju" value="제주특별자치도" checked>
                              <label for="detailSearchAddressJeju">제주도</label>
                          </div>
                      </div>
                  </div>

                  <div class="col-md-3">
                      <label class="form-label">
                          <i class="bi bi-geo-alt"></i> 직무카테고리-대분류
                      </label>
                      <div class="jobCategoryLarge-box">
                          <select class="form-select" id="jobCategoryLarge" name="jobCategoryLargeFilter" multiple required>
                              <option value="">선택하세요</option>
                          </select>
                      </div>
                  </div>

                  <div class="col-md-3">
                      <label class="form-label">
                          <i class="bi bi-geo-alt"></i> 직무카테고리-중분류
                      </label>
                      <div class="jobCategoryMid-box">
                          <select class="form-select" id="jobCategoryMid" name="jobCategoryMidFilter" multiple required>
                              <option value="">선택하세요</option>
                          </select>
                      </div>
                  </div>

          </div>
          <!-- 상세 검색 끝 -->
      </div>

      <!-- 참여자 목록 테이블 -->
      <div class="card-modern">
          <div class="table-responsive">
              <table class="table table-modern mb-0" id="participantTable">
                  <thead>
                  <tr>
                      <%--            <th scope="col" style="width: 60px;">--%>
                          <%--              <input type="checkbox"--%>
                          <%--                     class="form-check-input"--%>
                          <%--                     id="selectAllCheckbox"--%>
                          <%--                     aria-label="전체 선택">--%>
                          <%--            </th>--%>
                          <th scope="col" style="width: 120px;">구직번호</th>
                          <th scope="col">이름</th>
                          <th scope="col">학교명</th>
                          <th scope="col">나이</th>
                          <th scope="col">성별</th>
                          <th scope="col">거주지</th>
                          <th scope="col">희망직종</th>
                          <th scope="col">희망연봉</th>
                          <th scope="col" style="width: 120px;">상세정보</th>
                  </tr>
                  </thead>
                  <tbody id="participantTableBody">
                  <c:choose>
                      <c:when test="${not empty jobPlacementDatas}">
                          <c:forEach items="${jobPlacementDatas}" var="datas">
                              <tr>
                                  <td>${datas.jobNumber}</td>
                                  <td>${fn:substring(datas.participant, 0, 4)}</td>
                                  <td>${datas.schoolName}</td>
                                  <c:choose>
                                      <c:when test="${datas.ageRangeContent eq null || datas.ageRangeContent eq ''}">
                                          <td>${datas.age}</td>
                                      </c:when>
                                      <c:otherwise>
                                          <td>${datas.ageRangeContent}</td>
                                      </c:otherwise>
                                  </c:choose>
                                  <td>${datas.gender}</td>
                                  <td>${datas.address}</td>
                                  <td>
                                      <c:forEach var="job" items="${datas.desiredJobList}" varStatus="status">
                                          <c:if test="${not empty job.jobCategoryLarge}">${job.jobCategoryLarge} &gt; </c:if><c:if test="${not empty job.jobCategoryMid}">${job.jobCategoryMid} &gt; </c:if>${job.desiredJob}<c:if test="${!status.last}"><br/></c:if>
                                      </c:forEach>
                                  </td>
                                  <td>${datas.desiredSalary}</td>
                                  <td>
                                      <a href="#"
                                         class="btn btn-outline-primary btn-sm detailPageATag"
                                         title="상세보기">
                                          <i class="bi bi-eye"></i>
                                      </a>
                                      <input type="hidden" value="${datas.jobNumber}" name="jobNumber">
                                  </td>
                              </tr>
                          </c:forEach>
                      </c:when>
                      <c:otherwise>
                          <tr>
                              <td colspan="8" style="font-size: 1.5em; text-align: center;">검색된 참여자가 없습니다.</td>
                          </tr>
                      </c:otherwise>
                  </c:choose>
                  </tbody>
              </table>
          </div>
      </div>

      <%-- 페이지네이션 시작 --%>
      <nav class="col-md-11 text-center ms-auto me-auto d-flex justify-content-center">
          <ul class="pagination">
          </ul>
      </nav>
  </div>
</main>

<!-- Toast Container -->
<div class="toast-container" id="toastContainer"></div>

<!-- Scripts -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Pagination Function Start -->
<script src="/js/paginationJS_0.0.1.js"></script>
<script>
    $(document).ready(function(){
        <%-- pagination 시작 --%>
        // page 변수
        let page = parseInt("${page}", 10) || 1; // page가 비어있거나 아닌 경우 숫자로 변환 후 기본값 1 적용
        page = (page == 0) ? 1 : page; //page가 0이라면 1로 변경
        // startButton 변수
        const startButton = parseInt("${startButton}", 10) || 1; // startButton 기본값 1
        // endButton 변수
        const endButton = parseInt("${endButton}", 10) || 1; // endButton 기본값 10
        // totalButton 변수
        const totalButton = parseInt("${totalButton}", 10) || 0; // totalButton 기본값 0
        //pagination JS 함수 호출
        paginationAddItems(page, startButton, endButton, totalButton);
        <%-- pagination 끝 --%>

        <%-- 검색 폼 input --%>
        // 검색어
        let searchKeyword = $('#searchKeyword');
        // 검색 필터
        let searchType = $('#searchType');
        // 나이대 필터
        let ageRangeFilter = $('#ageRangeFilter');
        // 성별 필터
        let genderFilter = $('#genderFilter');
        // 게시글 개수 필터
        let countFilter = $('#countFilter');
        // 주소 필터
        let detailSearchAddress = $('.detailSearchAddress');
        // 직무 대분류
        let jobCategoryLarge = $('#jobCategoryLarge');
        // 직무 중분류
        let jobCategoryMid = $('#jobCategoryMid');
        // 주소 전체 선택
        let detailSearchAddressAll = $('#detailSearchAddressAll');
        <%-- 검색 폼 input --%>

        <%-- 검색 초기화 시작 --%>
        $("#filterResetBtn").on('click', function(){
            searchKeyword.val('');
            searchType.val('');
            ageRangeFilter.val('');
            genderFilter.val('');
            countFilter.val('10');
            detailSearchAddress.prop('checked', true);
            jobCategoryLarge.val('');
            jobCategoryMid.val('');
        })
        <%-- 검색 초기화 끝 --%>

        /* 주소 검색 필터 전체 선택 함수 시작 */
        function detailSearchAddressAllCheck(){
            if(detailSearchAddressAll.is(':checked')){
                detailSearchAddress.prop('checked', true);
            }else{
                detailSearchAddress.prop('checked', false);
            }
        }


        detailSearchAddressAll.on('click', function(){
            detailSearchAddressAllCheck();
        })

        /* 주소 검색 필터 선택에 따른 전체 선택해제 */
        detailSearchAddress.on('click', function(){
            if(!$(this).is(':checked')){
                detailSearchAddressAll.prop('checked', false);
            }
        })
        /* 주소 검색 필터 전체 선택 함수 끝 */

        /* 상세보기 버튼 주소 추가 */
        $('.detailPageATag').click(function(){
            let jobNumber = $(this).parent().find('input[name="jobNumber"]').val();
            window.location.href = 'placementDetail' + searchHref(page) + '&jobNumber=' + jobNumber;
        })
        /* 상세보기 버튼 주소 추가 */

        /* 검색어 및 각 필터값 특수 문자 처리 시작 */
        // 특수문자 검증
        searchKeyword.on('input', function(){
            regexSpecialSymbols($(this).val(), $(this));
        });

        /* 검색어 및 각 필터값 특수 문자 처리 끝 */

        /* 검색 시작*/
        //enter Key Event
        searchKeyword.on('keypress', function(e){
            if(e.keyCode === 13){
                $('#refreshListBtn').click();
            }
        })

        $('#refreshListBtn').on('click',function(){
            let searchKeywordVal = searchKeyword.val();
            let searchTypeVal = searchType.val();
            let ageRangeFilterVal = ageRangeFilter.val();
            let genderFilterVal = genderFilter.val();
            let countFilterVal = countFilter.val();
            let detailSearchAddressAllBoolean = detailSearchAddressAll.is(':checked');
            let jobCategoryLargeVal = jobCategoryLarge.val();
            let jobCategoryMidVal = jobCategoryMid.val();
            let searchHref = '?page=1';//+page;

            searchHref += searchKeywordVal === '' ? '' : '&searchKeyword='+searchKeywordVal.trim();
            searchHref += searchTypeVal === '' ? '' : '&searchType='+searchTypeVal;
            searchHref += ageRangeFilterVal === '' ? '' : '&ageRangeFilter='+ageRangeFilterVal;
            searchHref += genderFilterVal === '' ? '' : '&genderFilter='+genderFilterVal;
            searchHref += jobCategoryLargeVal === '' ? '' : '&jobCategoryLargeFilter='+jobCategoryLargeVal;
            searchHref += jobCategoryMidVal === '' ? '' : '&jobCategoryMidFilter='+jobCategoryMidVal;
            searchHref += countFilterVal === '' ? '&pageRows=10' : '&pageRows='+countFilterVal;

            //만약 주소에 전체 선택이 되어있다면 주소창에 넣지 않도록 설정
            if (!detailSearchAddressAllBoolean){
                detailSearchAddress.each(function(){
                    if($(this).is(':checked')){
                        searchHref += '&searchAddressFilter='+$(this).val();
                    }
                })
            }

            window.location.href = '/jobPlacement/placementList'+searchHref;
            }
        )
        /* 검색 끝*/

        /* 검색 폼 데이터 및 추가 시작 */
        searchKeyword.val('${param.searchKeyword}');
        searchType.val('${param.searchType}');
        ageRangeFilter.val(${param.ageRangeFilter});
        genderFilter.val('${param.genderFilter}');
        countFilter.val('${param.pageRows}'==='' ? '10' : '${param.pageRows}');

        // 직무 카테고리 - 대분류 먼저 설정
        const jobCategoryLargeFilterValue = '${param.jobCategoryLargeFilter}';
        const jobCategoryMidFilterValue = '${param.jobCategoryMidFilter}';

        <%--jobCategoryLarge.val('${param.jobCategoryLargeFilter}' || '');--%>
        <%--jobCategoryMid.val('${param.jobCategoryMidFilter}' || '');--%>

        if (jobCategoryLargeFilterValue) {
            jobCategoryLarge.val(jobCategoryLargeFilterValue);

            // 대분류 선택 시 중분류 옵션을 생성하는 이벤트를 강제로 발생시킴
            jobCategoryLarge.trigger('change');

            // 중분류 옵션 생성이 완료된 후 값을 설정
            // (옵션 생성이 비동기인 경우를 대비해 약간의 지연 추가)
            setTimeout(function() {
                if (jobCategoryMidFilterValue) {
                    jobCategoryMid.val(jobCategoryMidFilterValue);
                }
            }, 100);
        }

        // JSTL을 사용해 컨트롤러에서 전달된 모든 searchAddressFilter 값을 JS 배열로 만듭니다.
        const searchAddressFilterValues = [
            <c:forEach items="${paramValues.searchAddressFilter}" var="val" varStatus="status">'${val}'<c:if test="${!status.last}">,</c:if></c:forEach>
        ];

        // 배열에 값이 있을 경우에만 로직을 실행합니다.
        if (searchAddressFilterValues.length > 0) {
            // 'all'이 포함되어 있으면 전체 선택 처리
            if (searchAddressFilterValues.includes('all')) {
                detailSearchAddressAll.prop('checked', true);
                detailSearchAddress.prop('checked', true);
            } else {
                // 'all'이 아니면, 먼저 전체 선택을 해제하고 시작합니다.
                detailSearchAddressAll.prop('checked', false);
                detailSearchAddress.prop('checked', false);

                // 각 지역 체크박스를 순회하며, 값이 배열에 포함되어 있으면 체크합니다.
                detailSearchAddress.each(function() {
                    if (searchAddressFilterValues.includes($(this).val())) {
                        $(this).prop('checked', true);
                    }
                });

                // 만약 모든 지역이 개별적으로 선택되었다면, '전체 선택' 체크박스도 활성화합니다.
                if (detailSearchAddress.length === detailSearchAddress.filter(':checked').length) {
                    detailSearchAddressAll.prop('checked', true);
                }
            }
        }


        /* 검색 폼 데이터 및 추가 끝 */

    })
</script>
</body>
</html>