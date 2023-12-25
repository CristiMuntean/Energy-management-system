package ro.tuc.ds2020.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Reading {
    @Id
    @Column(name="id", columnDefinition = "bigint")
    private Long id;

    @Column(name="device_id", nullable = false)
    private Long deviceId;

    @Column(name = "measurement_value", nullable = false)
    private Double measurementValue;

    @Column(name = "timestamp", nullable = false)
    private Long timestamp;

    public Reading() {
    }

    public Reading(Long id, Long deviceId, Double measurementValue, Long timestamp) {
        this.id = id;
        this.deviceId = deviceId;
        this.measurementValue = measurementValue;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Double getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(Double measurementValue) {
        this.measurementValue = measurementValue;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
