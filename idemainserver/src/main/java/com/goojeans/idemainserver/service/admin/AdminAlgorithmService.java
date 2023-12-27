package com.goojeans.idemainserver.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.goojeans.idemainserver.domain.dto.request.adminrequest.AlgoCreateRequestDto;
import com.goojeans.idemainserver.domain.dto.request.adminrequest.AlgoModifyRequestDto;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.AlgoShortResponseDto;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.AlgoAllResponseDto;
import com.goojeans.idemainserver.domain.dto.response.adminresponse.LanguageCountDto;
import com.goojeans.idemainserver.domain.entity.Algorithm;
import com.goojeans.idemainserver.repository.algorithm.S3Repository;
import com.goojeans.idemainserver.repository.algorithm.AlgorithmRepository;
import com.goojeans.idemainserver.repository.membersolved.MemberSolvedRepository;
import com.goojeans.idemainserver.util.Language;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminAlgorithmService {

	private final AlgorithmRepository algorithmRepository;
	private final MemberSolvedRepository memberSolvedRepository;
	private final S3Repository s3Repository;

	public List<LanguageCountDto> getLanguageSolvedCounts() {

		List<Object[]> results = memberSolvedRepository.countSolvedByLanguage();

		int total = 0;
		int java = 0;
		int python3 = 0;
		int cpp = 0;

		for (Object[] result : results) {
			String language = result[0].toString();
			int count = ((Long)result[1]).intValue();
			if (language.equals(Language.Java.toString())) {
				java = count;
			} else if (language.equals(Language.Python.toString())) {
				python3 = count;
			} else if (language.equals(Language.Cpp.toString())) {
				cpp = count;
			}

			total += count;
		}

		return List.of(LanguageCountDto.of(java, python3, cpp, total));
	}

	public List<AlgoAllResponseDto> save(AlgoCreateRequestDto requestDto) {

		// DB - name, tag, level 저장.
		Algorithm algorithmEntity = Algorithm.builder()
			.algorithmName(requestDto.getAlgorithmName())
			.tag(requestDto.getTag())
			.level(requestDto.getLevel())
			.build();
		Algorithm savedAlgorithm = algorithmRepository.saveAlgorithm(algorithmEntity);

		// S3에 저장 - description, testcases, answers -
		Long algorithmId = algorithmEntity.getAlgorithmId();

		String description = requestDto.getDescription();
		String descriptionPath = algorithmId + "/algorithm.txt";
		s3Repository.uploadString(descriptionPath, description);
log.info("descriptionPath: {}", descriptionPath);
		List<String> testcases = requestDto.getTestcases();
		List<String> answers = requestDto.getAnswers();
		for (int i = 1; i <= testcases.size(); i++) {
			String testcasePath = algorithmId + "/testcases/testcase" + (i) + ".txt";
			s3Repository.uploadString(testcasePath, testcases.get(i-1));

			String answerPath = algorithmId + "/answers/answer" + (i) + ".txt";
			s3Repository.uploadString(answerPath, answers.get(i-1));

		}

		return List.of(AlgoAllResponseDto.from(savedAlgorithm, requestDto.getDescription(), requestDto.getTestcases(),
			requestDto.getAnswers()));
	}

	public List<AlgoAllResponseDto> update(Long id, AlgoModifyRequestDto requestDto) {

		// DB - name, tag, level 수정.
		Algorithm algorithmEntity = algorithmRepository.findAlgorithmById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 알고리즘 문제가 존재하지 않습니다."));
		Algorithm updateAlgorithm = algorithmEntity.updateAlgorithm(
			requestDto.getAlgorithmName(),
			requestDto.getTag(),
			requestDto.getLevel()
		);

		// S3 - update(==저장) - description, testcases, answers
		Long algorithmId = algorithmEntity.getAlgorithmId();

		String description = requestDto.getDescription();
		String descriptionPath = algorithmId + "/algorithm.txt";
		s3Repository.uploadString(descriptionPath, description);

		List<String> testcases = requestDto.getTestcases();
		List<String> answers = requestDto.getAnswers();
		int originSize = s3Repository.getObjectsAsStringList(algorithmId + "/testcases").size();
		int newSize = testcases.size();
		for (int i = 1; i <= newSize; i++) {
			String testcasePath = algorithmId + "/testcases/testcase" + (i) + ".txt";
			s3Repository.uploadString(testcasePath, testcases.get(i-1));
			String answerPath = algorithmId + "/answers/answer" + (i) + ".txt";
			s3Repository.uploadString(answerPath, answers.get(i-1));
		}
		if (newSize < originSize) {

			for (int i = newSize+1; i <= originSize; i++) {
				String testcasePath = algorithmId + "/testcases/testcase" + (i) + ".txt";
				s3Repository.deleteFileFromS3(testcasePath);
				String answerPath = algorithmId + "/answers/answer" + (i) + ".txt";
				s3Repository.deleteFileFromS3(answerPath);
			}

		}

		return List.of(AlgoAllResponseDto.from(updateAlgorithm, requestDto.getDescription(), requestDto.getTestcases(),
			requestDto.getAnswers()));
	}

	public void deleteById(Long id) {

		try {

			// DB에서 삭제
			algorithmRepository.deleteById(id);

			// S3에서 삭제
			s3Repository.deleteAlgosByAlgoId(id);

		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			throw new IllegalArgumentException(e);
		}
	}

	/*
	 * DB에 있는 정보만 내려 주기
	 */
	public List<AlgoShortResponseDto> findAll() {
		return algorithmRepository.findAllAlgorithm()
			.stream()
			.map(AlgoShortResponseDto::from)
			.toList();
	}

	public List<AlgoAllResponseDto> findById(Long algorithmId) {

		// DB에서 찾기
		Algorithm algorithmEntity = algorithmRepository.findAlgorithmById(algorithmId)
			.orElseThrow(() -> new IllegalArgumentException("해당 알고리즘 문제가 존재하지 않습니다."));

		// description, testcases, answers - S3에서 받아오기
		String descriptionPath = algorithmId + "/algorithm.txt";
		String description = s3Repository.getObjectAsString(descriptionPath);

		List<String> testcases = s3Repository.getObjectsAsStringList(algorithmId + "/testcases");

		List<String> answers = s3Repository.getObjectsAsStringList(algorithmId + "/answers");

		return List.of(AlgoAllResponseDto.from(algorithmEntity,
			description, testcases, answers));

	}
}