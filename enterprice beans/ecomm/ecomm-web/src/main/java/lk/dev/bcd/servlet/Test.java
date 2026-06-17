package lk.dev.bcd.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.dev.bcd.user.remote.UserRemote;
import lk.dev.bcd.user.remote.dto.UserDTO;

import javax.naming.InitialContext;
import java.io.IOException;
import java.util.List;

public class Test extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.service(req, resp);

//        resp.getWriter().write("E comm web Test!");

        try {

            InitialContext ctx = new InitialContext();
            UserRemote user = (UserRemote) ctx.lookup("java:global/bcd-webapp/Cal");

            List<UserDTO> allUsers = user.getAllUsers();
            for (UserDTO userDTO : allUsers) {
                user.toString();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
