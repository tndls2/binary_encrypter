package com.example.binary_encrypter_server.controller;

import com.example.binary_encrypter_server.dto.FileResponseDTO;
import com.example.binary_encrypter_server.service.EncryptionService;
import com.example.binary_encrypter_server.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

//@RestController
@Controller  //html 테스트 임시 사용
@RequiredArgsConstructor
@RequestMapping("/") //todo:나중에 file로 수정
public class FileController {
    private final FileService fileService;

    // 파일 업로드 Form 페이지로 이동(임시)
    @GetMapping("/file")
    public String fileUploadForm(Model model) {
        return "files/index";
    }


    /*
     * 바이너리 파일 업로드
     */
    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFile(MultipartFile file, RedirectAttributes attributes) throws IOException {
        // file 예외상황 처리
        // 파일이 여러개인 경우에 대한 처리
        // 파일명 중복 체크
        String filename = file.getOriginalFilename();
        if (filename.endsWith(".bin")) {
            // 바이너리 파일만 업로드 가능
            try {
                // 업로드 파일 저장
                FileResponseDTO fileDto = fileService.uploadFile(file);
                return ResponseEntity.ok("File uploaded successfully: " + fileDto.getFilename());

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
            }
        } else {
            // 바이너리 파일이 아닌 경우 에러 발생
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Only binary files are allowed.");
        }

    }

    /*
     * 바이너리 파일 다운로드: 파라미터의 파일명(name)으로 조회 후 다운로드
     */
    @GetMapping("/file/{name}/download")
    public ResponseEntity<?> downloadFile(@PathVariable("name") String name) throws IOException {
        // 파일 로드
        byte[] binaryFile = fileService.findFileByName(name);

        // HTTP 헤더 설정 (파일 다운로드를 위한 설정)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", name);

        return new ResponseEntity<>(binaryFile, headers, HttpStatus.OK);
    }
}
