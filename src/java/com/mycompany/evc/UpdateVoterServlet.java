package com.mycompany.evc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class UpdateVoterServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        

        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (PrintWriter out = response.getWriter()) {
            if (idStr == null || name == null || email == null || password == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("Missing parameters");
                return;
            }

            int id = Integer.parseInt(idStr);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC","root", "1234");
            String sql = "UPDATE voters SET name = ?, email = ?, password = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setInt(4, id);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                out.println("<script>alert('Voter Updated.'); window.location='adminDashboard.html';</script>");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("Voter not found.");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}

