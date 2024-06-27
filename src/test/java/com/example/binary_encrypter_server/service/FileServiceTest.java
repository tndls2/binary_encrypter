package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.request.FileRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @BeforeEach
    void setUp() throws IOException {
        createTestFile(); // 테스트용 파일 생성
    }

    // 테스트 파일 생성 메서드
    private void createTestFile() throws IOException {
        File directory = new File(TEST_FILE_DIRECTORY);
        directory.mkdirs();

        File file = new File(TEST_FILE_DIRECTORY + TEST_FILE_NAME);
        file.createNewFile();

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("Test file content".getBytes());
        }
    }


    @Test
    @DisplayName("파일 다운로드 성공 테스트")
    void downloadFile_Success() {
        // given
        String fileName = TEST_FILE_NAME;


        // when
        Resource result = fileService.downloadFile(fileName);

        // then
        assertEquals(fileName, result.getFilename());
    }

    @Test
    @DisplayName("파일 업로드 성공 테스트")
    void uploadFile_Success() throws IOException {
        // given
        MultipartFile file = new MockMultipartFile("test.bin", "test.bin", "application/octet-stream", "Test file content".getBytes());
        EncryptResponseDTO encryptResponseDTO = new EncryptResponseDTO("encryptedContent".getBytes(), "iv");

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
    void uploadFileToPath_Success() throws IOException {
        // given
        String originalFileName = "upload-test.bin";
        MultipartFile file = new MockMultipartFile(originalFileName, originalFileName, "application/octet-stream", "Test file content".getBytes());

        // when
        Path uploadedFilePath = fileService.uploadFileToPath(file);

        // then
        assertNotNull(uploadedFilePath);
        assertTrue(Files.exists(uploadedFilePath));

        // 프로젝트 루트 디렉토리를 기준으로 상대 경로 계산
        Path expectedFilePath = Paths.get("").toAbsolutePath()
                .resolve(TEST_FILE_DIRECTORY)
                .resolve(originalFileName)
                .normalize();

        assertEquals(expectedFilePath.toString(), uploadedFilePath.toString());
    }

//    @Test
//    void uploadFileToPath_FailOnIOException() throws IOException {
//        // given
//        MultipartFile file = mock(MultipartFile.class);
//        when(file.getOriginalFilename()).thenReturn("test.bin");
//        // when, then
//        assertThrows(CustomException.class, () -> fileService.uploadFileToPath(file));
//    }

    @Test
    void getFileContent_Success() {
        // given
        Path filePath = Paths.get(TEST_FILE_DIRECTORY).resolve(TEST_FILE_NAME);

        // when
        byte[] content = fileService.getFileContent(filePath);

        // then
        assertNotNull(content);
        assertTrue(content.length > 0);
    }

    @Test
    void getFileContent_InvalidPath() {
        // given
        Path invalidPath = Paths.get(TEST_FILE_DIRECTORY).resolve("invalid-file-name.txt");

        // when, then
        assertThrows(CustomException.class, () -> fileService.getFileContent(invalidPath));
    }

    @Test
    void createFile_Success() {
        // given
        FileRequestDTO fileRequestDTO = new FileRequestDTO();
        fileRequestDTO.setFileName("new-file.txt");
        fileRequestDTO.setContent("New file content".getBytes());

        // when
        fileService.createFile(fileRequestDTO);

        // then
        Path filePath = Paths.get(TEST_FILE_DIRECTORY).resolve("new-file.txt");
        assertTrue(Files.exists(filePath));
    }

    @Test
    void createFileName_Success() {
        // given
        String originName = "test.txt";
        String end = "_new";

        // when
        String newFileName = fileService.createFileName(originName, end);

        // then
        assertEquals("test_new.txt", newFileName);
    }
}
