package com.goojeans.idemainserver.service.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.goojeans.idemainserver.domain.dto.request.adminRequest.AlgoCreateRequestDto;
import com.goojeans.idemainserver.domain.dto.request.adminRequest.AlgoModifyRequestDto;
import com.goojeans.idemainserver.domain.dto.response.adminResponse.AlgoShortResponseDto;
import com.goojeans.idemainserver.domain.dto.response.adminResponse.AlgoAllResponseDto;
import com.goojeans.idemainserver.domain.entity.Algorithm;
import com.goojeans.idemainserver.repository.algorithm.AlgorithmRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminAlgorithmService {

	private final AlgorithmRepository algorithmRepository;

	// TODO: test할 수 있는 것과 없는 것이 있음.
	// 제어할 수 있는 것 / 제어할 수 없는 것
	// 제어할 수 없는 것: random 메서드, 외부 API 호출 (non testable method), 시간에 따라 결과가 달라지는 것
	// method를 조금 수정하면 non-testable method를 testable method로 바꿀 수 있음.
	// random: random 생성을 외부에서 생성하고, 파라미터로 넣어주는 형식으로 바꾼다면, 테스트 가능. 프로그램 전체에서는 random 유지, testable까지!!!
	// 외부 API 호출  -> 눈으로 확인하는 테스트를 짜고 평소에는 @Disabled로 끄고, 필요할 때만 테스트를 켜서 돌린다.

	public AlgoAllResponseDto save(AlgoCreateRequestDto requestDto) {

		// TODO: List<MemberSolved> memberSolved = new ArrayList<>(); 이렇게 넣어줘야 하나?
		Algorithm algorithmEntity = Algorithm.builder()
			.algorithmName(requestDto.getAlgorithmName())
			.tag(requestDto.getTag())
			.level(requestDto.getLevel())
			.build();
		Algorithm savedAlgorithm = algorithmRepository.saveAlgorithm(algorithmEntity);

		// TODO: description, testcases, answers - S3에 저장



		return AlgoAllResponseDto.from(savedAlgorithm, requestDto.getDescription(), requestDto.getTestcases(),
			requestDto.getAnswers());
	}

	public AlgoAllResponseDto update(Long id, AlgoModifyRequestDto requestDto) {

		// Algorithm entity를 id로 찾아옴.
		Algorithm algorithmEntity = algorithmRepository.findAlgorithmById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 알고리즘 문제가 존재하지 않습니다."));

		// Algorithm entity에 name, tag, level 수정.
		Algorithm updateAlgorithm = algorithmEntity.updateAlgorithm(
			requestDto.getAlgorithmName(),
			requestDto.getTag(),
			requestDto.getLevel()
		);

		// TODO description, testcases, answers - S3에 저장
		// Link로 조립하고 AWS S3에서 받아오는데.... File 로 받아와서 String으로 변환 -> List<String>으로 변환

		return AlgoAllResponseDto.from(updateAlgorithm, requestDto.getDescription(), requestDto.getTestcases(),
			requestDto.getAnswers());
	}

	public boolean delete(Long id) {

		try {
			algorithmRepository.deleteById(id);
			return true;
		} catch (IllegalArgumentException e) {
			log.error("해당 알고리즘 문제가 존재하지 않습니다.");
			log.error(e.getMessage());
			return false;
		}
	}

	public List<AlgoShortResponseDto> findAll() {
		return algorithmRepository.findAllAlgorithm()
			.stream()
			.map(AlgoShortResponseDto::from)
			.toList();
	}

}