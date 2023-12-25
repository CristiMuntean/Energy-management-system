package ro.tuc.ds2020.services.mq;

public class UserOperation {
    private Long id;
    private String username;
    private String operation;

    public UserOperation() {
    }

    public UserOperation(Long id, String username, String operation) {
        this.id = id;
        this.username = username;
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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String toJson() {
        return """
                {
                    "operation": "%s",
                    "id": %d,
                    "username": "%s"
                }
                """.formatted(operation, id, username);
    }
}
