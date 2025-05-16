package com.mycompany.evc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public class UserDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("voterEmail") == null) {
            // If not logged in, redirect to login page
            response.sendRedirect("voterLogin.html");
        } else {
            // If logged in, serve the user dashboard page
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            // Serve the content of userDashboard.html
            out.println("<html>");
            out.println("<head><title>User Dashboard</title></head>");
            out.println("<body>");
            out.println("<h2>Welcome to your Dashboard</h2>");
            out.println("<p>Here, you can proceed to vote in the election.</p>");
            out.println("<a href='voting.html'>Go to Voting</a>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
