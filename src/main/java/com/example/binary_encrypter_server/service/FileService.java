package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.request.FileRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.dto.response.FileResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.FileErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
/**
 * 바이너리 파일 업로드 및 다운로드를 처리하는 서비스
 */
public class FileService {
    @Value("${file.path}")
    private String FILE_PATH;
    private final ResourceLoader resourceLoader;
    @Autowired
    private final EncryptionService encryptionService;


    /**
     * 바이너리 파일 업로드 처리
     *  1. 업로드 파일 특정 경로에 저장
     *  2. 파일의 내용 암호화
     *  3. 암호화된 파일 생성
     *  4. 암호화 내역 저장
     *
     *  참고: https://github.com/Gomding/spring-file
     *
     * @param file 업로드할 파일
     * @throws CustomException 업로드한 파일이 binary file이 아니거나 file name이 유효하지 않은 경우
     */
    public void uploadFile(MultipartFile file){
        String fileName = file.getOriginalFilename();
        // file 유효성 체크
        if (fileName != null && fileName.endsWith(".bin")) {
            ;
        } else {
            throw new CustomException(FileErrorCode.INVALID_FILE);
        }

        // 특정 경로에 파일을 저장
        Path targetPath = uploadFileToPath(file);

        // 파일 내용 암호화
        byte[] content = new byte[0];
        content = getFileContent(targetPath);
        FileResponseDTO fileDTO = new FileResponseDTO(fileName, content);
        EncryptResponseDTO encryptResponseDto = encryptionService.encrypt(fileDTO.getContent());

        // 암호화된 파일 생성
        String originName = fileDTO.getFileName();
        String newName = createFileName(originName, "_enc");  // 파일명 + _enc

        // 새로운 파일 생성
        FileRequestDTO createFileRequestDTO = new FileRequestDTO(newName, encryptResponseDto.getEncryptedContent());
        createFile(createFileRequestDTO);

        // 암호화 이력 생성
        EncryptionLogRequestDTO createEncryptionLogRequestDTO = new EncryptionLogRequestDTO(
                    originName, newName, encryptResponseDto.getIv()
            );
        encryptionService.createEncryptionLog(createEncryptionLogRequestDTO);
    }

    /**
     * 바이너리 파일 다운로드
     *
     * 참고: https://github.com/Gomding/spring-file
     *
     * @param name 다운로드할 파일명
     * @throws CustomException 파일이 존재하지 않거나 읽을 수 없는 경우
     * @return resource 파일
     */
    public Resource downloadFile(String name) {
        Path filePath = Paths.get(FILE_PATH).resolve(name).normalize();
        Resource resource = resourceLoader.getResource("file:" + filePath.toString());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new CustomException(FileErrorCode.NOT_FOUND_FILE);
        }
    }

    /**
     * 파일을 특정 경로에 저장
     * @param file 파일
     * @throws IOException 지정된 경로에 파일을 생성하지 못한 경우
     */
    public Path uploadFileToPath(MultipartFile file){
        String fileName = file.getOriginalFilename();
        // 1. 업로드 파일 특정 경로에 저장
        Path filePath = Paths.get(FILE_PATH).toAbsolutePath().normalize();
        Path targetPath = filePath.resolve(fileName).normalize();

        try {
            Files.createDirectories(filePath); // 경로가 없는 경우 디렉토리 생성
            file.transferTo(targetPath);
        } catch (IOException e) {
            throw new CustomException(FileErrorCode.CREATE_FILE_FAIL);
        }

        return targetPath;
    }

    /**
     * 파일의 content 가져오기
     * @param filePath 파일 경로
     * @throws CustomException
     *          1. 파일 경로에 파일이 존재하지 않는 경우
     *          2. 파일을 읽어올 수 없는 경우
     * @return 파일에서 읽어온 byte array
     */
    public byte[] getFileContent(Path filePath){
        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            throw new CustomException(FileErrorCode.INVALID_FILE_PATH);
        }
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new CustomException(FileErrorCode.NOT_FOUND_FILE);
        }
    }

    /**
     * 특정 경로에 새로운 파일 생성
     * @param fileRequestDTO 파일명, 파일 내용
     * @throws CustomException 파일을 생성할 수 없는 경우
     */
    public void createFile(FileRequestDTO fileRequestDTO) {
        String fileName = fileRequestDTO.getFileName();
        Path filePath = Paths.get(FILE_PATH).resolve(fileName).normalize();
        byte[] content = fileRequestDTO.getContent();

        try {
            Files.createDirectories(filePath.getParent()); // 경로가 없는 경우 디렉토리 생성
            Files.write(filePath, content); // 파일 생성
        } catch (IOException e) {
            throw new CustomException(FileErrorCode.CREATE_FILE_FAIL);
        }
    }

    /**
     * 새로운 파일명 생성
     * - 파일확장자와 구분지어 파일명 끝에 end 문자열 합성
     * @param originName 기존 파일명
     * @param end 새로 붙여질 어미
     * @return newFileName 새로운 파일명
     */
    public String createFileName(String originName, String end) {
        String newFileName = originName.substring(0, originName.lastIndexOf('.'))
                + end
                + originName.substring(originName.lastIndexOf('.'));
        return newFileName;
    }
}
