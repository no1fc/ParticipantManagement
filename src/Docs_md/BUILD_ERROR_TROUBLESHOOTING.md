# 빌드 오류 해결 가이드

> **작성일**: 2025-12-30
> **프로젝트**: JobmoaProject v1.2.17-SNAPSHOT
> **작성자**: Build Troubleshooting Log

## 목차
1. [Lombok "cannot find symbol" 오류](#1-lombok-cannot-find-symbol-오류)
2. [IntegrationTest 실패 오류](#2-integrationtest-실패-오류)
3. [예방 및 권장사항](#3-예방-및-권장사항)

---

## 1. Lombok "cannot find symbol" 오류

### 증상
Maven 빌드 실행 시 100개 이상의 컴파일 오류 발생:
```
[ERROR] cannot find symbol
  symbol:   variable log
  location: class com.jobmoa.app.CounselMain.biz.bean.PaginationBean

[ERROR] cannot find symbol
  symbol:   method getDashboardCondition()
  location: variable dashboardDTO of type DashboardDTO

[ERROR] cannot find symbol
  symbol:   method setBasicCondition(java.lang.String)
  location: variable basicDTO of type BasicDTO
```

### 원인 분석
**Maven Compiler Plugin에 Lombok annotation processor path가 설정되지 않음**

- Lombok의 `@Slf4j`, `@Data`, `@Getter`, `@Setter` 등의 어노테이션은 **컴파일 시점에 코드를 생성**하는 annotation processor입니다.
- Maven 컴파일러가 Lombok annotation processor를 인식하지 못하면 코드 생성이 일어나지 않아 `log` 변수나 getter/setter 메서드를 찾을 수 없게 됩니다.
- pom.xml에 Lombok 의존성은 있었으나, `maven-compiler-plugin` 설정이 누락되어 있었습니다.

### 해결 방법

#### pom.xml 수정
`<build>` 섹션에 `maven-compiler-plugin` 설정 추가:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>

        <!-- Lombok annotation processor 설정 추가 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.36</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 검증
```bash
./mvnw clean compile
```

**결과**:
```
[INFO] BUILD SUCCESS
[INFO] Total time:  4.230 s
```

### 추가 참고사항

#### IntelliJ IDEA에서는 왜 문제가 없었나?
- IntelliJ IDEA는 자체적으로 Lombok 플러그인을 제공하며, IDE 내에서 annotation processing을 자동으로 처리합니다.
- 그러나 **Maven CLI 빌드**는 IDE와 독립적으로 동작하므로, pom.xml에 명시적인 설정이 필요합니다.

#### SLF4J 의존성 이슈
초기에 slf4j-api와 slf4j-log4j12가 `<scope>provided</scope>`로 설정되어 있어 제거를 시도했으나, 근본 원인은 Lombok annotation processor 미설정이었습니다.

Spring Boot는 `spring-boot-starter`에 이미 로깅 구현체(Logback)를 포함하고 있으므로, slf4j-api를 별도로 추가할 필요가 없습니다.

---

## 2. IntegrationTest 실패 오류

### 증상
Maven 테스트 실행 시 IntegrationTest 실패:
```
[ERROR] IntegrationTest.testAllConfigsIntegration:53
        Should have beans from WebMvcConfig
        ==> expected: <true> but was: <false>
```

### 원인 분석
**테스트 코드가 존재하지 않는 빈을 확인하려고 시도**

테스트 코드 (IntegrationTest.java:52):
```java
boolean hasWebMvcConfig = context.containsBean("viewResolver");
```

실제 WebMvcConfig.java:43-50:
```java
// @Bean
// public InternalResourceViewResolver viewResolver() {
//     ...
// }
```

- `viewResolver` 빈이 주석 처리되어 있었습니다.
- Spring Boot 환경에서는 `application.properties`의 `spring.mvc.view.prefix`, `spring.mvc.view.suffix` 설정으로 ViewResolver를 자동 구성하므로, 수동 빈 등록이 불필요했습니다.
- 테스트는 주석 처리된 빈의 존재를 확인하려 했기 때문에 실패했습니다.

### 해결 방법

#### IntegrationTest.java 수정
존재하지 않는 `viewResolver` 빈 대신, **WebMvcConfig 클래스 자체**가 빈으로 등록되었는지 확인:

```java
// Before (실패)
boolean hasWebMvcConfig = context.containsBean("viewResolver");

// After (성공)
boolean hasWebMvcConfig = context.containsBean("webMvcConfig");
```

**설명**:
- `@Configuration` 클래스는 기본적으로 클래스명의 camelCase 형태로 빈 이름이 등록됩니다.
- `WebMvcConfig` → `webMvcConfig`
- 이 방법으로 WebMvcConfig가 정상적으로 로드되었는지 확인할 수 있습니다.

### 검증
```bash
./mvnw test
```

**결과**:
```
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 테스트 결과 상세
- **IntegrationTest**: 4개 테스트 모두 통과
  - ✅ testFullContextLoads
  - ✅ testAllConfigsIntegration
  - ✅ testBeanDependencyInjection
  - ✅ testContextHierarchy
- **RootConfigTest**: 9개 테스트 모두 통과

---

## 3. 예방 및 권장사항

### 3.1 Lombok 사용 시 필수 설정

#### Maven 프로젝트
pom.xml에 반드시 `annotationProcessorPaths` 설정:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

#### Gradle 프로젝트
```gradle
dependencies {
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}
```

### 3.2 테스트 작성 시 주의사항

#### DO: 실제 존재하는 빈 확인
```java
// Config 클래스 자체 확인
context.containsBean("webMvcConfig")

// 특정 타입의 빈 확인
context.getBeansOfType(DataSource.class).size() > 0

// 인터페이스로 확인
context.getBean(WebMvcConfigurer.class)
```

#### DON'T: 주석 처리되거나 조건부 빈 확인
```java
// ❌ 주석 처리된 빈
context.containsBean("viewResolver")

// ❌ @ConditionalOnProperty 등으로 조건부 활성화되는 빈
context.containsBean("optionalBean")
```

### 3.3 빌드 환경별 검증

#### 로컬 개발
```bash
# IDE에서 잘 되더라도 반드시 CLI에서도 확인
./mvnw clean compile
./mvnw test
```

#### CI/CD 파이프라인
```bash
# 전체 빌드 프로세스 검증
./mvnw clean package
./mvnw verify
```

### 3.4 의존성 scope 설정 가이드

| Scope | 용도 | 예시 |
|-------|------|------|
| `compile` | 기본값, 모든 단계에서 사용 | Spring Core, MyBatis |
| `provided` | 컴파일/테스트에만 사용, 런타임은 컨테이너 제공 | Servlet API, Lombok |
| `runtime` | 런타임에만 필요 | JDBC Driver |
| `test` | 테스트에만 사용 | JUnit, Mockito |

**Lombok**: `provided` scope 사용
- 컴파일 시점에만 필요 (코드 생성)
- 런타임에는 생성된 코드만 사용되므로 Lombok 라이브러리 불필요

**SLF4J**: Spring Boot Starter 사용 시 별도 추가 불필요
- `spring-boot-starter`에 이미 포함됨

---

## 4. 트러블슈팅 체크리스트

빌드 오류 발생 시 다음 순서로 확인:

### ✅ Step 1: Lombok 관련 오류
- [ ] `cannot find symbol: variable log` 오류?
- [ ] `cannot find symbol: method getXxx()` 오류?
- [ ] pom.xml에 `maven-compiler-plugin` + `annotationProcessorPaths` 설정 있는가?
- [ ] Lombok 버전이 pom.xml dependency와 annotationProcessorPaths에서 일치하는가?

### ✅ Step 2: 테스트 실패
- [ ] 테스트가 확인하는 빈이 실제로 등록되어 있는가?
- [ ] 주석 처리된 빈을 확인하고 있지 않은가?
- [ ] @ConditionalOnXxx 조건이 테스트 환경에서 만족되는가?

### ✅ Step 3: 의존성 문제
- [ ] 중복 의존성이 없는가? (`mvn dependency:tree` 확인)
- [ ] scope가 올바르게 설정되어 있는가?
- [ ] Spring Boot Starter가 제공하는 의존성을 중복으로 추가하지 않았는가?

### ✅ Step 4: 빌드 도구 설정
- [ ] Java 버전이 일치하는가? (프로젝트: 17)
- [ ] Maven/Gradle 버전이 적절한가?
- [ ] IDE 빌드와 CLI 빌드 결과가 동일한가?

---

## 5. 참고 자료

### 공식 문서
- [Lombok Maven Setup](https://projectlombok.org/setup/maven)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/)

### 프로젝트 문서
- [Spring Boot 마이그레이션 가이드](SPRING_BOOT_MIGRATION_COMPLETE_GUIDE.md)
- [배포 빠른 가이드](DEPLOYMENT_QUICK_GUIDE.md)
- [프로젝트 현황](프로젝트-현황.md)

---

## 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2025-12-30 | 1.0 | 초기 문서 작성 (Lombok 오류, IntegrationTest 오류) | Build Team |

---

**문서 끝**
