package fast.campus.netplix.repository.user;

import fast.campus.netplix.entity.user.UserEntity;

import java.util.Optional;

public interface UserCustomRepository {
    Optional<UserEntity> findByEmail(String email);
}
