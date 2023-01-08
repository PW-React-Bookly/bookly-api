package pw.bookly.backend.web;

import pw.bookly.backend.models.User;

import javax.validation.constraints.Email;

public record UserDTO(Long id, String username, String password, @Email String email) {

    public static UserDTO valueFrom(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getEmail());
    }

    public static User convertToUser(UserDTO userDto) {
        User user = new User();
        user.setId(userDto.id());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());
        return user;
    }
}
