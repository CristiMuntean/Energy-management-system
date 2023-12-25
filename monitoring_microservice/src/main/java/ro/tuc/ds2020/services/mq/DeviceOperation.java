package ro.tuc.ds2020.services.mq;

public class DeviceOperation {
    private String operation;
    private Long deviceId;
    private int maxHourlyEnergyConsumption;
    private Long userId;

    public DeviceOperation() {
    }

    public DeviceOperation(String operation, Long deviceId, int maxHourlyEnergyConsumption, Long userId) {
        this.operation = operation;
        this.deviceId = deviceId;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.userId = userId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
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
}
