package model;

import com.github.javafaker.Faker;

import java.util.Locale;


public class User {
    private final static Faker faker = new Faker(new Locale("ru_RU"));
    private String email;
    private String password;
    private String name;

    public static User random() {
        User user = new User();

        user.name = faker.name().firstName();
        user.email = faker.internet().emailAddress();
        user.password = faker.internet().password();

        return user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
