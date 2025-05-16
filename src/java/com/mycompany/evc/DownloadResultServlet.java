package com.mycompany.evc;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class DownloadResultServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"election_results.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("Position,Candidate Name,Vote Count");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys", "root", "1234")) {

                String query = "SELECT vote_counts.position, candidates.name, vote_counts.vote_count " +
                               "FROM vote_counts " +
                               "JOIN candidates ON vote_counts.candidate_id = candidates.id " +
                               "ORDER BY vote_counts.position, vote_counts.vote_count DESC";

                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                // Process the result set and write to CSV
                while (rs.next()) {
                    String position = rs.getString("position");
                    String name = rs.getString("name");
                    int votes = rs.getInt("vote_count");
                    writer.println(position + "," + name + "," + votes);
                }

                
            }
        } catch (Exception e) {
            writer.println("Error," + e.getMessage());
            e.printStackTrace();
        }
    }
}
