package lk.dev.bcd.user.remote.bean;

import jakarta.ejb.Stateless;
import lk.dev.bcd.user.remote.UserRemote;
import lk.dev.bcd.user.remote.dto.UserDTO;
import org.w3c.dom.ls.LSOutput;

import java.util.List;

@Stateless
public class UserSessionBean implements UserRemote {


    @Override
    public UserDTO getUser(Long id) {
        return new UserDTO();
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        return new UserDTO();
    }

    @Override
    public UserDTO updateUser(UserDTO user) {
        return new UserDTO();
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return new UserDTO();
    }

    @Override
    public void deleteUser(Long id) {
        System.out.println("User Deleted Sucessfully : deleteUser");
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return List.of();
    }
}
