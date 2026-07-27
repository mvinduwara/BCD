package dev.bcd;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(value = "/Test", asyncSupported = false)
public class Test extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        super.service(req, resp);
        
        System.out.println("Start : " + Thread.currentThread().getName());

        AsyncContext async = request.startAsync();
        async.start(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName() + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
            async.complete();
        });
        System.out.println("End : " + Thread.currentThread().getName());
    }
}
