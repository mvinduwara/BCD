package lk.dev.bcd.user.remote;

import jakarta.ejb.Remote;
import lk.dev.bcd.user.remote.dto.UserDTO;

import java.util.List;

@Remote
public interface UserRemote {

    UserDTO getUser(Long id);

    UserDTO createUser(UserDTO userdto);

    UserDTO updateUser(UserDTO user);

    UserDTO getUserByEmail(String email);

    UserDTO deleteUser(Long id);

    List<UserDTO> getAllUsers();
}
