package com.example.binary_encrypter_server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.example.binary_encrypter_server.dto.request.FileRequestDTO;
import com.example.binary_encrypter_server.exceptions.CustomException;
import com.example.binary_encrypter_server.exceptions.FileErrorCode;
import com.example.binary_encrypter_server.exceptions.S3ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 업로드한 파일을 AWS S3에 저장하는 메서드
     * @apiNote
     *  클라이언트로부터 업로드된 파일을 받아 AWS S3 버킷에 저장하고,
     *  파일이 저장된 후 S3의 직접 접근 가능한 URL을 반환함
     * @param file 업로드할 파일
     * @throws CustomException 파일을 S3에 업로드하는 과정에서 예외가 발생할 경우
     * @return url AWS S3에 업로드된 파일의 직접 접근 가능한 URL
     */
    public String uploadFileToS3(MultipartFile file) throws IOException {
        // 파일의 원본 이름을 가져옴
        String originalFilename = file.getOriginalFilename();

        // MultipartFile에서 InputStream을 가져옴
        InputStream is = file.getInputStream();
        // InputStream을 byte 배열로 변환
        byte[] bytes = IOUtils.toByteArray(is);


        // S3에 업로드할 파일의 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType()); // 파일의 ContentType 설정
        metadata.setContentLength(bytes.length); // 파일의 크기를 메타데이터에 설정

        // byte 배열을 ByteArrayInputStream으로 변환
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try {
            // S3에 파일을 업로드하기 위한 PutObjectRequest 생성
            // putObjectRequest(버킷명, 파일명, 파일 데이터, 메타데이터)
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, originalFilename, byteArrayInputStream, metadata)
                            // 업로드된 파일을 퍼블릭으로 설정 (PublicRead 권한 부여)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            // S3에 파일 업로드
            amazonS3.putObject(putObjectRequest);
        }
        finally{
            // 스트림 자원 해제
            byteArrayInputStream.close();
            is.close();
        }

        // AWS S3에 업로드된 파일의 직접 접근 가능한 URL 생성
        String url = amazonS3.getUrl(bucketName, originalFilename).toString();

        // URL 반환
        return url;
    }


    /**
     * AWS S3에서 지정된 파일을 다운로드하여 반환하는 메서드
     * @apiNote
     *  파일 이름을 기반으로 AWS S3에서 파일을 가져오고,
     *  해당 파일의 내용을 `Resource` 형태로 반환함
     * @param fileName 다운로드할 파일의 이름
     * @throws IOException 파일을 다운로드하는 과정에서 I/O 오류가 발생한 경우
     * @throws CustomException S3에서 파일을 가져오는 데 실패한 경우
     * @return Resource 다운로드된 파일
     */
    public Resource downloadFileFromS3(String fileName){
        try {
            // S3에서 파일 가져오기
            S3Object s3Object = amazonS3.getObject(bucketName, fileName);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            // InputStream을 Resource로 변환하여 반환
            return new InputStreamResource(inputStream);

        } catch (Exception e) {
            // S3에서 파일을 가져오는 도중 예외 발생 시 사용자 정의 예외 던지기
            throw new CustomException(S3ErrorCode.GET_FILE_FROM_S3_FAIL);
        }
    }

    /**
     * AWS S3에서 파일의 내용을 가져오는 메서드
     * @param fileName 내용을 불러올 대상 파일의 파일명
     *
     * @throws CustomException S3에서 해당 파일명을 가진 파일을 가져오는 데 실패한 경우 발생
     * @return content 파일의 내용을 담은 byte 배열
     */
    public byte[] getFileContentFromS3(String fileName) {
        try {
            // S3에서 파일을 가져옴
            S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucketName, fileName));

            // 파일의 내용(InputStream)을 가져옴
            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            // InputStream을 byte 배열로 변환
            byte[] content = IOUtils.toByteArray(inputStream);

            // 변환된 byte 배열을 반환
            return content;
        } catch (IOException e) {
            throw new CustomException(S3ErrorCode.GET_FILE_CONTENT_FROM_S3_FAIL);
        }
    }


    /**
     * 암호화된 파일을 AWS S3에 저장하는 메서드
     * @param fileRequestDTO 업로드할 파일의 파일명과 파일 내용을 담고 있는 DTO
     * @throws CustomException S3에서 파일 업로드 중 오류가 발생한 경우
     */
    public void saveEncryptedFileToS3(FileRequestDTO fileRequestDTO) {
        String fileName = fileRequestDTO.getFileName();
        byte[] content = fileRequestDTO.getContent();
        // 파일 내용을 바이트 배열로부터 ByteArrayInputStream으로 변환
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);

        // 파일 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(content.length);
        metadata.setContentType("application/octet-stream");

        // S3에 파일 업로드 요청 생성 및 실행
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, byteArrayInputStream, metadata)
                .withCannedAcl(CannedAccessControlList.Private));
    }
}
