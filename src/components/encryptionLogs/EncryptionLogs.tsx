import EncrptionLogTable from "./table/EncryptionLogTable";
import Pagination from "./pagination/Pagination";

import { useEncryptionLogs } from "../../context/EncryptionLogsContext";

/**
 * 암호화 이력 조회 및 파일 다운로드 컴포넌트
 */
export default function EncryptionLogList() {
    const { totalPage } = useEncryptionLogs();  // 암호화 이력 조회 컴포넌트

    return (
        <div>
            <EncrptionLogTable />
            {totalPage !== 0 && <Pagination />}
        </div>
    );
}
