package ro.tuc.ds2020.services.mq;

public class UserOperation {
    private String operation;
    private Long id;
    private String username;

    public UserOperation() {
    }

    public UserOperation(String operation, Long id, String username) {
        this.operation = operation;
        this.id = id;
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
