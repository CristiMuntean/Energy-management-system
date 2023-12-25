package ro.tuc.ds2020.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeviceWithUserIdDTO {
    private Long id;
    private String description;
    private String address;
    private int maxHourlyEnergyConsumption;
    private Long userId;
}
