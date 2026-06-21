package lk.dev.bcd;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.dev.bcd.dto.UserDTO;
import lk.dev.bcd.service.UserService;

import java.io.IOException;

public class UserServlet extends HttpServlet {

    @EJB
    private UserService userService;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.service(req, resp);

       UserDTO userdto = userService.getUserById(1L);
       resp.getWriter().write( "UserProfile");
    }
}
