# Ubuntu 24.04 배포 빠른 가이드

## 🚀 5분 배포 (JAR 방식 권장)

### 1단계: Windows에서 빌드
```bash
cd C:\JobmoaIntelliJFolder\JobmoaProject
mvn clean package -DskipTests
```
📦 생성: `target/TestProject-0.0.1-SNAPSHOT.jar`

### 2단계: Ubuntu 서버 준비
```bash
# Java 17 설치
sudo apt update
sudo apt install openjdk-17-jdk -y

# 앱 디렉토리 생성
mkdir -p ~/app
```

### 3단계: 파일 전송
```bash
# Windows PowerShell에서
scp target/TestProject-0.0.1-SNAPSHOT.jar ubuntu@YOUR_SERVER_IP:~/app/
```

### 4단계: Ubuntu에서 실행
```bash
cd ~/app
java -jar TestProject-0.0.1-SNAPSHOT.jar
```

✅ 완료! → `http://YOUR_SERVER_IP:8088` 접속

---

## 🔧 서비스로 등록 (자동 시작)

### 서비스 파일 생성
```bash
sudo nano /etc/systemd/system/jobmoa.service
```

### 내용 붙여넣기
```ini
[Unit]
Description=Jobmoa Spring Boot App
After=network.target

[Service]
User=ubuntu
ExecStart=/usr/bin/java -jar /home/ubuntu/app/TestProject-0.0.1-SNAPSHOT.jar
WorkingDirectory=/home/ubuntu/app
Restart=always

[Install]
WantedBy=multi-user.target
```

### 서비스 시작
```bash
sudo systemctl daemon-reload
sudo systemctl enable jobmoa
sudo systemctl start jobmoa
sudo systemctl status jobmoa
```

---

## 🔍 문제 해결

### 로그 확인
```bash
sudo journalctl -u jobmoa -f
```

### 포트 확인
```bash
sudo lsof -i :8088
```

### 방화벽 개방
```bash
sudo ufw allow 8088/tcp
```

---

## 📊 주요 URL

- **메인:** `http://YOUR_SERVER_IP:8088`
- **헬스체크:** `http://YOUR_SERVER_IP:8088/actuator/health`
- **WebSocket 테스트:** `http://YOUR_SERVER_IP:8088/webSocketTestPage.jsp`

---

## 🔄 업데이트 방법

### 1. 서비스 중지
```bash
sudo systemctl stop jobmoa
```

### 2. 새 JAR 업로드
```bash
scp target/TestProject-0.0.1-SNAPSHOT.jar ubuntu@YOUR_SERVER_IP:~/app/
```

### 3. 서비스 재시작
```bash
sudo systemctl start jobmoa
```

---

## 💡 프로덕션 권장 설정

### Nginx 리버스 프록시
```bash
# Nginx 설치
sudo apt install nginx -y

# 설정 파일
sudo nano /etc/nginx/sites-available/jobmoa
```

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8088;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /ws {
        proxy_pass http://localhost:8088/ws;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

```bash
# 활성화
sudo ln -s /etc/nginx/sites-available/jobmoa /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx

# 방화벽
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
```

---

## 📝 변경사항 요약

| 항목 | 변경 전 (MVC) | 변경 후 (Boot) |
|------|--------------|----------------|
| 실행 | 외부 Tomcat 필요 | JAR 독립 실행 |
| 설정 | 복잡 (여러 파일) | 간단 (자동) |
| WebSocket | 수동 설정 | 자동 작동 |
| 배포 | WAR → Tomcat | JAR 또는 WAR |

---

**상세 가이드:** `SPRING_BOOT_MIGRATION_COMPLETE_GUIDE.md` 참고
