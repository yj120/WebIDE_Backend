package com.goojeans.idemainserver.repository.algorithm;

import java.util.List;

public interface S3Repository {

	public void uploadString(String path, String content);

	public String getObjectAsString( String objectKey);
	public List<String> getObjectsAsStringList(String algorithmId);
	public boolean deleteAlgosByAlgoId(Long algorithmId);

	public boolean deleteAlgosByUserId(Long userId);
	public void deleteFileFromS3(String path) ;

}
