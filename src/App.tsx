import BinaryFileUpload from "./components/binaryFileUpload/BinaryFileUpload";
import EncryptionLogs from "./components/encryptionLogs/EncryptionLogs";

import { EncryptionLogsProvider } from "./context/EncryptionLogsContext"

import "./App.css";

export default function App() {
    return (
        <div className="App">
            <EncryptionLogsProvider>
                <BinaryFileUpload /> {/* 파일 업로드 컴포넌트 */}
                <EncryptionLogs />  {/* 암호화 이력 조회 및 파일 다운로드 컴포넌트 */}
            </EncryptionLogsProvider>
        </div>
    );
}
