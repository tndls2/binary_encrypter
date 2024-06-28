import React from 'react';
import { RiArrowDropLeftFill, RiArrowDropRightFill } from "react-icons/ri";

import {useEncryptionLogs} from "../../../context/EncryptionLogsContext";

import "./Pagination.css";


interface Props {
    page: number;
    setPage: React.Dispatch<React.SetStateAction<number>>;
    totalPage: number;
}

/**
 * Pagination 컴포넌트
 * 참조 : https://ts2ree.tistory.com/
 * 참조 : https://velog.io/@jiwon_17/React%ED%8E%98%EC%9D%B4%EC%A7%80-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0
 */
export default function Pagination() {
    const {page, setPage, totalPage} = useEncryptionLogs();

    /**
     * 이전 페이지 핸들러
     */
    const handlePrevPage = () => {
        if (page === 0) {
            window.alert("첫 페이지 입니다.");
            return;
        } else {
            setPage((prev) => prev - 1);
        }
    };

    /**
     * 다음 페이지 핸들러
     */
    const handleNextPage = () => {
        if (page + 1 === totalPage) {
            window.alert("마지막 페이지입니다.");
            return;
        } else {
            setPage((prev) => prev + 1);
        }
    };

    return (
        <div className="pagination">
            <RiArrowDropLeftFill
                color={page === 0 ? "#e0e0e0" : "#15A2F3"}  // 첫 페이지인 경우 아이콘 색상 변경
                onClick={handlePrevPage}
            />
            <div>
                {page + 1} / {totalPage}
            </div>
            <RiArrowDropRightFill
                color={page + 1 === totalPage ? "#e0e0e0" : "#15A2F3"} // 마지막 페이지인 경우 아이콘 색상 변경
                onClick={handleNextPage}
            />
        </div>
    );
}
