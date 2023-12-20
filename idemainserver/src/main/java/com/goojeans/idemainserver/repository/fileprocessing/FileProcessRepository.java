package com.goojeans.idemainserver.repository.fileprocessing;

import com.goojeans.idemainserver.domain.entity.Algorithm;
import com.goojeans.idemainserver.domain.entity.RunCode;

import java.io.File;
import java.util.Optional;

public interface FileProcessRepository {

    public File findFile(String filePath);

    public String saveFile(String filePath, File sourceCode);

    public String deleteFile(String filePath);

    public String modifyFilePath(String beforeFilePath, String afterFilePath);

    public RunCode saveMetaData(RunCode runCode);

    public RunCode getMetaData(String filePath);

    public Optional<Algorithm> findAlgorithmById(Long id);

}
