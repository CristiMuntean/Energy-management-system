package ro.tuc.ds2020.dtos;

import org.springframework.hateoas.RepresentationModel;
import ro.tuc.ds2020.entities.Reading;

import java.util.Objects;

public class ReadingDTO extends RepresentationModel<ReadingDTO> {
    private Long id;
    private Long deviceId;
    private Double measurementValue;
    private Long timestamp;

    public ReadingDTO() {
    }

    public ReadingDTO(Long id, Long deviceId, Double measurementValue, Long timestamp) {
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

    @Override
    public int hashCode() {
        return Objects.hash(id, deviceId, measurementValue, timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ReadingDTO reading = (ReadingDTO) obj;
        return Objects.equals(deviceId, reading.deviceId) &&
                Objects.equals(measurementValue, reading.measurementValue) &&
                Objects.equals(timestamp, reading.timestamp);
    }
}
