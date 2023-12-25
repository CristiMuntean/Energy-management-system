package ro.tuc.ds2020.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.entities.UserIdDeviceMapping;

import java.util.List;
import java.util.Optional;

public interface UserIdDeviceMappingRepository extends JpaRepository<UserIdDeviceMapping, Long> {
    @Query(value = "SELECT MAX(d.id) " +
            "FROM UserIdDeviceMapping d")
    Long getMaxId();

    @Query(value = "SELECT d.id " +
        "FROM UserIdDeviceMapping d " +
            "WHERE d.userId = :userId")
    List<Long> findUserDeviceMappingByUserId(@Param("userId") Long userId);

    UserIdDeviceMapping findUserIdDeviceMappingByDeviceIdAndUserId(Long deviceId, Long userId);
    Optional<UserIdDeviceMapping> findUserIdDeviceMappingByDeviceId(Long deviceId);
}
