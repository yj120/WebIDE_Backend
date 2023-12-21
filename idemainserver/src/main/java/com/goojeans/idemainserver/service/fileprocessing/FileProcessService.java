package com.goojeans.idemainserver.service.fileprocessing;

import com.goojeans.idemainserver.domain.dto.request.FileRequests.*;
import com.goojeans.idemainserver.domain.dto.response.FileResponses.*;

public interface FileProcessService {

    public FileProcessResponse<SourceCodeResponse> findSourceCode(SourceCodeRequest request);

    public FileProcessResponse<AlgorithmResponse> findAlgoText(Long algorithmId);

    public FileProcessResponse<ExecuteResponse> executeAndSaveCode(ExecuteRequest request);

    public FileProcessResponse<SubmitResponse> submitAndSaveCode(SubmitRequest request);

    public FileProcessResponse<FileTreeResponse> modifyFileStructure(ModifyPathRequest request);

    public FileProcessResponse<FileTreeResponse> deleteFile(DeleteFileRequest request);

    public FileProcessResponse<FileTreeResponse> findFileTree(FileTreeRequest request);

    public FileProcessResponse<FileTreeResponse> createFileOrFolder(CreateFileRequest request);
}
