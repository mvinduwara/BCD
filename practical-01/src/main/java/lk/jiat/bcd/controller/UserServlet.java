package lk.jiat.bcd.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lk.jiat.bcd.model.User;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/submit")
public class UserServlet extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.doPost(request, response);

        String name  = request.getParameter("name");
        String mobile =request.getParameter("mobile");

        //Create A New User
        User user = new User();
        user.setName(name);
        user.setMobile(mobile);


        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("Name:"+ user.getName());
        out.println("Mobile:"+ user.getMobile());
    }
}
