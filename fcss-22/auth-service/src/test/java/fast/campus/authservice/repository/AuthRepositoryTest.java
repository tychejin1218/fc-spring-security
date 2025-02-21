package fast.campus.authservice.repository;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

import fast.campus.authservice.domain.user.User;
import fast.campus.authservice.entity.otp.OtpEntity;
import fast.campus.authservice.entity.user.UserEntity;
import fast.campus.authservice.repository.auth.AuthRepository;
import fast.campus.authservice.repository.otp.OtpJpaRepository;
import fast.campus.authservice.repository.user.UserJpaRepository;
import fast.campus.authservice.util.OtpCodeUtil;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;

/**
 * AuthRepositoryTest는 AuthRepository 클래스의 동작을 테스트하는 단위 테스트 클래스
 *
 * <p>Mockito를 사용하여 UserJpaRepository와 OtpJpaRepository에 대한 Mock 객체를 생성하고, 트랜잭션 동작 없이 비즈니스 로직 동작을
 * 검증합니다.</p>
 */
@ExtendWith(MockitoExtension.class)
class AuthRepositoryTest {

  @Mock
  OtpJpaRepository otpJpaRepository;

  @Mock
  UserJpaRepository userJpaRepository;

  AuthRepository sut;

  /**
   * 각 테스트 실행 전 SUT(System Under Test)를 설정
   */
  @BeforeEach
  public void setup() {
    sut = new AuthRepository(
        otpJpaRepository,
        userJpaRepository,
        TransactionOperations.withoutTransaction(), // 읽기 트랜잭션 모의
        TransactionOperations.withoutTransaction()  // 쓰기 트랜잭션 모의
    );
  }

  @Test
  @DisplayName("동일한 사용자 ID 로 사용자를 등록할 수 없다.")
  public void test1() {

    // given
    String userId = "danny.kim";
    UserEntity userEntity = new UserEntity(
        userId, "password"
    );

    // Mock 리포지토리에서 이미 같은 ID를 가진 사용자가 존재하도록 설정
    given(userJpaRepository.findUserEntityByUserId(userId))
        .willReturn(Optional.of(userEntity));

    // when & then
    Assertions.assertThrows(RuntimeException.class, () -> {
      sut.createNewUser(userEntity.toDomain());
    });
  }

  @Test
  @DisplayName("사용자를 등록할 수 있다.")
  public void test2() {

    // given
    String userId = "danny.kim";
    String password = "1234";

    // Mock 리포지토리에서 사용자가 존재하지 않는 경우를 설정
    given(userJpaRepository.findUserEntityByUserId(userId))
        .willReturn(Optional.empty());

    // 등록되는 사용자 객체 정의 및 Mock 동작 설정
    User user = new User(userId, password);
    given(userJpaRepository.save(any()))
        .willReturn(user.toEntity());

    // when
    User result = sut.createNewUser(user);

    // then
    verify(userJpaRepository, atMostOnce()).save(user.toEntity());
    Assertions.assertEquals(result.getUserId(), userId);
    Assertions.assertEquals(result.getPassword(), password);
  }

  @Test
  @DisplayName("사용자가 존재하면 OTP 값을 업데이트 한다.")
  public void test3() {

    // given
    String userId = "danny.kim";
    String otp = OtpCodeUtil.generateOtpCode();

    // Mock 리포지토리에서 사용자에 해당하는 OTP 값을 찾을 수 있도록 설정
    OtpEntity otpEntity = new OtpEntity();
    given(otpJpaRepository.findOtpEntityByUserId(userId))
        .willReturn(Optional.of(otpEntity));

    // when
    sut.upsertOtp(userId, otp);

    // then
    Assertions.assertEquals(otp, otpEntity.getOtpCode());
  }

  @Test
  @DisplayName("사용자가 존재하지 않으면 새로운 OTP 를 생성한다.")
  public void test4() {

    // given
    String userId = "danny.kim";
    String otp = OtpCodeUtil.generateOtpCode();

    // Mock 리포지토리에서 사용자에 해당하는 OTP 값이 존재하지 않는 경우를 설정
    given(otpJpaRepository.findOtpEntityByUserId(userId))
        .willReturn(Optional.empty());

    // when
    sut.upsertOtp(userId, otp);

    // then
    verify(otpJpaRepository, atMostOnce()).save(any());
  }
}
