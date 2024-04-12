package ro.mpp2024.model;

import java.io.Serializable;

public abstract class Entity<Tid> implements Serializable {
    protected Tid id;

    protected Entity() {
    }

    protected Entity(Tid id) {
        this.id = id;
    }

    public Tid getId() {
        return id;
    }

    public void setId(Tid id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}
