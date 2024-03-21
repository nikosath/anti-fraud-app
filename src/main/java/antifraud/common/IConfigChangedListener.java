package antifraud.common;

public interface IConfigChangedListener {
    void onConfigChanged(Enum.ConfigCategory configCategory);
}
