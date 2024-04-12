package ro.mpp2024.persistance;


import ro.mpp2024.model.User;

public interface UserRepository extends  Repository<User, Long>{
    User findByUsername(String username);
}
