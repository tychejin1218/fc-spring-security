스프링 시큐리티 아키텍쳐의 필터 체인

HTTP 필터
스프링 시큐리티에서의 HTTP 필터는 HTTP 요청에 적용되는 다양한 책임을 위임
- 필터는 체인 형식으로 동작함 (요청을 받아 실행하고 다음 필터에 요청을 위임함)
  야구장에 입장하는 순서를 생각해보면:
1) 티켓에 있는 QR 코드를 보여주며 통과함
2) 짐 검사대에서 반입 금지 물품이 있는지 확인하며 통과함
3) 각 자리에 들어가기 전 티켓과 함께 최종 검사를 한번 더 진행 함
   => 만약 하나라도 통과를 못하면 예약한 야구장 좌석에 착석이 어려움
   스프링 시큐리티에서도 비슷함
   => 기본 설정으로 되어 있는 필터와 커스텀하게 설정된 필터를 함께 사용하여 HTTP 요청을 필터링함
   Spring Security

각 필터는 요청을 받고 각 담당 관리자에게 위임을 함
- 필터 체인이 요청을 가로챔
- 필터는 책임을 관리자 객체에게 역할을 위임함
- 각 필터는 책임을 적용한 후 체인의 다음 필터에 요청을

실무의 다양한 비즈니스 요구사항을 구현하기에는 기본 필터 구성으로는 부족함이 많음
- 따라서 구성 요소를 필터 체인에 추가하거나 대체하는 방법을 습득할 필요가 있음
  예를 들어,
- HTTP Basic 인증 방식 이외에 다른 인증 전략을 구현해야 할 때
- 권한 부여 이벤트를 외부 시스템에 전달해야 할 때
- 인증에 대한 로깅이 필요할 때
  Spring Security

스프링 시큐리티가 제공하는 필터 이외의 커스텀 필터를 기본 필터 체인 앞/뒤로 추가할 수 있음 (또는 기본 체인을 대체할 수도 있음)

스프링 시큐리티 아키텍쳐의 필터는 일반적인 HTTP 필터
- 필터를 생성하기 위해서는 jakarta.servlet 패키지의 Filter 인터페이스를 구현함
- doFilter() 메소드를 구현해야 함
- ServletRequest, ServletResponse, FilterChain 을 파라미터로 전달받음
 
ServletRequest: HTTP 요청
ServletResponse: HTTP 응답. 클라이언트로 전달하기 전 응답을 변경할 수 있음
FilterChain: 필터 체인. 체인의 다음 필터로 전달함


필터 체인은 필터가 작동하는 순서가 정의된 필터의 모음을 나타냄
- 필터 구현체에는 각자의 순서가 존재함
- 예시로, HTTP Basic 인증 방식을 적용하기 위해서 .httpBasic() 을 호출하면 필터 체인에 BasicAuthenticationFilter 가 추가됨
httpSecurity.httpBasic(Customizer.withDefaults());
- 각 필터는 순서가 존재하고 기본 필터들은 미리 정의된 필터의 순서 번호를 가지고 있음. 개발자가 직접 필터를 추가하면 설정에 따라
  기본 필터 전/후/사이에 추가가 됨

주의할 것.
- 같은 위치에 두 개 이상의 필터가 적용될 수 있음
- 동일한 위치라면 필터의 순서는 정해지지 않음 (순서 보장이 안되는 이슈가 있음)

기존 필터에 커스텀 필터 추가
기본으로 제공되는 필터에 직접 만든 필터를 앞/뒤로 추가할 수 있음
- 예시와 함께 알아보자
  예시. 모든 요청에 Request-Id 가 존재한다고 가정을 해보자
- 애플리케이션에서는 이 Request-Id 를 활용하여 요청에 대한 트래킹을 함 (Tracing 용)
- 따라서 Request-Id 는 필수로 전달되어야 함
- Request-Id 가 전달되는지에 대한 검증은 인증 검증을 수행하기 전에 진행하려고 함
- 인증 검증 전에 수행하는 이유는 인증 검증 자체가 DB I/O 가 발생하거나 추가적인 리소스를 필요로 하는 것이기 때문에 유효하지
  않은 요청에 대해서는 아예 시도를 하지 않도록 하기 위함 (리소스 최적화 관점)
  어떻게 할 수 있을까?

기존 필터에 커스텀 필터 추가
단계 1. 필터를 구현함
- 인입되는 요청에 원하는 헤더 값이 존재하는지를 검증하는 필터 클래스를 생성함
단계 2. 필터 체인에 해당 필터를 추가함
- 기존 필터에 헤더를 검증하는 필터를 추가함

RequestValidationFilter 의 흐름
1. 인입된 요청 헤더에 Request-Id 가 존재하는지 확인
2. 만약 존재하면 체인의 다음 필터로 전달
3. 만약 존재하지 않으면 HTTP 400 Bad Request 응답을 반환

addFilterBefore
addFilterBefore 활용
- HTTP Basic 인증을 담당하는 BasicAuthenticationFilter 가 수행되기 전에 RequestValidationFilter 를 수행하도록 설정함
- 예시의 간단함을 위해 인증되지 않은 요청을 허용하도록 설정함

기존 필터 뒤에 추가하기
어떤 케이스에 기존 필터 뒤에 필터를 추가할까?
예. 인증 과정 다음에 로깅을 하거나 이벤트를 발행하는 등의 역할을 수행해야 할 때, 필터 기능을 활용할 수 있음

LoggingFilter
로깅을 하는 로깅필터 클래스를 생성함
마찬가지로 로직을 수행하고 나면 다음 필터로 전달

addFilterAfter
addFilterAfter 를 활용하여 인증 필터 다음에 수행하도록 설정

기존 필터와 동일한 순서에 필터 추가하기
기존 필터가 수행하는 책임에 대해 다른 구현을 제공할 때, 이 기능이 필요함
앞에서 배웠던 addFilterBefore, addFilterAfter 와 비슷하게 이번 시간에는 addFilterAt 을 배우는 시간
예시. 만약 식별된 클라이언트에서의 요청만 허용하게끔 서버의 인증 인프라를 구축하려면 어떻게 할 수 있을까?
- 클라이언트는 HTTP 요청의 헤더에 서버와 약속한 문자열을 전달함
- 서버는 해당 값을 데이터베이스 또는 볼트에 있는 값과 비교하여 클라이언트를 식별할 수 있음

정적 키에 기반을 둔 식별
인증을 위한 정적 헤더 값에 기반을 둔 식별은 아래와 같은 흐름으로 진행됨
HTTP GET /api/v1/hello
Headers: Authorization=thisisme
Authorization 헤더의 값을 비교하여 접근을 할 수 있는지 판별

정적 키 등록
일반적으로는 데이터베이스나 비밀 볼트에 인증 키 값을 저장함
- 하지만 예시의 간단함을 위해 application.yml 파일에 키 값을 등록하고 활용하고자 함

@Value 로 키 값 불러오기
정적 키 인증을 관리하기 위해 StaticKeyAuthenticationFilter 클래스를 생성함
@Component 어노테이션을 통해 빈으로 등록
@Value 어노테이션을 활용해 application.yml 파일에 등록한 키 값을 주입받음

doFilter 구현
헤더의 값과 비교하여 동일하면 다음 필터로 전달

addFilterAt
의존성 주입으로 StaticKeyAuthenticationFilter 를 주입받음
- addFilterAt 으로 BasicAuthenticationFilter 위치에 staticKeyAuthenticationFilter 를 위치시킴
  
주의할 것
특정 위치에 필터를 추가해도 스프링 시큐리티는 그 위치에 필터가 하나라고 가정하지 않음
- 같은 위치에 필터를 더 추가할 수 있고, 그 경우에는 필터가 실행되는 순서를 보장하지 않음
- 절대 그 위치에 있는 필터와 대체되는 것이 아님
- 따라서 필터 체인에 필요가 없는 필터는 아예 추가되어서는 안됨

Filter 인터페이스를 구현하는 추상 클래스
스프링 시큐리티에서는 Filter 인터페이스를 감싸는 추상 클래스가 제공됨
- GenericFilterBean
  GenericFilterBean 을 확장한 OncePerRequestFilter 추상 클래스도 존재함
- 필터 체인에 추가한 필터를 요청당 한 번만 실행하도록 보장하지 않기 때문에 단 한 번만 실행될 수 있도록 지원함
  GenericFilterBean, OncePerRequestFilter 등 추가 기능을 제공하기 때문에 잘 활용하면 좋음
- 무조건적인 활용보다는 필요한 시점에 활용해야 함
- 가능한 간단하게 만드는 것이 좋으며 기초적인 기능의 경우에는 단순 Filter 인터페이스를 구현하는 것이 더 맞음

Filter 인터페이스로 로깅하는 필터를 추가
일반 Filter 인터페이스를 활용하여 로깅을 수행하는 필터 클래스를 생성하고 BasicAuthenticationFilter 위치에 추가

OncePerRequestFilter 의 특징
HttpServletRequest, HttpServletResponse 를 파라미터로 받음
- HTTP 요청이 들어오는 경우에 Filter 인터페이스에서는 형변환을 해서 사용해야 함
  요청에 따라 필터를 적용할 지 결정할 수 있음
- shouldNotFilter 메소드를 재정의하면 됨 (true 가 반환되면 해당 필터를 적용하지 않음)
비동기 요청에 대해서는 적용되지 않지만 shouldNotFilterAsyncDispatch 를 재정의하여 관련 처리를 할 수 있음

