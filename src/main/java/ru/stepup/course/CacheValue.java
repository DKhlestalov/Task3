package ru.stepup.course;

public class CacheValue {
    private Object value;
    private long expireTime;

    public CacheValue(Object value, long expireTime) {
        this.value = value;
        this.expireTime = expireTime;
    }

    public Object getValue() {
        return value;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
