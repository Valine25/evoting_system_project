/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.evc;

/**
 *
 * @author intel
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = java.sql.DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys?useUnicode=true&characterEncoding=UTF-8",
                    "root", "1234"
            )) {
                // Check admin credentials
                String sql = "SELECT * FROM admin WHERE email=? AND password=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, email);
                ps.setString(2, password);
                
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    // Login success
                    response.sendRedirect("adminDashboard.html");
                } else {
                    // Login failed
                    out.println("<script>alert('Invalid Admin credentials.'); window.location='adminLogin.html';</script>");
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
        }
    }
}
