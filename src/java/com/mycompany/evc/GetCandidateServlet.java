package com.mycompany.evc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetCandidateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys?useUnicode=true&characterEncoding=UTF-8",
                    "root", "1234"); // 

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id, name, email,department,position,photo FROM candidates");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String department = rs.getString("department");
                String position = rs.getString("position");
                String photo = rs.getString("photo");

                out.println("<tr>");
                out.println("<td>" + id + "</td>");
                out.println("<td>" + name + "</td>");
                out.println("<td>" + email + "</td>");
                out.println("<td>" + department + "</td>");
                out.println("<td>" + position + "</td>");
                out.println("<td><img src='images/" + photo + "' width='100'></td>");
                out.println("<td>");
                out.println("<button class='btn-edit' onclick='editCandidate(" + id + ", \"" + name + "\", \"" + email + "\",\"" + department + "\",\"" + position + "\",\"" + photo + "\")'>Edit</button>");
                out.println("<button class='btn-delete' onclick='deleteCandidate(" + id + ")'>Delete</button>");
                out.println("</td>");
                out.println("</tr>");
            }

        } catch (ClassNotFoundException | SQLException e) {
            out.println("<tr><td colspan='5'>Error loading candidates.</td></tr>");
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
