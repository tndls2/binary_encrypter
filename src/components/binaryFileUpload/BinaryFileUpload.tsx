import { useState, ChangeEvent, useRef } from "react";
import { AxiosProgressEvent } from "axios";

import { uploadFile } from "../../utils/axios";
import { useEncryptionLogs } from "../../context/EncryptionLogsContext";

import "./BinaryFileUpload.css";

/**
 * 바이너리 파일 업로드 컴포넌트
 *
 * @const handleClick: 파일 입력 요소를 클릭 -> 파일 선택 창을 염
 * @const handleFileChange: 파일 선택 시 .bin 파일인지 체크 -> 상태 업데이트
 * @const handleUpload: 선택된 파일 서버에 업로드
 * @cons
 */
export default function BinaryFileUpload() {
    const { page, setPage, fetchEncryptionLogs } = useEncryptionLogs();

    const [selectedFile, setSelectedFile] = useState<File | null>(null);

    const inputEl = useRef<HTMLInputElement>(null);

    /**
     * '찾아보기' 버튼 클릭 핸들러
     * 참고 : https://stackoverflow.com/questions/39913863/how-to-manually-trigger-click-event-in-reactjs
     */
    const handleClick = () => {
        if (inputEl.current) {
            inputEl.current.click();
        }
    };

    /**
     * 파일 선택 핸들러
     * 참고 : https://stackoverflow.com/questions/190852/how-can-i-get-file-extensions-with-javascript
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
     * 참고 : https://velog.io/@nike7on/React%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%ED%8C%8C%EC%9D%BC-%EC%97%85%EB%A1%9C%EB%93%9C-%EC%BB%B4%ED%8F%AC%EB%84%8C%ED%8A%B8-%EA%B5%AC%ED%98%84
     * 참고 : https://stackoverflow.com/questions/63126885/show-an-error-to-upload-the-file-or-disable-de-button
     */
    const handleUpload = async () => {
        try {
            await uploadFile("POST", "/file/upload", [selectedFile]);  // 파일 업로드 요청
            window.alert("File uploaded successfully");

            page !== 0 ? setPage(0) : await fetchEncryptionLogs(0);  // 페이지를 첫 페이지로 설정하고 이력 가져오기
        } catch (error) {
            window.alert("Failed to upload file");
        }

        setSelectedFile(null);  // 파일 선택 상태 초기화
    };

    return (
        <div className="file-upload">
            <div className="custom-upload">
                <input
                    placeholder={
                        selectedFile
                            ? selectedFile.name
                            : "암호화 대상 파일을 업로드 하세요"  // 선택된 파일이 있으면 파일 이름 표시, 없으면 기본 메시지 표시
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
            <button className={selectedFile ? "upload-button" : "disabled-button"} onClick={handleUpload}>
                제출하기
            </button>
        </div>
    );
}
