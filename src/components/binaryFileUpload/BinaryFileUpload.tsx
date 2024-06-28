import React, { useState, ChangeEvent, useRef } from "react";
import { AxiosProgressEvent } from "axios";

import { uploadFile } from "../../utils/axios";
import { useEncryptionLogs } from "../../context/EncryptionLogsContext";

import "./BinaryFileUpload.css";

const PROGRESS_BAR_DELAY = 3000;
/**
 * 바이너리 파일 업로드 컴포넌트
 */
export default function BinaryFileUpload() {
    const { page, setPage, fetchEncryptionLogs } = useEncryptionLogs();

    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [uploadProgress, setUploadProgress] = useState<number>(0); // 업로드 진행 상태
    const [uploading, setUploading] = useState<boolean>(false); // 업로드 중 여부
    const [showProgressBar, setShowProgressBar] = useState<boolean>(false); // 프로그레스 바 표시 여부

    const inputEl = useRef<HTMLInputElement>(null);

    /**
     * '찾아보기' 버튼 클릭 핸들러
     */
    const handleClick = () => {
        if (inputEl.current) {
            inputEl.current.click();
        }
    };

    /**
     * 파일 선택 핸들러
     * @param {ChangeEvent<HTMLInputElement>} e - 파일 입력 요소의 change 이벤트
     */
    const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0) {
            const file = e.target.files[0];
            const fileExtension = file.name.split(".").pop()?.toLowerCase();

            // binary file 인지 확인
            if (fileExtension === "bin") {
                setSelectedFile(file);
            } else {
                setSelectedFile(null);
                window.alert("오류: .bin 파일만 업로드할 수 있습니다.");
            }
        }
    };

    /**
     * 파일 업로드 핸들러
     * @description 업로드 요청이 처리가 3초 이상 걸리는 경우에는 프로그레스 바로 진행상황을 보여줌
     * 참고: https://hagohobby.tistory.com/22
     * 참고: https://velog.io/@nike7on/React%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%ED%8C%8C%EC%9D%BC-%EC%97%85%EB%A1%9C%EB%93%9C-%EC%BB%B4%ED%8F%AC%EB%84%8C%ED%8A%B8-%EA%B5%AC%ED%98%84
     */
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
                if (progressEvent.total !== undefined) {
                    const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                    setUploadProgress(progress); // 진행 상태 업데이트
                    setShowProgressBar(true); // 프로그레스 바 표시
                }
            });

            // 업로드 완료 후 성공 알림
            window.alert("File uploaded successfully");
            setShowProgressBar(false);

            // 페이지를 첫 페이지로 설정하고 이력 가져오기
            page !== 0 ? setPage(0) : await fetchEncryptionLogs(0);
        } catch (error) {
            // 업로드 실패 시 오류 알림
            window.alert("Failed to upload file");
        } finally {
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

    return (
        <div className="file-upload">
            <div className="custom-upload">
                <input
                    placeholder={
                        selectedFile
                            ? selectedFile.name
                            : "암호화 대상 파일을 업로드 하세요" // 선택된 파일이 있으면 파일 이름 표시, 없으면 기본 메시지 표시
                    }
                    disabled
                />
                <button onClick={handleClick}>찾아보기</button>
            </div>
            <input
                className="upload-input"
                type="file"
                ref={inputEl}
                onChange={handleFileChange}
            />
            <button
                className={selectedFile ? "upload-button" : "disabled-button"}
                onClick={handleUpload}
                disabled={uploading}
            >
                {uploading ? "업로딩 중..." : "제출하기"}
            </button>
            {showProgressBar && (
                <div className="progress-bar">
                    <div
                        className="progress-bar-fill"
                        style={{ width: `${uploadProgress}%` }}
                    >
                        {`${uploadProgress}%`}
                    </div>
                </div>
            )}
        </div>
    );
}
