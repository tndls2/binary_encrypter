import axios, { AxiosProgressEvent, Method } from "axios";

const SERVER_DEPLOY_URL = "http://localhost:8080";


/**
 * HTTP 요청을 보내는 함수
 * @param method HTTP 메소드
 * @param url 요청 URL
 * @param data 요청에 포함될 데이터
 */
export const request = async (
    method: Method | undefined,
    url: string,
    data?: any
) => {
    document.body.style.cursor = "wait";

    return axios({
        method,
        url: SERVER_DEPLOY_URL + url,
        data,
    })
        .then((res) => {
            document.body.style.cursor = "default";
            return res.data;
        })
        .catch((err) => {
            throw err;
        });
};

/**
 * 파일 업로드 요청을 보내는 함수
 * 참고: https://hagohobby.tistory.com/22
 * @param method HTTP 메소드
 * @param url 요청 URL
 * @param data 업로드할 파일 데이터
 * @param onUploadProgress 업로드 진행 상황 이벤트 핸들러
 */
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

/**
 * 파일 다운로드 요청을 보내는 함수
 *
 * @param method HTTP 메소드
 * @param url 요청 URL
 * @param data 요청에 포함될 데이터
 */
export const requestFile = async (
    method: Method | undefined,
    url: string,
    data?: any
) => {
    document.body.style.cursor = "wait";
    return axios({
        method,
        url: SERVER_DEPLOY_URL + url,
        data,
        responseType: "blob", // 파일 다운로드를 위해 Blob으로 설정
    })
        .then((response) => {
            // 응답 데이터를 Blob 객체로 변환
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement("a");
            link.href = url;
            link.setAttribute("download", data); // 다운로드 속성 설정 (파일명)
            document.body.appendChild(link);
            link.click(); // 파일 다운로드 실행
            document.body.style.cursor = "default";
            link.remove();
        })
        .catch((err) => {
            throw err;
        });
};