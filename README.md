# 위너스캐디(Winners Caddie)

골프장 캐디 업무를 지원하는 안드로이드 애플리케이션 소스 코드입니다. 회원 인증부터 근무 스케줄 관리, 주문/결제 처리, 블루투스 장치 연동, 푸시 알림 처리까지 캐디 업무에 필요한 주요 기능을 제공합니다.

## 주요 기능
- **회원/인증**: 회원 가입, 로그인, 가맹점 등록 등의 흐름이 포함되어 있습니다.
- **근무 스케줄 관리**: 스케줄 조회·신청·확정 등의 화면(Activity)로 구성되어 있습니다.
- **주문 및 결제**: 주문 확인, 결제 요청, 결제 완료 확인 등 다양한 결제 플로우를 제공합니다.
- **디바이스 연동**: 블루투스 프린터 및 결제 단말과의 연결을 위한 권한과 서비스가 포함되어 있습니다.
- **푸시 알림**: Firebase Cloud Messaging을 통해 알림을 수신하도록 구성되어 있습니다.

## 개발 환경
- **Min/Target SDK**: minSdk 22, targetSdk 35
- **컴파일 SDK**: compileSdk 35
- **빌드 도구**: Gradle Wrapper(`./gradlew`)
- **언어 수준**: Java 8 호환(1.8)
- **앱 버전**: 1.0.12 (versionCode 1012 기준)

## 빌드 및 실행
1. Android Studio에서 프로젝트를 열고, 요구되는 SDK(35)와 빌드 도구를 설치합니다.
2. Firebase 설정 파일(`app/google-services.json`)과 서명 키스토어를 준비합니다.
3. 아래 명령으로 원하는 플래버와 빌드 타입을 조합해 APK를 생성합니다.
   ```bash
   ./gradlew assemble<Flavor><BuildType>
   ```
   예) 개발 서버용 릴리스 빌드: `./gradlew assembleDevelRelease`
4. 생성된 APK 파일 이름은 `winners_caddie_<flavor>_<versionName>.apk` 형식으로 출력됩니다.

## 폴더 구조 개요
- `app/src/main/java/com/bkwinners/caddie/` : 앱의 핵심 Activity, 서비스, 유틸리티 코드.
- `app/src/main/res/` : UI 리소스(레이아웃, 문자열, 이미지 등).
- `app/build.gradle` : 빌드 설정, 플래버, 의존성 정의.
- `gradle.properties`, `build.gradle`, `settings.gradle` : 루트 빌드 설정.