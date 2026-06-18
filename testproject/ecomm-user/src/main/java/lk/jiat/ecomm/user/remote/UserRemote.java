package lk.jiat.ecomm.user.remote;

import jakarta.ejb.Remote;
import lk.jiat.ecomm.user.dto.UserDTO;

import java.util.List;

@Remote
public interface UserRemote {
    UserDTO getUser(Long id);

    UserDTO getUserByEmail(String email);

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO);

    void deleteUser(Long id);

    List<UserDTO> getAllUsers();
}
