package com.goojeans.idemainserver.controller;

import com.goojeans.idemainserver.domain.dto.request.FileRequests.*;
import com.goojeans.idemainserver.domain.dto.response.FileResponses.*;
import com.goojeans.idemainserver.service.fileprocessing.FileProcessService;
import com.goojeans.idemainserver.util.TokenAndLogin.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/editor")
public class FileProcessingController {

    private final FileProcessService service;
    private final JwtService jwtService;

    @PostMapping("/sourcecode")
    public FileProcessResponse<SourceCodeResponse> findSourceCode(@Validated @RequestBody SourceCodeRequest sourceRequest,
                                                                  HttpServletRequest request) {
        Optional<String> jwtToken = jwtService.extractAccessToken(request);
        String token = jwtToken.orElse("not valid value");

        Map<String, String> decode = jwtService.decode(token);

        String id = decode.get("id");
        log.info("check id={}", id);
        sourceRequest.setUserId(Long.parseLong(id));

        return service.findSourceCode(sourceRequest);
    }

    @GetMapping("/algorithm/{algorithmId}")
    public FileProcessResponse<AlgorithmResponse> findAlgoText(@Validated @PathVariable Long algorithmId) {
        return service.findAlgoText(algorithmId);
    }

    @PostMapping("/delete")
    public FileProcessResponse<FileTreeResponse> deleteFile(@Validated @RequestBody DeleteFileRequest deleteRequest,
                                                            HttpServletRequest request) {
        Optional<String> jwtToken = jwtService.extractAccessToken(request);
        String token = jwtToken.orElse("not valid value");

        Map<String, String> decode = jwtService.decode(token);

        String id = decode.get("id");
        log.info("check id={}", id);
        deleteRequest.setUserId(Long.parseLong(id));

        return service.deleteFile(deleteRequest);
    }

    @PatchMapping("/modification")
    public FileProcessResponse<FileTreeResponse> modifyFilePath(@Validated @RequestBody ModifyPathRequest modifyRequest,
                                                                HttpServletRequest request) {
        Optional<String> jwtToken = jwtService.extractAccessToken(request);
        String token = jwtToken.orElse("not valid value");

        Map<String, String> decode = jwtService.decode(token);

        String id = decode.get("id");
        log.info("check id={}", id);
        modifyRequest.setUserId(Long.parseLong(id));

        return service.modifyFileStructure(modifyRequest);
    }

    @PostMapping("/execute")
    public FileProcessResponse<ExecuteResponse> executeCode(@Validated @RequestBody ExecuteRequest executeRequest,
                                                            HttpServletRequest request) {
        Optional<String> jwtToken = jwtService.extractAccessToken(request);
        String token = jwtToken.orElse("not valid value");

        Map<String, String> decode = jwtService.decode(token);

        String id = decode.get("id");

        executeRequest.setUserId(Long.parseLong(id));
        log.info("source code = {}", executeRequest.getSourceCode());
        return service.executeAndSaveCode(executeRequest);
    }

    @PostMapping("/submit")
    public FileProcessResponse<SubmitResponse> submitCode(@Validated @RequestBody SubmitRequest submitRequest,
                                                          HttpServletRequest request) {

        Optional<String> jwtToken = jwtService.extractAccessToken(request);
        String token = jwtToken.orElse("not valid value");

        Map<String, String> decode = jwtService.decode(token);

        String id = decode.get("id");

        submitRequest.setUserId(Long.parseLong(id));
        log.info("source code = {}", submitRequest.getSourceCode());

        return service.submitAndSaveCode(submitRequest);
    }

    @GetMapping("/filetrees/{algorithmId}")
    public FileProcessResponse<FileTreeResponse> getAllFileTree(@PathVariable Long algorithmId,
                                                                HttpServletRequest request) {
        Optional<String> jwtToken = jwtService.extractAccessToken(request);
        String token = jwtToken.orElse("not valid value");

        Map<String, String> decode = jwtService.decode(token);

        String id = decode.get("id");

        return service.findFileTree(new FileTreeRequest(algorithmId, Long.parseLong(id)));
    }


    @PostMapping("/filecreate")
    public FileProcessResponse<FileTreeResponse> createFileFolder(@Validated @RequestBody CreateFileRequest fileRequest,
                                                                  HttpServletRequest request) {

        Optional<String> jwtToken = jwtService.extractAccessToken(request);
        String token = jwtToken.orElse("not valid value");

        Map<String, String> decode = jwtService.decode(token);

        String id = decode.get("id");

        fileRequest.setUserId(Long.parseLong(id));

        return service.createFileOrFolder(fileRequest);
    }

}
