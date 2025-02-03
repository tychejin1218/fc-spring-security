package fast.campus.fcss02.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 커스텀 어노테이션 정의
 *
 * <p>CustomEncryption 어노테이션은 특정 필드에만 적용할 수 있도록 설계된 어노테이션입니다.</p>
 *
 * <p><b>특징:</b></p>
 * <ul>
 *   <li>annotation 패키지 내에 정의됩니다.</li>
 *   <li>인터페이스 형식(@interface)으로 작성되며 '@' 기호를 사용해 어노테이션임을 나타냅니다.</li>
 * </ul>
 *
 * <p><b>주요 설정:</b></p>
 * <ul>
 *   <li><b>@Target(ElementType.FIELD)</b>: 해당 어노테이션은 필드에만 적용할 수 있도록 제한합니다.</li>
 *   <li><b>@Retention(RetentionPolicy.RUNTIME)</b>: 어노테이션이 런타임 시점에 유지되며, 리플렉션을 통해 접근하거나 동작할 수 있습니다.</li>
 * </ul>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomEncryption {

}
