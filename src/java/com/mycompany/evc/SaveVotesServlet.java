package com.mycompany.evc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

public class SaveVotesServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Debugging line to ensure servlet is called
        System.out.println("Servlet called");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            out.println("Session expired. Please log in again.");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        System.out.print("Before data");
        String presidentId = request.getParameter("president");
        String vicePresidentId = request.getParameter("vicePresident");
        String secretaryId = request.getParameter("secretary");
        String treasurerId = request.getParameter("treasurer");
        String representativeId = request.getParameter("representative");

        System.out.println("President ID: " + presidentId);
        System.out.println("Vice President ID: " + vicePresidentId);
        System.out.println("Secretary ID: " + secretaryId);
        System.out.println("Treasurer ID: " + treasurerId);
        System.out.println("Representative ID: " + representativeId);

        System.out.println("After data");
        // Check if all candidates have been selected
        if (presidentId == null || vicePresidentId == null || secretaryId == null
                || treasurerId == null || representativeId == null) {
            out.println("Please vote for all positions.");
            return;
        }
        System.out.println("After return");
        // Connect to the database
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC",
                    "root", "1234")) {

                // Start transaction
                conn.setAutoCommit(false);  // Start transaction manually
                System.out.println("Inside connection");
                try {
                    // Update vote count for each position
                    updateVoteCount(conn, Integer.parseInt(presidentId), "President");
                    updateVoteCount(conn, Integer.parseInt(vicePresidentId), "Vice President");
                    updateVoteCount(conn, Integer.parseInt(secretaryId), "Secretary");
                    updateVoteCount(conn, Integer.parseInt(treasurerId), "Treasurer");
                    updateVoteCount(conn, Integer.parseInt(representativeId), "Representative");

                    // Commit the transaction
                    conn.commit();
                    out.println("<script>alert('Vote added successfully.'); window.location='voterLogin.html';</script>");

                } catch (SQLException e) {
                    conn.rollback();  // Rollback transaction if any error occurs
                    out.println("Error saving votes: " + e.getMessage());
                } finally {
                    conn.setAutoCommit(true);  // Restore default autocommit behavior
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        }
    }

    // Method to update vote count for a candidate
    private void updateVoteCount(Connection conn, int candidateId, String position) throws SQLException {
        System.out.println("till here works");

        String checkQuery = "SELECT * FROM vote_counts WHERE candidate_id = ? AND position = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
        checkStmt.setInt(1, candidateId);
        checkStmt.setString(2, position);
        ResultSet rs = checkStmt.executeQuery();

        if (rs.next()) {
            // If record exists, update vote count
            int currentVotes = rs.getInt("vote_count");
            String updateQuery = "UPDATE vote_counts SET vote_count = ? WHERE candidate_id = ? AND position = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, currentVotes + 1);
            updateStmt.setInt(2, candidateId);
            updateStmt.setString(3, position);
            updateStmt.executeUpdate();
        } else {
            // If no record exists, insert a new record with vote count = 1
            System.out.println("Database works");
            String insertQuery = "INSERT INTO vote_counts (candidate_id, position, vote_count) VALUES (?, ?, ?)";
            System.out.println("Database donee e");
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setInt(1, candidateId);
            insertStmt.setString(2, position);
            insertStmt.setInt(3, 1);
            insertStmt.executeUpdate();
        }
    }
}
