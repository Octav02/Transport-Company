package ro.mpp2024.transportcompany.repository;

import ro.mpp2024.transportcompany.model.User;

public interface UserRepository extends  Repository<User, Long>{
    User findByUsername(String username);
}
