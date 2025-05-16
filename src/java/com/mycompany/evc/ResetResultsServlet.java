package com.mycompany.evc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class ResetResultsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys", "root", "1234")) {

                String resetQuery = "UPDATE vote_counts SET vote_count = 0";
                PreparedStatement pstmt = conn.prepareStatement(resetQuery);
                int rows = pstmt.executeUpdate();

                out.print("Vote counts successfully reset for " + rows + " entries.");

            }
        } catch (Exception e) {
            out.print("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
