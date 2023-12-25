package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.dtos.DeviceWithUserIdDTO;
import ro.tuc.ds2020.entities.Device;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query(value = "SELECT MAX(d.id) " +
            "FROM Device d")
    Long getMaxId();

    @Query(value = "SELECT d.id, d.decription, d.address, d.max_hourly_energy_consumption " +
            "FROM device d " +
            "JOIN user_id_device_mapping udm ON d.id = udm.device_id " +
            "WHERE udm.user_id = :userId", nativeQuery = true)
    Optional<List<Device>> getDevicesByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT d.id, d.decription, d.address, d.max_hourly_energy_consumption, udm.user_id " +
            "FROM device d " +
            "LEFT JOIN user_id_device_mapping udm ON d.id = udm.device_id", nativeQuery = true)
    List<?> getDevicesWithUserId();
}
