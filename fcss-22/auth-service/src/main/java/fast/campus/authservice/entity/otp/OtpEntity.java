package fast.campus.authservice.entity.otp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "otp")
@NoArgsConstructor
public class OtpEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column
  private String userId;

  @Column
  private String otpCode;

  public OtpEntity(String userId, String otpCode) {
    this.userId = userId;
    this.otpCode = otpCode;
  }

  public void renewOtp(String newOtpCode) {
    this.otpCode = newOtpCode;
  }
}
