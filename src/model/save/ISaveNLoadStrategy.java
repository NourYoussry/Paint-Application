package model.save;

public interface ISaveNLoadStrategy {

    void save(String path);
    void load(String path);

}
