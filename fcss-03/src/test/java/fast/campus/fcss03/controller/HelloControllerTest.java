package fast.campus.fcss03.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
class HelloControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @DisplayName("성공: 올바른 Basic Auth 인증 정보로 API 요청 테스트")
  @Test
  void testHelloApiWithCorrectBasicAuth() throws Exception {

    // Given: 올바른 사용자 정보와 Base64 인코딩
    String username = "danny.kim";
    String password = "12345";
    String basicAuthHeader = createBasicAuthHeader(username, password);

    // When: GET 요청을 실행
    ResultActions resultActions = mockMvc.perform(
        get("/api/v1/hello")
            .header("Authorization", basicAuthHeader)
            .accept("application/json")
    );

    // Then: 상태 200 OK와 expected content를 확인
    resultActions.andExpect(status().isOk())
        .andExpect(content().string("Hello, Spring Security"));
  }

  @DisplayName("실패: 잘못된 Basic Auth 인증 정보로 API 요청 테스트")
  @Test
  void testHelloApiWithIncorrectPassword() throws Exception {

    // Given: 잘못된 사용자 정보와 Base64 인코딩
    String username = "danny.kim";
    String incorrectPassword = "wrong_password";
    String basicAuthHeader = createBasicAuthHeader(username, incorrectPassword);

    // When: GET 요청을 실행
    ResultActions resultActions = mockMvc.perform(
        get("/api/v1/hello")
            .header("Authorization", basicAuthHeader)
            .accept("application/json"));

    // Then: 상태 401 Unauthorized 확인
    resultActions.andExpect(status().isUnauthorized());
  }

  /**
   * Basic Authentication 헤더를 생성하는 유틸리티 메서드.
   *
   * @param username 사용자 이름
   * @param password 비밀번호
   * @return Basic Auth 헤더 값 (Base64로 인코딩된 username:password 형태)
   */
  private String createBasicAuthHeader(String username, String password) {
    String credentials = username + ":" + password;
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
  }
}
