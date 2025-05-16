package com.mycompany.evc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@MultipartConfig
public class UpdateCandidateServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String newEmail = request.getParameter("email");
        String department = request.getParameter("department");
        String position = request.getParameter("position");
        Part filePart = request.getPart("photo");  // Get the uploaded file
        String photo = (filePart != null) ? filePart.getSubmittedFileName() : ""; // Get file name

        try (PrintWriter out = response.getWriter()) {
            if (idStr == null || name == null || newEmail == null || department == null || position == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("Missing parameters");
                return;
            }

            int id = Integer.parseInt(idStr);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC",
                    "root", "1234");

            // Step 1: Get current email from DB
            String currentEmail = "";
            String getEmailSql = "SELECT email, photo FROM candidates WHERE id = ?";
            PreparedStatement getEmailStmt = con.prepareStatement(getEmailSql);
            getEmailStmt.setInt(1, id);
            ResultSet rsEmail = getEmailStmt.executeQuery();
            if (rsEmail.next()) {
                currentEmail = rsEmail.getString("email");
                photo = rsEmail.getString("photo");  // Keep the old photo if new photo is not uploaded
            } else {
                out.println("<script>alert('Candidate not found.'); window.location='adminDashboard.html';</script>");
                return;
            }

            // Step 2: Only check for duplicate if email was changed
            if (!currentEmail.equalsIgnoreCase(newEmail)) {
                String checkSql = "SELECT id FROM candidates WHERE email = ?";
                PreparedStatement checkStmt = con.prepareStatement(checkSql);
                checkStmt.setString(1, newEmail);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    out.println("<script>alert('Email already exists for another candidate.'); window.location='adminDashboard.html';</script>");
                    return;
                }
            }

            // Step 3: If photo is uploaded, save the photo and update the database
            if (filePart != null) {
                String uploadDir = getServletContext().getRealPath("") + File.separator + "/images";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();  // Create directories if not exist
                }

                String fileName = filePart.getSubmittedFileName();
                String filePath = uploadDir + File.separator + fileName;

                filePart.write(filePath);  // Save the file to the server

                photo =  fileName; // Set the relative path of the photo
            }

            // Step 4: Update record in the database
            String updateSql = "UPDATE candidates SET name = ?, email = ?, department = ?, position = ?, photo = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(updateSql);
            ps.setString(1, name);
            ps.setString(2, newEmail);
            ps.setString(3, department);
            ps.setString(4, position);
            ps.setString(5, photo);  // Update photo field
            ps.setInt(6, id);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                out.println("<script>alert('Candidate updated successfully.'); window.location='adminDashboard.html';</script>");
            } else {
                out.println("<script>alert('Update failed. Candidate not found.'); window.location='adminDashboard.html';</script>");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }
}
