package project.adam.entity.member;

public enum Privilege {
    USER(0), ADMIN(1);

    Privilege(int value) {
        this.value = value;
    }

    public final int value;
}
