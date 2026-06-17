package lk.dev.bcd.user.remote.bean;

import jakarta.ejb.Stateless;
import lk.dev.bcd.user.remote.UserRemote;
import lk.dev.bcd.user.remote.dto.UserDTO;

import java.util.List;

@Stateless
public class UserSessionBean implements UserRemote {


    @Override
    public UserDTO getUser(Long id) {
        return null;
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        return null;
    }

    @Override
    public UserDTO updateUser(UserDTO user) {
        return null;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return null;
    }

    @Override
    public UserDTO deleteUser(Long id) {
        return null;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return List.of();
    }
}
