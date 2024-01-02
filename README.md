# WebIDE_Backend

## 🪄 프로젝트 개요
- idemainserver: 주요 요청을 처리하는 서버. 로그인, 회원가입 등 메인 서비스들은 해당 서버 코드에서 진행됩니다.
- runserver: 요청이 들어온 소스코드를 실행하는 서버입니다.

### 📅 개발 기간:
- 2023/12/10 ~ 2023/12/23

### ⚙️ 개발 환경
- `Java 17`
- **FrameWork**: Spring Boot 3.x
- **DataBase**: Mysql 8.0.33
- **ORM**: JPA
- 원활한 개발을 위해 IntelliJ를 사용할 경우 AWS toolkit, Spring Websocket 등 플러그인을 설치 바랍니다.

### 담당 기능
- **이건**: 소스코드 파이프라인 및 소스코드 실행 요청 처리
- **이재은**: 실시간 채팅 기능
- **홍예지**: 로그인 및 회원가입 기능
- **황한나**: 소스코드 실행 서버

## 🛑 커밋 전 체크 리스트 🛑

### 커밋 메세지
- 커밋 메세지 형식은 다음과 같습니다.
> type(타입) : title(제목)
> 
> body(본문, 생략 가능)
> 
> Resolves : #issueNo, ...(해결한 이슈 , 생략 가능)
> 
> See also : #issueNo, ...(참고 이슈, 생략 가능)

여기서 타입은 다음과 같습니다.

- feat : 새로운 기능을 추가하거나, 기존 기능을 요구사항 변경으로 인해 변경한 경우
- fix : 버그를 수정한 경우
- docs : 문서(주석) 추가/수정의 경우, 직접적인 코드의 변화 없이 문서만 추가 수정 했을 때
- style : UI를 추가/수정하거나, 스타일 관련 작업의 경우
- refactor : 기능의 변경 없이, 코드를 리팩토링 한 경우
- test : 테스트 코드를 추가/수정한 경우
- chore : 기능/테스트, 문서, 스타일, 리팩토링 외에 배포, 빌드와 같이 프로젝트의 기타 작업들에 대해 추가/수정한 경우

### 브랜치
- 해당 프로젝트는 Github Flow 전략을 사용합니다.
- main 브랜치에 직접 push하는 것을 금지합니다.
- pull request를 통해 코드 리뷰 후 merge 합니다.
- 또한 작업하는 브랜치가 main 브랜치가 아닌 개발 브랜치인지 확인합니다.
- 브랜치 명은 feature/기능명 입니다. 예시) feature/login
- 버그 fix를 위한 브랜치 분기의 경우 bug/버그명 입니다.

### 테스트 체크
- 반드시 테스트를 모두 통과하는지 확인합니다.
- 통합 테스트가 필요한 경우 로컬에서 확인 후 pull request 합니다.

### !!중요!! 중요 리소스 정보 노출
- AWS 키페어 같은 중요한 정보가 노출되지 않도록 확인합니다.
- 만약 새로운 키가 추가될 경우 환경 변수를 사용할 수 있도록 합니다.
- 중요 리소스가 노출되었고 그것을 인지했다면 최대한 빠른 시간 내에 팀에 연락합니다.

### conflict 방지
- conflict가 날 우려가 있는 파일 수정 시 수정 전에 팀원에게 알립니다.
- 우려가 있는 파일은 보통 build.gradle 파일이나 자신이 맡은 기능 외의 파일 입니다.
- 애매하면 무조건 코드 수정 전에 먼저 연락을 취하길 바랍니다.

update: 2023/12/07 🕥


## 🫧 프로젝트 결과
<p width="100%">
    <img src = "https://file.notion.so/f/f/9224b7b9-1355-41c4-9579-28b47a9e453a/4a8f6c29-36a9-44d2-b834-1257d6cc5f4f/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-01-03_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_1.28.51.png?id=84865c12-1683-4e40-95b8-7cca7760d498&table=block&spaceId=9224b7b9-1355-41c4-9579-28b47a9e453a&expirationTimestamp=1704304800000&signature=jpjgtg1xZerjr32hL2CVMMe7FaEPNj7xKQGcI8akVgI&downloadName=%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2024-01-03+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+1.28.51.png" width="50%"><img src = "https://file.notion.so/f/f/9224b7b9-1355-41c4-9579-28b47a9e453a/84c1cd92-3d70-42b8-b5e6-babe943016b4/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-01-03_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_2.19.51.png?id=3ca2e85e-73e9-4313-a3ca-32ba69782702&table=block&spaceId=9224b7b9-1355-41c4-9579-28b47a9e453a&expirationTimestamp=1704304800000&signature=Dy3fzTVsv4bg7ls0lsBShSdnsrcuuS40TlvZT6tKkgY&downloadName=%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2024-01-03+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+2.19.51.png" width="50%">
</p>
<p width="100%" align="center">
    <img src = "https://file.notion.so/f/f/9224b7b9-1355-41c4-9579-28b47a9e453a/e347b3af-3dad-4225-a734-fa21dc67c69a/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-01-03_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_1.21.43.png?id=391d109a-5664-46fb-a1ef-f9e5df54833c&table=block&spaceId=9224b7b9-1355-41c4-9579-28b47a9e453a&expirationTimestamp=1704304800000&signature=OueoXzATyDErYe7rH3UuULWXA7aSRPenL1AVgS8DktM&downloadName=%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2024-01-03+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+1.21.43.png" width="100%" width="90%">
</p>
<p width="100%">
    <img src = "https://file.notion.so/f/f/9224b7b9-1355-41c4-9579-28b47a9e453a/55e9b25d-1128-4f8f-8361-b2bec7c5d867/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-01-03_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_1.21.19.png?id=cb83cd54-8699-4530-80b0-53099b2dd75e&table=block&spaceId=9224b7b9-1355-41c4-9579-28b47a9e453a&expirationTimestamp=1704304800000&signature=EvcDR5Gl2Ef3rqZ96XQ0osECqr16znArYoXkyfqXic8&downloadName=%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2024-01-03+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+1.21.19.png" width="50%"><img src = "https://file.notion.so/f/f/9224b7b9-1355-41c4-9579-28b47a9e453a/9f9d4a34-043a-4766-862a-4045fd4cda2d/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-01-03_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_1.21.34.png?id=26ba88ae-30e2-4b4b-a3f3-35246751847a&table=block&spaceId=9224b7b9-1355-41c4-9579-28b47a9e453a&expirationTimestamp=1704304800000&signature=oG6WwnHTBRKviTvFt9RdwPutYHBM7ruwWZrn1Vi_ktU&downloadName=%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2024-01-03+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+1.21.34.png" width="50%">
</p>


### 🫧 페이지 구성
| 로그인 및 회원가입 페이지 | 메인 및 마이페이지 | 
|:--------------:|:--------------:|
|      ![Image](https://file.notion.so/f/f/9224b7b9-1355-41c4-9579-28b47a9e453a/4ea51681-c81b-4865-830c-4d19c39a7875/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-01-03_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_1.23.38.png?id=fb4e1ae7-a96b-447b-b8a9-d70b28d32043&table=block&spaceId=9224b7b9-1355-41c4-9579-28b47a9e453a&expirationTimestamp=1704304800000&signature=0zh9nNezQ_X9bjB8_lK9s8F0hJhOEMUfdiNWpwnNsH8&downloadName=%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2024-01-03+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+1.23.38.png)       |![Image](https://file.notion.so/f/f/9224b7b9-1355-41c4-9579-28b47a9e453a/5827af48-fde9-4755-8490-1828ae11922e/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-01-03_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_1.23.47.png?id=fe640c7c-4bcd-4ace-89c5-1db32a4fe481&table=block&spaceId=9224b7b9-1355-41c4-9579-28b47a9e453a&expirationTimestamp=1704304800000&signature=FqPXfjijgxVnDy2Wt8xGBAFH-D56LP6waEUQuqDMxdA&downloadName=%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2024-01-03+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+1.23.47.png) |

| 에디터, 채팅 | 실행 페이지 |
|:--------------:|:--------------:|
|![Image](https://file.notion.so/f/f/9224b7b9-1355-41c4-9579-28b47a9e453a/2bfb1bcc-6cdc-4ace-aa1e-20bdbd0a0fac/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-01-03_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_1.23.56.png?id=b56b16bd-92a0-4c67-95ad-e59b8ace1657&table=block&spaceId=9224b7b9-1355-41c4-9579-28b47a9e453a&expirationTimestamp=1704304800000&signature=tf9074N0JybmpwWBQgyOqtk4CgfDdsUoie9artQcfhg&downloadName=%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2024-01-03+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+1.23.56.png) | ![Image](https://file.notion.so/f/f/9224b7b9-1355-41c4-9579-28b47a9e453a/e452f820-dcd0-41cc-8ee7-fc7124cf38a8/%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA_2024-01-03_%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB_1.24.04.png?id=8f84886e-142a-4771-813a-b94a99fa59fe&table=block&spaceId=9224b7b9-1355-41c4-9579-28b47a9e453a&expirationTimestamp=1704304800000&signature=PkxshdNqfzv23t00RhKk8nunIqvCrcojM6_9BQla4b8&downloadName=%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2024-01-03+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+1.24.04.png) |

[🫧 💁🏻‍♀️ Notion](https://www.notion.so/GoormProject_Webide-4536e3ba7eec467d8869981ff03b262f)
</br>
[🫧 💁🏻‍♀️ Jira](https://theophilus.atlassian.net/wiki/spaces/Goojeans/overview)

