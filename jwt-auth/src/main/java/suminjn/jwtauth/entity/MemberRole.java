package suminjn.jwtauth.entity;

public enum MemberRole {
    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    MemberRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
