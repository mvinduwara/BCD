package lk.dev.bcd;

import lk.dev.bcd.dto.UserDTO;
import lk.dev.bcd.service.UserService;

import java.util.List;

public class UserSessionBean implements UserService {
    @Override
    public UserDTO getUserById(Long id) {
        System.out.println("getUserById");
        return null;
    }

    @Override
    public void getUser(UserDTO userDTO) {
        System.out.println("getUser");
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        System.out.println("updateUser");
    }

    @Override
    public void deleteUser(Long id) {
        System.out.println("deleteUser");
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return List.of();
    }
}
