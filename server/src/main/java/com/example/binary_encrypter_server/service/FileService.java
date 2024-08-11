package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.dto.request.EncryptionLogRequestDTO;
import com.example.binary_encrypter_server.dto.request.FileRequestDTO;
import com.example.binary_encrypter_server.dto.response.EncryptResponseDTO;
import com.example.binary_encrypter_server.dto.response.FileResponseDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.FileErrorCode;
import com.example.binary_encrypter_server.exceptions.S3ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
/**
 * 바이너리 파일 업로드 및 다운로드를 처리하는 서비스
 */
public class FileService {
    @Value("${file.path}")
    private String FILE_PATH;
    @Autowired
    private final EncryptionService encryptionService;

    @Autowired
    private final S3Service s3Service;

    /**
     * 바이너리 파일 업로드 처리
     * @apiNote
     *  1. 업로드 파일을 S3 bucket에 저장
     *  2. 파일의 내용 암호화
     *  3. 암호화된 파일 생성
     *  4. 암호화 내역 저장
     * @param file 업로드할 파일
     * @throws CustomException 업로드한 파일이 binary file이 아니거나 file name이 유효하지 않은 경우
     */
    public void uploadFile(MultipartFile file){
        String fileName = file.getOriginalFilename();

        // file 유효성 체크
        if (fileName == null || !fileName.endsWith(".bin")) {
            throw new CustomException(FileErrorCode.INVALID_FILE);
        }

        // 파일을 S3에 업로드
        try {
            s3Service.uploadFileToS3(file);
        } catch (IOException e) {
            throw new CustomException(S3ErrorCode.UPLOAD_FILE_TO_S3_FAIL);
        }

        // 파일 내용 S3에서 가져오기
        byte[] content = s3Service.getFileContentFromS3(fileName);

        // 파일 내용 암호화
        FileResponseDTO fileDTO = new FileResponseDTO(fileName, content);
        EncryptResponseDTO encryptResponseDto = encryptionService.encrypt(fileDTO.getContent());

        // 암호화된 파일 생성
        String originName = fileDTO.getFileName();
        String newName = createFileName(originName, "_enc");  // 파일명 + _enc

        FileRequestDTO createFileRequestDTO = new FileRequestDTO(newName, encryptResponseDto.getEncryptedContent());

        // 암호화된 파일을 S3에 저장
        s3Service.saveEncryptedFileToS3(createFileRequestDTO);

        // 암호화 이력 생성
        EncryptionLogRequestDTO createEncryptionLogRequestDTO = new EncryptionLogRequestDTO(
                originName, newName, encryptResponseDto.getIv()
        );
        encryptionService.createEncryptionLog(createEncryptionLogRequestDTO);
    }

    /**
     * 바이너리 파일 다운로드
     * @param name 다운로드할 파일명
     * @return resource 파일
     */
    public Resource downloadFile(String name) {
        Resource resource = s3Service.downloadFileFromS3(name);
        return resource;
    }

    /**
     * 새로운 파일명 생성
     * @apiNote 파일확장자와 구분지어 파일명 끝에 end 문자열 합성
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
