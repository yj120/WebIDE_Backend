package com.goojeans.idemainserver.service.fileprocessing;

import com.goojeans.idemainserver.domain.dto.request.FileRequests.*;
import com.goojeans.idemainserver.domain.dto.response.FileResponses.*;
import com.goojeans.idemainserver.domain.entity.RunCode;
import com.goojeans.idemainserver.repository.fileprocessing.FileProcessRepository;
import com.goojeans.idemainserver.util.FileExtension;
import com.goojeans.idemainserver.util.SubmitResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class FileProcessServiceTest {

    @Mock
    private FileProcessRepository repository;

    @Spy
    @InjectMocks
    private FileProcessingServiceImpl service;

    private File testFile;
    String content = "print('hello')\nprint('hi')";
    String testResult = "hello\nhi";

    @BeforeEach
    void beforeEach() {
        testFile = new File("test.py");
        if (!testFile.exists()){
            try {
                testFile.createNewFile();
                FileWriter fw = new FileWriter(testFile.getAbsolutePath());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            } catch (IOException e) {
                log.info("file error={}", e.getMessage());
            }
        }
    }

    @AfterEach
    void afterEach() {
        testFile.delete();
    }

    @DisplayName("소스 코드 찾기")
    @Test
    void findSourceCode() {
        //Given
        Long algorithmId = 1L;
        String sourceCodePath = "folder/file.py";
        Long userId = 1L;
        String fileKey = "1/1/folder/file.py";
        SourceCodeRequest request = new SourceCodeRequest(algorithmId, sourceCodePath, userId);
        SourceCodeResponse response = new SourceCodeResponse(content);

        when(repository.findFile(fileKey)).thenReturn(testFile);

        //When
        FileProcessResponse<SourceCodeResponse> sourceCode = service.findSourceCode(request);

        //Then
        assertThat(sourceCode.getData().contains(response)).isTrue();
        log.info("check data contents={}", "\n" + sourceCode.getData().get(0).getSourceCode());

    }

    @DisplayName("알고리즘 파일 찾기")
    @Test
    void findAlgoText() {
        //Given
        Long algorithmId = 1L;
        String fileKey = "1/algorithm.txt";
        AlgorithmResponse response = new AlgorithmResponse(content);

        when(repository.findFile(fileKey)).thenReturn(testFile);

        //When
        FileProcessResponse<AlgorithmResponse> algorithm = service.findAlgoText(algorithmId);

        //Then
        assertThat(algorithm.getData().get(0)).isEqualTo(response);
        log.info("check data contents={}", algorithm.getData().get(0).getAlgorithmText());

    }

    @DisplayName("실행 & 저장")
    @Test
    void executeAndSaveCode() {
        //Given
        Long algorithmId = 1L;
        String filePath = "folder/file.py";
        String testCase = "";
        Long userId = 1L;
        FileExtension extension = FileExtension.PYTHON3;
        String fileKey = "1/1/folder/file.py";

        ExecuteRequest request = ExecuteRequest.builder()
                .sourceCode(content)
                .algorithmId(algorithmId)
                .fileExtension(extension)
                .filePathSuffix(filePath)
                .userId(userId)
                .testCase(testCase)
                .build();

        when(repository.saveFile(fileKey, testFile)).thenReturn("success");
        doReturn(testFile).when(service).getFile(content);

        FileProcessResponse<ExecuteResponse> mock = new FileProcessResponse<>();
        mock.setStatus(200);
        mock.setData(List.of(new ExecuteResponse(testResult)));

        RestExecuteRequest restRequest = RestExecuteRequest.builder()
                .s3Url(fileKey)
                .extension(request.getFileExtension())
                .testCase(request.getTestCase())
                .algorithmId(algorithmId)
                .build();

        doReturn(mock).when(service).restPost("http://run:8080/execute", restRequest, ExecuteResponse.class);

        //When
        FileProcessResponse<ExecuteResponse> response = service.executeAndSaveCode(request);
        log.info("check={}", response);

        //Then
        assertThat(response.getData().get(0).getExecuteResult()).isEqualTo(testResult);
        log.info("check result={}", response.getData().get(0).getExecuteResult());

    }

    @DisplayName("수정된 파일 제출 요청")
    @Test
    void submitAndSaveCodeEdited() {
        //Given
        Long algorithmId = 1L;
        String filePath = "folder/file.py";
        Long userId = 1L;
        FileExtension extension = FileExtension.PYTHON3;
        String fileKey = "1/1/folder/file.py";
        RunCode runCode = RunCode.builder()
                .sourceUrl(fileKey)
                .build();

        SubmitRequest request = SubmitRequest.builder()
                .algorithmId(algorithmId)
                .filePathSuffix(filePath)
                .sourceCode(content)
                .edited(true)
                .userId(userId)
                .fileExtension(extension)
                .build();

        RestSubmitRequest restRequest = RestSubmitRequest.builder()
                .s3Url(fileKey)
                .algorithmId(algorithmId)
                .extension(extension)
                .build();

        FileProcessResponse<SubmitResponse> mock = new FileProcessResponse<>();
        mock.setStatus(200);
        mock.setData(List.of(new SubmitResponse(SubmitResult.CORRECT)));

        doReturn(testFile).when(service).getFile(content);
        doReturn(mock).when(service).restPost("http://run:8080/submit", restRequest, SubmitResponse.class);

        SubmitResponse response = new SubmitResponse(SubmitResult.CORRECT);

        when(repository.saveFile(fileKey, testFile)).thenReturn("success");
        when(repository.saveMetaData(any(RunCode.class))).thenReturn(runCode);

        //When
        FileProcessResponse<SubmitResponse> serviceResponse = service.submitAndSaveCode(request);

        //Then
        assertThat(serviceResponse.getData().get(0)).isEqualTo(response);

    }

    @DisplayName("새로 생성 / 바뀌지 않은 파일 제출 요청")
    @Test
    void submitAndSaveCodeNoEdited() {
        //Given
        Long algorithmId = 1L;
        String filePath = "folder/file.py";
        Long userId = 1L;
        FileExtension extension = FileExtension.PYTHON3;
        String fileKey = "1/1/folder/file.py";

        RunCode runCode = RunCode.builder()
                .sourceUrl(fileKey)
                .submitResult(SubmitResult.CORRECT)
                .build();

        SubmitRequest request = SubmitRequest.builder()
                .algorithmId(algorithmId)
                .filePathSuffix(filePath)
                .sourceCode(content)
                .edited(false)
                .userId(userId)
                .fileExtension(extension)
                .build();

        SubmitResponse response = new SubmitResponse(SubmitResult.CORRECT);
        when(repository.getMetaData(fileKey)).thenReturn(Optional.ofNullable(runCode));

        //When
        FileProcessResponse<SubmitResponse> serviceResponse = service.submitAndSaveCode(request);

        //Then
        assertThat(serviceResponse.getData().get(0)).isEqualTo(response);

    }

    @DisplayName("파일 경로 수정")
    @Test
    void modifyFileStructure() {
        //Given
        Long algorithmId = 1L;
        Long userId = 1L;
        String before = "folder/file.py";
        String after = "move/file.py";
        String beforeFileKey = "1/1/folder/file.py";
        String afterFileKey = "1/1/move/file.py";
        String prefix = "1/1";

        ModifyPathRequest request = new ModifyPathRequest(before, after, userId, algorithmId);

        when(repository.modifyFilePath(beforeFileKey, afterFileKey)).thenReturn("success");
        when(repository.findFileTrees(prefix)).thenReturn(List.of(new FileTreeResponse(afterFileKey)));

        //When
        FileProcessResponse<FileTreeResponse> response = service.modifyFileStructure(request);

        //Then
        assertThat(response.getData().contains(new FileTreeResponse("1/1/move/file.py"))).isTrue();

    }

    @DisplayName("파일 삭제")
    @Test
    void deleteFile() {
        //Given
        String deletePath = "folder/file.py";
        Long userId = 1L;
        Long algorithmId = 1L;
        String fileKey = "1/1/folder/file.py";
        String prefix = "1/1";

        DeleteFileRequest request = new DeleteFileRequest(deletePath, algorithmId, userId);
        when(repository.deleteFile(fileKey)).thenReturn("success");
        when(repository.findFileTrees(prefix)).thenReturn(null);

        //When
        FileProcessResponse<FileTreeResponse> response = service.deleteFile(request);

        //Then
        assertThat(response.getData()).isNull();

    }

    @DisplayName("파일 트리 찾기")
    @Test
    void findFileTree() {
        //Given
        Long algorithmId = 1L;
        Long userId = 1L;
        String prefix = algorithmId + "/" + userId;

        FileTreeRequest testRequest = new FileTreeRequest(algorithmId, userId);
        FileTreeResponse testResponse = new FileTreeResponse("1/1/test.py");
        when(repository.findFileTrees(prefix)).thenReturn(List.of(testResponse));

        //When
        FileProcessResponse<FileTreeResponse> result = service.findFileTree(testRequest);

        //Then
        assertThat(result.getData().contains(testResponse)).isTrue();

    }

    @DisplayName("파일 or 폴더 생성")
    @Test
    void createFileOrFolder() {
        //Given
        Long algorithmId = 1L;
        Long userId = 1L;
        String createPath = "test.py";
        String prefix = "1/1";
        String testPath = algorithmId + "/" + userId + "/" + createPath;

        CreateFileRequest testRequest = new CreateFileRequest(algorithmId, userId, createPath);
        FileTreeResponse testTree = new FileTreeResponse(createPath);

        when(repository.saveFile(eq(testPath), any(File.class))).thenReturn("success");
        when(repository.findFileTrees(prefix)).thenReturn(List.of(testTree));

        //When
        FileProcessResponse<FileTreeResponse> result = service.createFileOrFolder(testRequest);

        //Then
        assertThat(result.getData().contains(testTree)).isTrue();

    }
}