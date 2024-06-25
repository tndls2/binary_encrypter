package com.example.binary_encrypter_server.service;

import com.example.binary_encrypter_server.dto.FileRequestDTO;
import com.example.binary_encrypter_server.dto.FileResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
public class FileService {
    private static final String FILE_UPLOAD_PATH = "src/main/resources/file/";

    /*
     * 파일을 특정 경로에 업로드(저장)
     */
    public FileResponseDTO uploadFile(MultipartFile file) throws IOException {
        // 1. 파일 업로드
        Path fielPath = Paths.get(FILE_UPLOAD_PATH).toAbsolutePath().normalize();
        String filename = file.getOriginalFilename();
        Path targetPath = fielPath.resolve(filename).normalize();
        try {
            file.transferTo(targetPath);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일 업로드에 실패했습니다.");
        }

        // 2. 파일의 content 가져오기
        byte[] content = getFileContent(fielPath);

        // 3. File Response Dto 생성
        FileResponseDTO fileDTO = new FileResponseDTO(filename, content);
        return fileDTO;
    }


    /*
     * 파일의 content 가져오기 (binary 형태)
     */
    private byte[] getFileContent(Path filePath) throws IOException {
        return Files.readAllBytes(filePath);
    }


    /*
     * 파라미터 데이터(파일명, 파일 내용)을 이용하여 특정 경로에 새로운 파일 생성
     */
    public FileResponseDTO createFile(FileRequestDTO fileRequestDTO) throws IOException {
        // 1. 새로운 파일 생성
        String fileName = fileRequestDTO.getFilename();
        Path filePath = Paths.get(FILE_UPLOAD_PATH).resolve(fileName).normalize();
        byte[] content = fileRequestDTO.getContent();
        Files.write(filePath, content);

        // 2. File Response Dto 생성
        FileResponseDTO fileDTO = new FileResponseDTO(fileName, content);
        return fileDTO;
    }

    /* 특정 파일명에 해당하는 파일 가져오기 */
    public byte[] findFileByName(String filename) throws IOException {
        Path filePath = Paths.get(FILE_UPLOAD_PATH).resolve(filename).normalize();
        return Files.readAllBytes(filePath);
    }


    /*
     * 새로운 파일명 생성: 파일확장자와 구분지어 파일명 끝에 end 문자열 합성
     */
    public String createFileName(String originName, String end){
        String newFileName = originName.substring(0, originName.lastIndexOf('.'))
                + end
                + originName.substring(originName.lastIndexOf('.'));

        return newFileName;
    }
}
