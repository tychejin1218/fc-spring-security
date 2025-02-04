package fast.campus.fcss07.controller;

import fast.campus.fcss07.controller.request.UserRegisterRequestBody;
import fast.campus.fcss07.controller.response.ResultResponse;
import fast.campus.fcss07.domain.user.CreateUser;
import fast.campus.fcss07.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  /**
   * 사용자 등록 API
   * <p>
   * 클라이언트로부터 사용자 데이터를 받아 사용자 등록을 처리
   * </p>
   *
   * @param requestBody 사용자 등록 요청 데이터를 담은 {@link UserRegisterRequestBody} 객체
   * @return 성공 메시지를 반환하는 {@link ResultResponse} 객체
   */
  @PostMapping("/api/v1/register")
  public ResultResponse<String> register(@RequestBody UserRegisterRequestBody requestBody) {
    String result = userService.register(
        new CreateUser(requestBody.getUsername(),
            bCryptPasswordEncoder.encode(requestBody.getPassword())));
    return ResultResponse.ok(result);
  }
}
