package ro.mpp2024.transportcompany.model;

public abstract class Entity<Tid> {
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
}
