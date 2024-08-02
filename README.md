# 🔒 바이너리 파일 암호화 시스템 🔒
<img width="1054" alt="system_image" src="https://github.com/user-attachments/assets/7775bb51-cbab-4674-91c7-fcede944bf2f">

### 목차
1. [시스템 개요 📖](#-시스템-개요)
2. [사용 프로그램 및 목적 🛠](#-사용-프로그램-및-목적)
3. [개발환경 🚀](#-개발환경)
4. [소프트웨어 아키텍처 📂](#-소프트웨어-아키텍처)
5. [시나리오 📑](#-시나리오)
    - [바이너리 파일 업로드 및 암호화](#1-바이너리-파일-업로드-및-암호화)
    - [암호화 이력 조회](#2-암호화-이력-조회)
    - [바이너리 파일 다운로드](#3-바이너리-파일-다운로드)
6. [기능 정의 🔍](#-기능-정의)
7. [실행 ⚙️](#%EF%B8%8F-실행)
8. [테스트 케이스 🛠](#-테스트-케이스)
9. [구현 내용 👩‍💻](#-구현-내용)
    - [API 명세서📑](#-api-명세서)
    - [Client 🖥](#-client)
      - [progress bar](#1-progress-bar)
      - [contextAPI](#2-context-api)
      - [pagination](#3-pagination)
    - [Server 🖥](#-server)
      - [javadoc](#1-javadoc)
      - [AES-128 암호화](#2-aes-128-암호화)
      - [pagination](#3-pagination)
      - [TDD](#4-tdd)
    - [DevOps 🖥](#-devops)
      - [Docker](#1-docker)
      - [AWS](#2-aws)
        
# 📖 시스템 개요

  | 항목 | 내용 |
  | :---: | --- |
  | **프로젝트명** | 바이너리 파일 암호화 시스템 |
  | **프로젝트 개요** | 사용자가 업로드한 바이너리 파일을 AES-128 알고리즘을 사용하여 암호화하고, 암호화 이력을 조회할 수 있는 시스템을 개발한다.<br> 이력을 기반으로 업로드한 파일과 암호화된 파일을 모두 다운로드할 수 있다. |
  | **주요기능** | • 바이너리 파일 업로드 및 암호화 <br> • 암호화 이력 조회 <br> • 바이너리 파일 다운로드 (원본 파일, 암호화된 파일) |
  | **목표 및 기대효과** | • 사용자가 업로드한 파일을 AES-128 알고리즘으로 암호화한 파일을 제공함으로써 파일의 기밀성을 보장한다. <br> • 사용자에게 암호화 이력을 제공하여 투명성을 확보한다. <br> • 이력 기반으로 업로드한 파일과 암호화된 파일을 제공함으로써 파일 관리 및 추적에 용이하도록 한다. |

# 🛠 사용 프로그램 및 목적

  | 프로그램 | 목적 |
  | :---: | :---: |
  | **Postman** | API 테스트 |
  | **Git** | 프로젝트 작업 내역 관리 |
  | **WebStorm** | React 개발을 위한 IDE |
  | **IntelliJ IDEA** | Spring Boot 개발을 위한 IDE |
  | **draw.io** | 소프트웨어 아키텍처 설계도 및 Flow Chart 그리기 |

# 🚀 개발환경

  | 항목 | 내용 |
  | :---: | :---: |
  | **운영체제** | macOS (Apple M1 칩셋) |
  | **개발 언어** | TypeScript, Java |
  | **프레임워크** | React.js, Spring Boot |
  | **데이터베이스** | H2 Database <br> - 개발용 Embedded 방식 <br>  - 테스트용 In-Memory 방식) |

# 📂 소프트웨어 아키텍처
![encrypter_subject drawio](https://github.com/user-attachments/assets/4c71af54-5fb5-4a45-890d-2f6e4fcb2b09)

# 📑 시나리오

## 1. 바이너리 파일 업로드 및 암호화

  <ul>
    <img width="455" alt="scenario1 file_upload" src="https://github.com/user-attachments/assets/910247b8-b278-4c2b-8114-a970f66f4012"><br>
    개요: 사용자가 바이너리 파일을 업로드하면, 시스템은 파일을 AES-128 알고리즘으로 암호화한 파일을 만든다.<br>
      1. 사용자가 [찾아보기] 버튼을 클릭한다.<br>
      2. 사용자가 업로드할 파일을 선택한다.<br>
          <ul>
            2-1. 클라이언트는 해당 파일이 바이너리 파일이 아니면 사용자에게 오류 알림을 띄운다.<br>
          </ul>
      3. 사용자가 [제출하기] 버튼을 클릭한다.<br>
      4. 클라이언트는 서버에 파일 업로드 요청을 한다.<br>
      5. 클라이언트는 파일 업로드에 3초 이상 소요되는 경우 사용자에게 진행 상태를 보여준다.<br>
      6. 서버는 파일 업로드 요청을 확인한다.<br>
      7. 서버는 파일 업로드를 진행한다.<br>
          <ul>
            7-1. 요청된 파일이 바이너리 파일인지 확인한다.<br>
            7-2. 업로드한 파일을 로컬에 저장한다.<br>
            7-3. 업로드한 파일의 내용을 암호화한다.<br>
            7-4. 암호화한 파일을 로컬에 저장한다.<br>
            7-5. 암호화 이력을 저장한다.<br>
          </ul>
      8. 서버에서 파일 업로드 과정 중 오류가 생기면 클라이언트는 사용자에게 오류 알림을 띄운다.<br>
      9. 서버에서 파일 업로드를 성공하면 클라이언트에 응답한다.<br>
      10. 클라이언트는 사용자에게 성공 알림을 띄운다.<br>
      11. 클라이언트는 암호화 이력을 조회한다.<br>
      12. 클라이언트는 사용자에게 메인화면을 보여준다.<br>
  </ul>
      
## 2. 암호화 이력 조회
  
  <ul>
   <img width="455" alt="scenario2 check_encryption_logs" src="https://github.com/user-attachments/assets/f3ca8774-c24f-4f78-bc67-8f427b54f98c"><br>
   개요: 사용자는 이전 암호화 이력 테이블을 조회한다. 기본적으로 첫 번째 페이지를 보여주며, 페이지를 이동할 수 있다.<br>
      1. 사용자가 메인 화면에 접속한다.<br>
          <ul>
            1-1. 클라이언트는 암호화 이력의 첫 번째 페이지를 서버에 요청한다.<br>
          </ul>
      2. 사용자가 ◀︎ 아이콘을 클릭한다.<br>
          <ul> 
            2-1. 클라이언트는 현재 암호화 이력 페이지가 첫 번째인지 확인한다.<br>
            2-2. 현재 첫 번째 페이지라면 알림을 띄운다.<br>
            2-3. 클라이언트는 암호화 이력의 이전 페이지를 서버에 요청한다.<br>
          </ul>
      3. 사용자가 ▶ 아이콘을 클릭한다.<br>
          <ul>
            3-1. 클라이언트는 현재 암호화 이력 페이지가 마지막인지 확인한다.<br>
            3-2. 현재 마지막 페이지라면 알림을 띄운다.<br>
            3-3. 클라이언트는 암호화 이력의 다음 페이지를 서버에 요청한다.<br>
          </ul>
      4. 서버는 클라이언트의 요청을 확인한다.<br>
      5. 서버는 데이터베이스에서 암호화 이력을 가져온다.<br>
      6. 서버에서 암호화 이력 조회 과정 중 오류가 발생하면 클라이언트는 사용자에게 오류 알림을 띄운다.<br>
      7. 서버에서 암호화 이력 조회에 성공하면 클라이언트에 응답한다.<br>
      8. 클라이언트는 사용자에게 암호화 이력의 특정 페이지를 보여준다.<br>
        <ul>
          8-1. 1번 요청일 경우 첫 번째 페이지를 보여준다.<br>
          8-2. 2번 요청일 경우 이전 페이지를 보여준다.<br>
          8-3. 3번 요청일 경우 다음 페이지를 보여준다.<br>
        </ul>
  </ul>

## 3. 바이너리 파일 다운로드

 <ul>
   <img width="455" alt="scenario3 download_file" src="https://github.com/user-attachments/assets/6398b46d-ab37-4986-b5be-b686c40a304e"><br>
   개요: 사용자는 암호화 이력 테이블에서 '암호화 대상 파일', '암호화 된 파일'을 다운로드한다.<br>
      1. 사용자가 암호화 이력 테이블에서 [다운로드] 아이콘을 클릭한다.<br>
      2. 클라이언트는 서버에 파일 다운로드를 요청한다.<br>
      3. 서버는 로컬에서 해당 파일을 가져온다.<br>
      4. 서버에서 파일을 가져오지 못하면 클라이언트는 사용자에게 오류 알림을 띄운다.<br>
      5. 서버는 파일을 가져오면 클라이언트에 파일을 포함하여 응답한다.<br>
      6. 클라이언트는 사용자에게 성공 알림을 띄운다.<br>
      7. 클라이언트는 사용자에게 파일을 제공한다.<br>
 </ul>

# 🔍 기능 정의
 <table>
  <tr>
    <td>기능 분류</td>
    <td>기능명</td>
    <td>기능 설명</td>
  </tr>
  <tr>
    <td rowspan="5" ><strong>파일 업로드</strong></td>
    <td>파일 업로드</td>
    <td>• [제출하기] 클릭 시, 파일 선택 가능 <br>
      • binary 파일형식인지 확인 필요함 <br>
      • [제출하기] 클릭 시, 파일 업로드 진행 <br>
    </td>
  </tr>
  <tr>
    <td >업로드 진행 상태 표시</td>
    <td>• 업로드에 3초 이상 소요되는 경우, 업로드 진행 상태 보여줌 <br>
     • [제출하기] 밑에 progress bar로 표시 <br>
     • 업로드에 3초 미만 소요되는 경우, progress bar가 나타나지 않도록 함 <br>
    </td>
  </tr>
  <tr>
    <td>파일 암호화</td>
    <td>• 업로드된 파일의 내용을 AES-128 알고리즘으로 암호화함 <br>
     • AES-128 암호에 필요한 IV는 매 암호화 요청시 랜덤으로 생성함 <br>
     • AES-128 암호에 필요한 secret key는 임의의 16bytes 값으로 지정함 <br>
    </td>
  </tr>
  <tr>
    <td>파일 로컬 저장</td>
    <td>• 업로드 된 파일을 특정 경로에 저장함 <br>
     • 암호화 된 내용을 새로운 파일명으로 특정 경로에 저장함 <br>
         (새로운 파일명: 암화화 전 파일명 +_enc) <br>
     • 암호화 된 내용을 새로운 파일명으로 특정 경로에 저장함 <br>
    </td>
  </tr>
  <tr>
    <td>암호화 이력 저장</td>
    <td>• 매 암호화 진행 시 암호화 이전 파일명, 암호화된 파일명, IV값을 DB에 저장함 <br>
     • DB에 저장할 시점도 함께 DB에 저장함 <br>
    </td>
  </tr>
  <tr>
    <td rowspan="2" ><strong>암호화 이력 조회</strong></td>
    <td>암호화 이력 조회</td>
    <td>• 암호화 요청 이력을 테이블에 최신순으로 보여줌 <br>
     • 암호화 요청 이력 데이터에서 '암호화 대상 파일명', '암호화된 파일명', 'IV', '일시'를 가져옴 <br>
     • 'IV'는 32자의 문자열로 보여줌 <br>
     • '일시'는 yyyy-MM-dd HH:mm:ss 형태로 보여줌 <br>
     • 테이블 크기는 5로 지정함 <br>
     • 항상 첫 번째 페이지를 보여줌 <br>
    </td>
  </tr>
  <tr>
    <td>페이지 이동</td>
    <td>• ▶ 아이콘 클릭 시, 다음 페이지의 암호화 이력을 보여줌 <br>
     • ◀ 아이콘 클릭 시, 이전 페이지의 암호화 이력을 보여줌 <br>
    </td>
  </tr>
  <tr>
    <td>파일 다운로드</td>
    <td>파일 다운로드</td>
    <td>• 파일명 옆 아이콘을 클릭하면, 해당 파일 다운로드 진행 <br>
     • 특정 경로에서 해당 파일명으로 파일을 가져옴
    </td>
  </tr>
</table>

# ⚙️ 실행
  
# 🛠 테스트 케이스
  <img width="1148" alt="test_case" src="https://github.com/user-attachments/assets/ba4b33f6-e12e-46e5-a741-6dd67b35bdf5">

# 👩‍💻 구현 내용
## 📑 API 명세서
https://documenter.getpostman.com/view/26455565/2sA3kdAHwn

## 🖥 Client
  ### 1. Progress Bar
  
  Axios를 이용하여 비동기적으로 구현하기 <br>
  * 파일 업로드 요청 및 진행 상태 업데이트 & 업로드 종료 처리 및 초기화
      ```javascript
        import { AxiosProgressEvent } from "axios";  // AxiosProgressEvent 이용 

        const handleUpload = async () => {
          if (!selectedFile) {
              return;
          }
          setUploading(true); // 업로드 시작
          
          const startTime = Date.now();
          
          try {
              const formData = new FormData();
              formData.append("file", selectedFile);
              
              // 파일 업로드 요청
              await uploadFile("POST", "/file/upload",
                  formData, (progressEvent: AxiosProgressEvent) => {
                  if (progressEvent.total !== undefined &&  progressEvent.loaded !== progressEvent.total ) {
                      const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);  // progressEvent.loaded, total로 현재 진행 상태 % 계산
                      setUploadProgress(progress); // 진행 상태 업데이트
                      setShowProgressBar(true); // 프로그레스 바 표시
                  }
              });
              
              // 업로드 완료 후 성공 알림
              window.alert("파일 업로드를 성공했습니다.");
              setShowProgressBar(false);

              // 페이지를 첫 페이지로 설정하고 이력 가져오기
              page !== 0 ? setPage(0) : await fetchEncryptionLogs(0);
          } catch (error) {
              // 업로드 실패 시 오류 알림
              window.alert("오류: 파일 업로드 과정에서 오류가 발생했습니다.");
          } finally {
              // 업로드 상태 관련 상태들 초기화
              setUploading(false); // 업로드 종료
              setUploadProgress(0); // 진행 상태 초기화
              setSelectedFile(null); // 파일 선택 상태 초기화

              // 업로드 시간 계산 후 3초 미만일 때 프로그레스 바 숨기기
              const elapsedTime = Date.now() - startTime;
              if (elapsedTime < PROGRESS_BAR_DELAY) {
                  setShowProgressBar(false); // 프로그레스 바 숨기기
              }
          }
        };
      ```

  * Axios 설정
    - uploadFile : 서버에 파일 업로드 요청을 보내는 함수 <br>
    - axios를 사용하여 파일을 업로드할 때, onUploadProgress 콜백을 전달하면 axios는 해당 콜백을 통해 업로드 진행 상태를 계속해서 전달받고 처리 <br>
    - onUploadProgress(AxiosProgressEvent) 콜백 함수:주기적으로 호출되어 업로드 진행 상태 감지 -> 업로드 상태 실시간으로 업데이트 가능 <br>
    ```javascript
      export const uploadFile = async (
          method: Method | undefined,
          url: string,
          data?: any,
          onUploadProgress?: (progressEvent: AxiosProgressEvent) => void
      ) => {
          const reqHeader = {
              "Content-Type": "multipart/form-data",
          };
          const formData = new FormData();
          data.forEach((item: any) => {
              formData.append("file", item);
          });
  
      document.body.style.cursor = "wait";
  
      // axios를 사용하여 파일 업로드 요청
      return axios({
          headers: reqHeader,
          method,
          url: SERVER_DEPLOY_URL + url,
          data: formData,
          onUploadProgress,
      })
          .then((res) => {
              document.body.style.cursor = "default";
              return res.data;
          })
          .catch((err) => {
              throw err;
          });
      };
    ```

  ### 2. Context API

  ### 3. Pagination

## 🖥 Server
  ### 1. javadoc
  
  
  ### 2. AES-128 암호화
    
  자바에서 AES-128 적용하기 : 자바와 같은 JVM 기반 언어에서 ```java.security``` 패키지, ```javax.crypto``` 패키지를 사용하여 AES 암호화를 사용할 수 있음
  
  * 암호화에 사용되는 상수와 변수 선언
     ```java
        private static final String ALGORITHM = "AES";  // 암호화에 사용할 알고리즘
  
        /* 사용되는 알고리즘/블록 암호화 모드/패딩 방식
          자바에서 AES/CBC/PKCS7Padding을 지원하지 않아서 PKCS5Padding로 대체 사용 (로직 상 차이 없음)
         */
        private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
     
        private static final int IV_LENGTH = 16;  // AES 알고리즘에서는 IV값이 16바이트로 고정됨
  
       /* 암호화에 사용할 16바이트(128비트)의 비밀키
         안전하게 관리되도록 application.properties에 설정함
       */
        @Value("${aes.key}")
        private String SECRET_KEY_STRING;
      ```
  * 암호화에 필요한 객체와 값 생성
     ```java
         /**
         * Cipher 가져오기
         * @throws CustomException 암호를 가져오지 못한 경우
         * @return cipher 객체
         */
        public Cipher getCipherInstance(){
            try {
                return Cipher.getInstance(TRANSFORMATION);  // javax.crypto 패키지의 Cipher 클래스를 이용 -> AES-128 암호화에 적용될 Cipher 인스턴스 생성
            } catch (Exception e) {
                throw new CustomException(EncryptionErrorCode.GET_CIPHER_FAIL);
            }
        }
        /**
         * IV값 랜덤 생성
         * @return iv iv값
         */
        public static byte[] generateIV() {
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);  // java.security 패키지의 SecureRandom 함수를 사용하여 무작위한 IV값을 생성함
            return iv;
        }
    
        /**
         * 알고리즘에 부합한 형태의 키 생성
         * @return key
         */
        public SecretKey getSecretKey(String stringSecretKey) {
           /* javax.crypto 패키지의 SecretKeySpec을 사용하여 주어진 문자열을 SecretKeySpec 객체로 만듬
             SecretKeySpec은 SecretKey 인터페이스의 구현체로, 암호화/복호화 시 사용함
           */
            SecretKeySpec key = new SecretKeySpec(stringSecretKey.getBytes(), ALGORITHM);
            return key;
        }
      ```
    
  * AES-128 암호화 적용
     ```java
         /**
         * Cipher로 암호화 진행
         * @param cipher 사용할 cipher
         * @param key key값
         * @param iv iv값
         * @param content 암호화할 내용
         * @throws CustomException 암호화에 실패한 경우
         * @return 암호화된 내용
         */
        public byte[] useCipher(Cipher cipher, SecretKey key, byte[] iv, byte[] content){
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));  // IV와 key를 cipher에 설정
                byte[] encryptedContent = cipher.doFinal(content); // 암호화
                return encryptedContent;
            } catch (Exception e) {
                throw new CustomException(EncryptionErrorCode.ENCRYPT_FAIL);
            }
        }
      ```

  ### 3. Pagination
  
  
  ### 4. TDD
  
  - FileServiceTest : 파일 업로드, 파일 다운로드 관련 Service 메소드 Request-Response 검증 <br>
      <img width="625" alt="file_service_test" src="https://github.com/user-attachments/assets/6d06c295-aff5-4558-9475-857d4dc147ef"> <br>
  - EncryptionServiceTest : 암호화 이력 조회, 파일 내용 암호화 관련 Service 메소드 Request-Response 검증 <br>
      <img width="625" alt="encryption_service_Test" src="https://github.com/user-attachments/assets/04dbbe5c-78e3-438a-b996-85c3eaa77189"> <br>
  - EncryptionLogRepositoryTest : 암호화 이력 조회 관련 Repository 메소드 Request-Response 검증 <br>
      <img width="625" alt="EncryptionLogRepositoryTest" src="https://github.com/user-attachments/assets/ff690de8-4964-484d-bc11-5ccc671d12ec"> <br>
  - EncryptionResponseDTOTest : 암호화 이력 조회 시 DTO 형변환이 올바르게 진행되는지 검증 <br>
      <img width="605" alt="EncryptionResponseDTOTest" src="https://github.com/user-attachments/assets/b7e59624-3690-4ab5-85aa-cbf3b4b2e6c2"> <br>

## 🖥 DevOps
  ### 1. Docker
  <img width="1221" alt="docker_container" src="https://github.com/user-attachments/assets/c7250d8e-796c-487d-9a2e-dba1e5089d71">
  <img width="1221" alt="docker_container_details" src="https://github.com/user-attachments/assets/ba8347a6-5815-4b43-94c5-177e23898bb0">

  ### 2. AWS
