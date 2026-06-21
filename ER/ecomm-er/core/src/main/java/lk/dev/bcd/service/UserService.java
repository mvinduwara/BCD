package lk.dev.bcd.service;

import jakarta.ejb.Remote;
import lk.dev.bcd.dto.UserDTO;

import java.util.List;

@Remote
public interface UserService {

    UserDTO getUserById(Long id);
    void getUser(UserDTO userDTO);
    void updateUser(UserDTO userDTO);
    void deleteUser(Long id);
    List<UserDTO> getAllUsers();
}
