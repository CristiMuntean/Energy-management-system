package ro.tuc.ds2020.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Device {
    @Id
    @Column(name="id", columnDefinition = "bigint")
    private Long deviceId;

    @Column(name = "maxHourlyEnergyConsumption", nullable = false)
    private int maxHourlyEnergyConsumption;

    @Column(name = "userId", columnDefinition = "bigint")
    private Long userId;

    public Device() {
    }

    public Device(Long deviceId, int maxHourlyEnergyConsumption, Long userId) {
        this.deviceId = deviceId;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.userId = userId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long id) {
        this.deviceId = id;
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
}
