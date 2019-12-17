package com.jalen.lantern;

public class ItemData<T> {
    private T data;
    private boolean update;

    ItemData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }
}
