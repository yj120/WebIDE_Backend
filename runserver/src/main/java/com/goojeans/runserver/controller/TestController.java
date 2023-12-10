package com.goojeans.runserver.controller;

import com.goojeans.runserver.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@Slf4j
@RestController
public class TestController {

    @GetMapping("/python")
    public ResponseDto helloWorldPy() {
        String pythonCode = "print('hello python')";
        String filePath = "example.py"; // 파일 경로와 이름 지정

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(pythonCode);
            System.out.println("Python file created successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred.");
        }
        StringBuilder answer = getStringBuilder("python3", filePath);
        return new ResponseDto(answer.toString());
    }

    @GetMapping("/cpp")
    public String helloWorldCpp() {
        String cppCode =
                "#include <iostream>\n" +
                        "using namespace std;\n" +
                        "int main(){\n" +
                        "    cout << \"hello cpp\";\n" +
                        "    return 0;\n" +
                        "}";
        String cppFilePath = "example.cpp";
        String executableFilePath = "./example"; // 실행 파일 이름

        // C++ 소스 파일 생성
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cppFilePath))) {
            writer.write(cppCode);
            System.out.println("Cpp file created successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
            return "Error in creating cpp file";
        }

        // C++ 파일 컴파일
        StringBuilder compileOutput = getStringBuilder("g++", cppFilePath, "-o", executableFilePath);
        if (compileOutput.toString().contains("error")) {
            return "Error in compiling cpp file";
        }

        // 컴파일된 실행 파일 실행
        StringBuilder executionOutput = getStringBuilder(executableFilePath);

        // 파일 삭제
        new File(cppFilePath).delete();
        new File(executableFilePath).delete();

        return executionOutput.toString();
    }

    private static StringBuilder getStringBuilder(String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        StringBuilder answer = new StringBuilder();
        try{
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // 실행 결과 처리
                log.info("line output={}", line);
                answer.append(line);
            }
        } catch (IOException e){
            log.error(e.getMessage());
            answer.delete(0, answer.length());
            answer.append("error");
        }
        return answer;
    }

}
