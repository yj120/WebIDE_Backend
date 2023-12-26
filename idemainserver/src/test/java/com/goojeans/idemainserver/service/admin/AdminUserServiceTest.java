package com.goojeans.idemainserver.service.admin;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.goojeans.idemainserver.domain.dto.response.adminresponse.UserResponseDto;
import com.goojeans.idemainserver.domain.entity.Users.User;
import com.goojeans.idemainserver.repository.Users.UserRepository;
import com.goojeans.idemainserver.util.TokenAndLogin.Role;
import com.goojeans.idemainserver.util.TokenAndLogin.SocialType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

	@Mock
	private UserRepository userRepository;

	// @Spy
	@InjectMocks
	// @Mock
	private AdminUserService service;

	private User fakeUser;
	String email = "asd@naver.com"; // 이메일
	String password = "dkdkdkdkdk123"; // 비밀번호
	String nickname = "nickname"; // 닉네임
	String imageUrl = "ddd.com"; // 프로필 이미지
	String bio = "sss.com"; // 블로그 주소
	String city = "sudo"; // 사는 도시
	boolean terms = true; // 약관 동의 여부
	Role IsAdmin = Role.USER;
	SocialType socialType = SocialType.GOOGLE; // KAKAO, NAVER, GOOGLE
	String socialId = "123";
	String refreshToken = "123";

	@BeforeEach
	void setUp() {

		fakeUser = User.builder()
			.id(1L)
			.email(email)
			.password(password)
			.nickname(nickname)
			.imageUrl(imageUrl)
			.bio(bio)
			.city(city)
			.terms(terms)
			.IsAdmin(IsAdmin)
			.socialType(socialType)
			.socialId(socialId)
			.refreshToken(refreshToken)
			.build();

	}

	@AfterEach
	void afterEach() {
		reset(userRepository);
	}

	@Test
	@DisplayName("유저 정보 조회")
	void getUser() {

		// given
		// TODO when then 말고 do return으로 테스트해 보기! side effect 적은 걸로.
		assertThat(fakeUser.getId()).isEqualTo(1L);
		when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(fakeUser));
		// service는...... 대상이.... 아냐.............................. => 의존 주입 받아야 하는 것과 아닌 것(내가 테스트하고 싶은 대상!!!!! )을 구분해야 한다. -> 테스트할 건 mocking 하면 ^^ 안 된다^^^^^^^^^^. 딱 서비스 불러오고 어쩌구 비굫고 어쩌구 assert로 끝내야 한다.
		// service는................... test.......eotkd.......
		// service에 @Mock을 사용하지 않아서 이러면 안 됐다~ -> Mock이 Mcok을 의존할 필요 XXXXXXXXXXXX (case by case - method 호출을 기록하는 어쩌구면 ... .ㅇ....)
		// when(service.getUser(1L)).thenReturn(UserResponseDto.of(fakeUser));

		// when
		UserResponseDto user = service.getUser(fakeUser.getId());

		// then
		assertThat(fakeUser.getEmail()).isEqualTo(user.getEmail());

	}

	@Test
	@DisplayName("모든 유저 정보 조회")
	void getAllUsers() {

		// repository에서 Entity 불러와 Dto를 생성하는 로직
		// repository가 안 짜여 있어도 된다..? => repository는 다 안 짜여 있어도 됨 ㅇㅇ 다만 어떤 게 반환될지는 알아야 한다! 인풋-아웃풋은 설계된 상태에서 ㅇㅇ repository에서 어떻게 모킹하는지 알 수 있으니까! 그렇게 가정하고 테스트를 진행한다.

		// given
		when(userRepository.findAll()).thenReturn(List.of(fakeUser));

		// when
		// service의 인터페이스(빈 함수)는 만들어 놔야 함.
		List<UserResponseDto> allUsers = service.getAllUsers();

		// then - 몇 명.. 오......오.............오..............
		// then - size, dto 변환 로직 확인. (repository에서 entity를 불러와 dto로 변환하는 로직)
		// 내가 뭘 하고자 했는지를 명확하게 규명할 수 있어야 함! 행동! 여기서 어떤 일들이 일어나는지 전부!!! + 어떤 일이 일어나서는 안 되는지!!!!!!!!!!!!!!!!!
		// 정형화된 검증: list -> size, delete -> 호출 여부 ?, dto 변환 로직, 동일성 여부 (get)
		assertThat(allUsers.size()).isEqualTo(1);
		assertThat(allUsers.get(0).getEmail()).isEqualTo(fakeUser.getEmail());

	}

	@Test
	@DisplayName("유저 삭제")
	void deleteUser() {

		// DB에서 실제 데이터가 삭제되는지는 궁금 X
		// service 로직이 잘 수행되는지가 궁금!
		// 길거나 제어문 등 있어서 ex 터질 가능성이 있거나 어쩌구면
		// repository의 delete가 호출은 되어야 함(이건 설계의 영역)
		// mock을 사용해서 repository의 delete가 호출되는지 확인하면 된다.
		// -> db에서 실제 삭제됐는지는 관심사가 아니기 때문에 실제 호출됐는지가 중요한 것.
		// repository의 delete가 void를 반환하기 때문에 repository mocking을 안 해도 되지 않나 하는 추측 by 범석

		// when
		service.deleteUser(fakeUser.getId());

		// then
		verify(userRepository, times(1)).deleteById(1L);

	}
}