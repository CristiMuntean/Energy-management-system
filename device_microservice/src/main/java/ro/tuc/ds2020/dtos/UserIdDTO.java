package ro.tuc.ds2020.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

public class UserIdDTO extends RepresentationModel<UserIdDTO> {
    private Long userId;

    public UserIdDTO() {
    }

    public UserIdDTO(Long userId) {
        this.userId = userId;
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
        UserIdDTO userIdDTO = (UserIdDTO) o;
        return Objects.equals(userId, userIdDTO.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
