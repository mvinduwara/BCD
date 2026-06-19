package lk.jiat.ecomm.web.servlet;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lk.dev.bcd.ejb.remote.AppSetting;
import lk.jiat.ecomm.user.dto.UserDTO;
import lk.jiat.ecomm.user.remote.TestRemote;
import lk.jiat.ecomm.user.remote.UserRemote;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.List;

@WebServlet("/test")
public class Test extends HttpServlet {

    @EJB(lookup = "java:global/ecomm-user-1.0/TestNewSessionBean") //J2EE 5+ no usages
    private TestRemote testRemote;

    @EJB // 1 usage
    private AppSetting appSetting;

    @Override
    public void init() throws ServletException {
        System.out.println("Test init...");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");
        resp.getWriter().write("Ecomm Web module Test<br>");

        resp.getWriter().write("App Name: " + appSetting.getName());

//        resp.setContentType("text/html");
//        resp.getWriter().write("Ecomm Web module Test<br>");
//        try {
//            TestRemote tr;
//            //InitialContext ic = new InitialContext();
//            //tr = (TestRemote) ic.lookup("java:global/ecomm-user-1.0/TestSessionBean");
//            HttpSession session = req.getSession();
//            if (session.getAttribute("testBean") == null) {
//                InitialContext ic = new InitialContext();
//                tr = (TestRemote) ic.lookup("java:global/ecomm-user-1.0/TestSessionBean");
//                session.setAttribute("testBean", tr);
//            }else {
//                tr = (TestRemote) session.getAttribute("testBean");
//            }
//            String test = tr.test();
//            resp.getWriter().write("Result: "+ test);
//            // Session, Application
////            List<UserDTO> allUsers = userRemote.getAllUsers();
////            for (UserDTO user : allUsers) {
////               user.toString();
////            }
//        } catch (NamingException e) {
//            throw new RuntimeException(e);
//        }
    }
}
