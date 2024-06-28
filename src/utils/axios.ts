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
 *
 * 참고 : https://github.com/lameesanees/gis-frontend
 *
 * @param method HTTP 메소드
 * @param url 요청 URL
 * @param data 업로드할 파일 데이터
 */
export const uploadFile = async (
    method: Method | undefined,
    url: string,
    data?: any
) => {
    // 요청 헤더 설정
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
    })
        .then((res) => {
            document.body.style.cursor = "default";
            return res.data;
        })
        .catch((err) => {
            throw err;
        });
};
