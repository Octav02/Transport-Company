package ro.mpp2024;

import ro.mpp2024.model.User;
import ro.mpp2024.repository.UserRepository;
import ro.mpp2024.repository.dbrepostiory.UserDBRepository;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            props.load(new FileReader("properties/db.properties"));
        } catch (IOException e) {
            System.out.println("Cannot find file " + e);
        }

        UserRepository userRepository = new UserDBRepository(props);

        User foundUser = userRepository.findById(1L);
        System.out.println("Found User" + foundUser);

        User user = new User("Mihai Mihai", "Mihai", "Mihai", "Mihai");
        userRepository.add(user);

        Collection<User> users = userRepository.getAll();
        System.out.println("User adaugat si getAll");
        users.forEach(System.out::println);


        User user2 = userRepository.findByUsername("Mihai Mihai");
        System.out.println("User update before  + Gasit dupa username: " + user2);
        user2.setFirstName("Mihai2");
        userRepository.update(user2, user2.getId());

        user2 = userRepository.findById(user2.getId());
        System.out.println("User update after  + Gasit dupa id: " + user2);

        userRepository.delete(user2);

        Iterable<User> users2 = userRepository.findAll();
        System.out.println("User sters si findAll");
        users2.forEach(System.out::println);


    }
}