package com.mycompany.evc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class VoterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("name");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (email == null || password == null || name == null || email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            out.println("<script>alert('Please fill in all fields!'); window.location='voterLogin.html';</script>");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC",
                    "root", "1234")) {

                // 1. Check if email exists
                String checkQuery = "SELECT * FROM voters WHERE email = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, email);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Email exists → check password and name
                    String dbPassword = rs.getString("password");
                    String dbName = rs.getString("name");

                    if (dbPassword.equals(password) && dbName.equalsIgnoreCase(name)) {
                        // Login successful, set session and redirect
                        int userId = rs.getInt("id");
                        HttpSession session = request.getSession();
                        session.setAttribute("voterEmail", email);
                        session.setAttribute("userId", userId);
                        response.sendRedirect("userDashboard.html");
                    } else {
                        out.println("<script>alert('Email already exists with different credentials!'); window.location='voterLogin.html';</script>");
                    }

                } else {
                    // Email doesn't exist → insert new voter
                    String insertQuery = "INSERT INTO voters (email, password, name) VALUES (?, ?, ?)";
                    PreparedStatement ps = conn.prepareStatement(insertQuery);
                    ps.setString(1, email);
                    ps.setString(2, password);
                    ps.setString(3, name);

                    int result = ps.executeUpdate();
                    if (result > 0) {
                        // Get user ID after successful insertion
                        String getIdQuery = "SELECT id FROM voters WHERE email = ?";
                        PreparedStatement idStmt = conn.prepareStatement(getIdQuery);
                        idStmt.setString(1, email);
                        ResultSet idRs = idStmt.executeQuery();

                        if (idRs.next()) {
                            int userId = idRs.getInt("id");
                            HttpSession session = request.getSession();
                            session.setAttribute("voterEmail", email);
                            session.setAttribute("userId", userId);
                            response.sendRedirect("userDashboard.html");
                        } else {
                            out.println("<script>alert('Registration successful but failed to retrieve user ID.'); window.location='voterLogin.html';</script>");
                        }
                    } else {
                        out.println("<script>alert('Failed to register.'); window.location='voterLogin.html';</script>");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
