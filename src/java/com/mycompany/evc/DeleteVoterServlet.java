package com.mycompany.evc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class DeleteVoterServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        String idStr = request.getParameter("id");

        try (PrintWriter out = response.getWriter()) {
            if (idStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("Missing voter ID");
                return;
            }

            int id = Integer.parseInt(idStr);

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con;
            con = java.sql.DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC", "root", "1234");
            String sql = "DELETE FROM voters WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int rowsDeleted = ps.executeUpdate();
            if (rowsDeleted > 0) {
                out.print("Voter deleted successfully.");
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
