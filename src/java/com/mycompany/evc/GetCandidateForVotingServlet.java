package com.mycompany.evc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class GetCandidateForVotingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Get voter name from session
        HttpSession session = request.getSession();
        String voterName = (String) session.getAttribute("voterName"); // Assuming voterName is stored in session

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys?useUnicode=true&characterEncoding=UTF-8",
                    "root", "1234");

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id, name, position FROM candidates");

            // Prepare a JSON object to store candidates by position
            JSONObject candidatesData = new JSONObject();

            // Initialize a JSONArray for each position
            JSONArray presidentCandidates = new JSONArray();
            JSONArray vicePresidentCandidates = new JSONArray();
            JSONArray secretaryCandidates = new JSONArray();
            JSONArray treasurerCandidates = new JSONArray();
            JSONArray representativeCandidates = new JSONArray();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String position = rs.getString("position");

                JSONObject candidate = new JSONObject();
                candidate.put("id", id);
                candidate.put("name", name);

                String imageFileName = name.trim().toLowerCase().replaceAll("\\s+", "_") + ".jpg";
                candidate.put("photoUrl", imageFileName);

                switch (position) {
                    case "President":
                        presidentCandidates.put(candidate);
                        break;
                    case "Vice President":
                        vicePresidentCandidates.put(candidate);
                        break;
                    case "Secretary":
                        secretaryCandidates.put(candidate);
                        break;
                    case "Treasurer":
                        treasurerCandidates.put(candidate);
                        break;
                    case "Representative":
                        representativeCandidates.put(candidate);
                        break;
                }
            }

            // Add the arrays to the candidatesData object
            candidatesData.put("president", presidentCandidates);
            candidatesData.put("vicePresident", vicePresidentCandidates);
            candidatesData.put("secretary", secretaryCandidates);
            candidatesData.put("treasurer", treasurerCandidates);
            candidatesData.put("representative", representativeCandidates);

            // Add voter name to the response
            JSONObject responseData = new JSONObject();
            responseData.put("voterName", voterName); // Add voter name to the response
            responseData.put("candidates", candidatesData);

            // Output the JSON data
            out.println(responseData.toString());

        } catch (ClassNotFoundException | SQLException e) {
            out.println("{\"error\":\"Error loading candidates: " + e.getMessage() + "\"}");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
