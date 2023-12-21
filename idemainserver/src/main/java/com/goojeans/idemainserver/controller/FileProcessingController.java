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

    //TODO: get all file trees
/*    @GetMapping("/filetrees/{algorithmId}")
    public */

    @PostMapping("/delete")
    public FileProcessResponse<MessageResponse> deleteFile(@Validated @RequestBody DeleteFileRequest deleteRequest) {
        //TODO: get userId from jwt token
        Long tempUserId = 1L;
        deleteRequest.setUserId(tempUserId);

        return service.deleteFile(deleteRequest);
    }

    @PatchMapping("/modification")
    public FileProcessResponse<MessageResponse> modifyFilePath(@Validated @RequestBody ModifyPathRequest modifyRequest) {
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
}
