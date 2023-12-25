package ro.tuc.ds2020.entities;


import javax.persistence.*;

@Entity
public class Device {
    @Id
    @Column(name="id", columnDefinition = "bigint")
    private Long id;

    @Column(name="decription")
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "maxHourlyEnergyConsumption", nullable = false)
    private int maxHourlyEnergyConsumption;

    public Device() {
    }

    public Device(Long id, String description, String address, int maxHourlyEnergyConsumption) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMaxHourlyEnergyConsumption() {
        return maxHourlyEnergyConsumption;
    }

    public void setMaxHourlyEnergyConsumption(int maxHourlyEnergyConsumption) {
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }
}
