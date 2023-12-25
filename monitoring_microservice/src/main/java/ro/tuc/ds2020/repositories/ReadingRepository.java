package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ro.tuc.ds2020.entities.Reading;

import java.util.List;

public interface ReadingRepository extends JpaRepository<Reading, Long> {
    @Query(value = "SELECT MAX(r.id) " +
            "FROM Reading r")
    Long getMaxId();

    @Query(value = "SELECT r " +
            "FROM Reading r " +
            "WHERE r.deviceId = ?1 AND r.timestamp BETWEEN ?2 AND ?3")
    List<Reading> getReadingsForDeviceBetweenTimestamps(Long deviceId, Long startTimestamp, Long endTimestamp);
}
