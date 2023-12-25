package ro.tuc.ds2020.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class UserId {
    @Id
    @Column(name="userId", columnDefinition = "bigint")
    private Long userId;

    public UserId() {
    }

    public UserId(Long userId) {
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
        UserId userId1 = (UserId) o;
        return Objects.equals(userId, userId1.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
