package ro.mpp2024.repository;

import ro.mpp2024.model.User;

public interface UserRepository extends  Repository<User, Long>{
    User findByUsername(String username);
}
