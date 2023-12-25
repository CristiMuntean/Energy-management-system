package ro.tuc.ds2020.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class DeviceDTO extends RepresentationModel<DeviceDTO> {
    private Long deviceId;
    private int maxHourlyEnergyConsumption;
    private Long userId;

    public DeviceDTO() {
    }

    public DeviceDTO(Long deviceId, int maxHourlyEnergyConsumption, Long userId) {
        this.deviceId = deviceId;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.userId = userId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public int getMaxHourlyEnergyConsumption() {
        return maxHourlyEnergyConsumption;
    }

    public void setMaxHourlyEnergyConsumption(int maxHourlyEnergyConsumption) {
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return maxHourlyEnergyConsumption == deviceDTO.maxHourlyEnergyConsumption && Objects.equals(deviceId, deviceDTO.deviceId) &&
                Objects.equals(userId, deviceDTO.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), deviceId, maxHourlyEnergyConsumption, userId);
    }
}
