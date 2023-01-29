package pw.bookly.backend.web;

import pw.bookly.backend.models.User;

import javax.validation.constraints.Email;

public record UserDTO(long id, Boolean isActive, @Email String email, String firstName, String lastName, String password) {

    public static UserDTO valueFrom(User user) {
        return new UserDTO(user.getId(), user.getActive(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword());
    }

    public static User convertToUser(UserDTO dto) {
        User user = new User();
        user.setId(dto.id());
        user.setActive(dto.isActive());
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setPassword(dto.password());
        return user;
    }
}
