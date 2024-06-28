import React from "react";
import { GrDownload } from "react-icons/gr";

import { requestFile } from "../../../utils/axios";
import {EncryptionLog} from "../../../types/encryptionLog";
import { useEncryptionLogs } from "../../../context/EncryptionLogsContext"

import "./EncryptionLogTable.css";


interface Props {
    data: EncryptionLog[];
    page: number;
}

/**
 * 암호화 이력 조회 및 파일 다운로드 컴포넌트
 */
export default function EncryptionLogTable() {
    const {data, page} = useEncryptionLogs();

    /**
     * 파일 다운로드
     * @param fileName 다운로드할 파일명
     */
    const fileDownload = async (fileName: string) => {
        try {
            const res = await requestFile(
                "GET",
                `/file/${fileName}/download`,
                fileName
            );
            console.log(res);
        } catch (e) {
            console.log(e);
        }
    };

    return (
        <table className="file-table">
            <thead>
                <tr>
                    <th>No.</th>
                    <th>암호화 대상 파일</th>
                    <th>암호화 된 파일</th>
                    <th>IV 값</th>
                    <th>일시</th>
                </tr>
            </thead>
            <tbody>
                {data.map((item, idx) => (
                    <tr>
                        <td>{page * 5 + idx + 1}</td>
                        <td>
                            <div className="download-td">
                                <span>{item.originFile}</span>
                                <GrDownload
                                    onClick={() =>
                                        fileDownload(item.originFile)
                                    }
                                />
                            </div>
                        </td>
                        <td>
                            <div className="download-td">
                                <span>{item.encryptedFile}</span>
                                <GrDownload
                                    onClick={() =>
                                        fileDownload(item.encryptedFile)
                                    }
                                />
                            </div>
                        </td>
                        <td>{item.iv}</td>
                        <td>{item.createdAt}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
}
