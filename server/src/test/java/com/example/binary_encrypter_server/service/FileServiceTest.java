package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.request.FileRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.exceptions.CommonErrorCode;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.FileErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class FileServiceTest {
    @Autowired
    private FileService fileService;

    @Autowired
    private ResourceLoader resourceLoader;

    @Value("${file.path}")
    private String TEST_FILE_DIRECTORY;  //application-test.properties 참조

    @Value("${file.name}")
    private String TEST_FILE_NAME;  //application-test.properties 참조

    // 테스트 파일 생성 메서드
    private void createTestFile() throws IOException {
        File directory = new File(TEST_FILE_DIRECTORY);
        directory.mkdirs();  // 해당 경로가 없는 경우 생성해줌

        File file = new File(TEST_FILE_DIRECTORY + TEST_FILE_NAME);
        file.createNewFile();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("Test file content".getBytes());
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        createTestFile(); // 테스트용 binary 파일 미리 생성
    }

    @Nested
    @DisplayName("바이너리 파일 업로드 처리 메소드 테스트")
    class uploadFile {
        @Test
        @DisplayName("바이너리 파일이 업로드되는 경우")
        void uploadFileSuccess () throws IOException {
        // given
        MultipartFile file = new MockMultipartFile("test.bin", "test.bin",
                "application/octet-stream", "Test file content".getBytes());
        EncryptResponseDTO encryptResponseDTO = new EncryptResponseDTO("encryptedContent".getBytes(), "iv".getBytes());

        // EncryptionService Mock 객체 생성
        EncryptionService encryptionServiceMock = mock(EncryptionService.class);
        when(encryptionServiceMock.encrypt(any())).thenReturn(encryptResponseDTO);

        // FileService에 Mock 객체 주입
        ReflectionTestUtils.setField(fileService, "encryptionService", encryptionServiceMock);

        // when
        fileService.uploadFile(file);

        // then
        verify(encryptionServiceMock, times(1)).encrypt(any());
        verify(encryptionServiceMock, times(1)).createEncryptionLog(any(EncryptionLogRequestDTO.class));
        }

        @Test
        @DisplayName("업로드하는 파일이 binary file이 아니거나 유효하지 않는 이름이 NOT_BINARY_FILE 발생")
        void uploadFileFail () throws IOException {
        // given (originalFilename이 빈 문자열)
        MultipartFile file = new MockMultipartFile("test.bin", "",
                "application/octet-stream", "Test file content".getBytes());
        // when, then
        assertThatThrownBy(() -> fileService.uploadFile(file))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", FileErrorCode.INVALID_FILE);
        }
    }

    @Nested
    @DisplayName("파일 다운로드 메소드 테스트")
    class downloadFile {
        @Test
        @DisplayName("파일명을 가지고 해당 파일 다운로드에 성공한 경우")
        void downloadFileSuccess () {
        // given
        String fileName = TEST_FILE_NAME;


        // when
        Resource result = fileService.downloadFile(fileName);

        // then
        assertEquals(fileName, result.getFilename());
        }
    }

    @Test
    @DisplayName("존재하지 않는 파일명을 입력하면 NOT_FOUND_FILE 발생")
    void downloadFileFail () {
        // given
        String fileName = "not_exist_file.bin";

        // when, then
        assertThatThrownBy(() -> fileService.downloadFile(fileName))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", FileErrorCode.NOT_FOUND_FILE);
    }

    @Test
    @DisplayName("파일을 특정 경로에 저장하는 메소드 테스트")
    void uploadFileToPath() {
        // given
        String originalFileName = "test_upload_file.bin";
        MultipartFile file = new MockMultipartFile(originalFileName, originalFileName, "application/octet-stream", "Test file content".getBytes());

        // when
        Path uploadedFilePath = fileService.saveFile(file, null);

        // then
        assertNotNull(uploadedFilePath);
        assertTrue(Files.exists(uploadedFilePath));

        // 프로젝트 루트 디렉토리를 기준으로 상대 경로 계산
        Path expectedFilePath = Paths.get("").toAbsolutePath()
                .resolve(TEST_FILE_DIRECTORY)
                .resolve(originalFileName)
                .normalize();

        // 테스트 코드에서 기대하는 절대 경로로 변환
        Path expectedAbsolutePath = expectedFilePath.toAbsolutePath();

        // 반환된 경로도 절대 경로로 변환
        Path actualAbsolutePath = uploadedFilePath.toAbsolutePath();

        assertEquals(expectedAbsolutePath.toString(), actualAbsolutePath.toString());
    }


    @Nested
    @DisplayName("파일의 content 가져오는 메소드 테스트")
    class getFileContent{
        @Test
        @DisplayName("입력된 경로를 이용하여 파일의 content를 가져옴")
        void getFileContentSuccess() {
            // given
            Path filePath = Paths.get(TEST_FILE_DIRECTORY).resolve(TEST_FILE_NAME);

            // when
            byte[] content = fileService.getFileContent(filePath);

            // then
            assertNotNull(content);
            assertTrue(content.length > 0);
        }

        @Test
        @DisplayName("유효하지 않은 file path를 입력하면 INVALID_FILE_PATH 발생")
        void getFileContentInvalidPathFail() {
            // given
            Path invalidPath = Paths.get(TEST_FILE_DIRECTORY).resolve("invalid_file.bin");

            // when, then
            assertThatThrownBy(() -> fileService.getFileContent(invalidPath))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", FileErrorCode.INVALID_FILE_PATH);
        }
    }
    @Nested
    @DisplayName("특정 경로에 파일을 저장하는 메소드")
    class saveFile{
        @Test
        @DisplayName("파일명+파일 내용을 파라미터로 넘기면 특정 경로에 파일 저장")
        void saveFileByFileRequestDTOSuccess () {
        // given
        String newName = "test_create_file.bin";
        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setFileName(newName);
        fileRequestDTO.setContent("New file content".getBytes());

        // when
        fileService.saveFile(null, fileRequestDTO);

        // then
        Path filePath = Paths.get(TEST_FILE_DIRECTORY).resolve(newName);
        assertTrue(Files.exists(filePath));
        assertDoesNotThrow(() -> fileService.saveFile(null, fileRequestDTO));
        }

        @Test
        @DisplayName("파일을 파라미터로 넘기면 특정 경로에 파일 저장")
        void saveFileByMultipartFileSuccess () throws IOException {
            // given
            MultipartFile file = new MockMultipartFile("test.bin", "test.bin",
                    "application/octet-stream", "Test file content".getBytes());

            // when
            fileService.saveFile(file, null);

            // then
            Path filePath = Paths.get(TEST_FILE_DIRECTORY).resolve(file.getOriginalFilename());
            assertTrue(Files.exists(filePath));
            assertDoesNotThrow(() -> fileService.saveFile(file, null));
        }

        @Test
        @DisplayName("파라미터 값이 잘못 입력된 경우 INVALID_PARAMETER 발생")
        void saveFileFail () {
            // when, then
            assertThatThrownBy(() -> fileService.saveFile(null, null))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", CommonErrorCode.INVALID_PARAMETER);
        }
    }
}
