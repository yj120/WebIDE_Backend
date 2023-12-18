package com.goojeans.idemainserver.repository.fileprocessing;

import com.goojeans.idemainserver.domain.entity.RunCode;
import com.goojeans.idemainserver.util.SolvedStatus;

import java.io.File;

public interface FileProcessRepository {

    public File findSourceCode(String filePath);

    public String saveFile(String filePath, File sourceCode);

    public String deleteFile(String filePath);

    public String modifyFilePath(String beforeFilePath, String afterFilePath);

    public RunCode saveMetaData(RunCode runCode);

    public RunCode getMetaData(String filePath);

}
