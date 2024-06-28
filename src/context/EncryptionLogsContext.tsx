import React, {createContext, useContext, useState, useEffect, ReactNode} from 'react';

import {request} from "../utils/axios";
import {EncryptionLog} from "../types/encryptionLog";

/*
context API 적용
참고: https://velog.io/@apro_xo/React.js-context-APIFeat.-useContext
참고: https://junheedot.tistory.com/entry/%EC%98%88%EC%A0%9C%EB%A1%9C-%EB%B0%B0%EC%9A%B0%EB%8A%94-react-context
*/

interface EncryptionLogsContextProps {
    data: EncryptionLog[];
    page: number;  // 현재 페이지 번호
    totalPage: number;
    setPage: React.Dispatch<React.SetStateAction<number>>  // 페이지 설정 함수
    fetchEncryptionLogs: (page: number) => void;
}

const EncryptionLogsContext = createContext<EncryptionLogsContextProps|undefined>(undefined); //context 생성

export const useEncryptionLogs = () => {
    const context = useContext(EncryptionLogsContext);
    if(!context) {
        throw new Error("EncryptionLogsContext 안에서만 사용할 수 있습니다.");
    }
    return context;
}

/**
 * 암호화 이력 조회 컴포넌트 조회
 */
export const EncryptionLogsProvider = ({ children }: { children: ReactNode }) => {
    const [data, setData] = useState<EncryptionLog[]>([]);
    const [page, setPage] = useState<number>(0);
    const [totalPage, setTotalPage] = useState<number>(0);

    /**
     * 특정 페이지의 암호화 로그 데이터를 가져오는 함수
     * @param page 가져올 페이지 번호
     */
    const fetchEncryptionLogs = async (page: number) => {
        try {
            console.log('Fetching logs for page:', page);
            const res = await request(
                "GET",
                `/encryption/list?page=${page}&size=5`
            );

            console.log('Fetched data:', res.data.content);
            setData(res.data.content);
            setTotalPage(res.data.totalPages);
        } catch (e: any) {
            console.log(e);
        }
    };

    // 페이지 번호 바뀔 때마다 호출
    useEffect(() => {
        fetchEncryptionLogs(page);
    }, [page]);

    return (
        <EncryptionLogsContext.Provider value={{ data, page, totalPage, setPage, fetchEncryptionLogs }}>
            {children}
        </EncryptionLogsContext.Provider>
    );
};