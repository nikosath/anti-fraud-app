package antifraud.security;

public enum LockOperationEnum {
    LOCK, UNLOCK;

    public static LockOperationEnum fromIsAccountUnlocked(boolean isAccountUnlocked) {
        return isAccountUnlocked ? UNLOCK : LOCK;
    }

    public static boolean toIsAccountUnlocked(LockOperationEnum operation) {
        return LockOperationEnum.UNLOCK.equals(operation);
    }
}
