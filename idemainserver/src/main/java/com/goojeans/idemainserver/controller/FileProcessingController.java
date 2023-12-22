package com.goojeans.idemainserver.controller;

import com.goojeans.idemainserver.domain.dto.request.FileRequests.*;
import com.goojeans.idemainserver.domain.dto.response.FileResponses.*;
import com.goojeans.idemainserver.service.fileprocessing.FileProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/editor")
public class FileProcessingController {

    private final FileProcessService service;

    @PostMapping("/sourcecode")
    public FileProcessResponse<SourceCodeResponse> findSourceCode(@Validated @RequestBody SourceCodeRequest sourceRequest) {
        //TODO: get userId from jwt token
        Long tempUserId = 1L;

        sourceRequest.setUserId(tempUserId);
        return service.findSourceCode(sourceRequest);
    }

    @GetMapping("/algorithm/{algorithmId}")
    public FileProcessResponse<AlgorithmResponse> findAlgoText(@Validated @PathVariable Long algorithmId) {
        return service.findAlgoText(algorithmId);
    }

    @PostMapping("/delete")
    public FileProcessResponse<FileTreeResponse> deleteFile(@Validated @RequestBody DeleteFileRequest deleteRequest) {
        //TODO: get userId from jwt token
        Long tempUserId = 1L;
        deleteRequest.setUserId(tempUserId);

        return service.deleteFile(deleteRequest);
    }

    @PatchMapping("/modification")
    public FileProcessResponse<FileTreeResponse> modifyFilePath(@Validated @RequestBody ModifyPathRequest modifyRequest) {
        //TODO: get userId from jwt token
        Long tempUserId = 1L;
        modifyRequest.setUserId(tempUserId);

        return service.modifyFileStructure(modifyRequest);
    }

    @PostMapping("/execute")
    public FileProcessResponse<ExecuteResponse> executeCode(@Validated @RequestBody ExecuteRequest executeRequest) {
        //TODO: get userId from jwt token
        Long tempUserId = 1L;
        executeRequest.setUserId(tempUserId);

        return service.executeAndSaveCode(executeRequest);
    }

    @PostMapping("/submit")
    public FileProcessResponse<SubmitResponse> submitCode(@Validated @RequestBody SubmitRequest submitRequest) {
        //TODO: get userId from jwt token
        Long tempUserId = 1L;
        submitRequest.setUserId(tempUserId);

        return service.submitAndSaveCode(submitRequest);
    }

    @GetMapping("/filetrees/{algorithmId}")
    public FileProcessResponse<FileTreeResponse> getAllFileTree(@PathVariable Long algorithmId) {
        //TODO: get userId from jwt token
        Long tempUserId = 1L;

        return service.findFileTree(new FileTreeRequest(algorithmId, tempUserId));
    }


    @PostMapping("/filecreate")
    public FileProcessResponse<FileTreeResponse> createFileFolder(@Validated @RequestBody CreateFileRequest request) {
        //TODO: get userId from jwt token
        Long tempUserId = 1L;

        request.setUserId(tempUserId);

        return service.createFileOrFolder(request);
    }

}
