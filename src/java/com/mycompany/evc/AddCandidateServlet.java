package com.mycompany.evc;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.*;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class AddCandidateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String department = request.getParameter("department");
        String position = request.getParameter("position");

        // Get the uploaded photo
        Part filePart = request.getPart("photo");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

        // Path to your web Pages folder (where HTML/CSS and photos go)
        String uploadPath = getServletContext().getRealPath("/images");

        // Create the folder if not exists
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Save the image into /web Pages/
        filePart.write(uploadPath + File.separator + fileName);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/evmsys?useUnicode=true&characterEncoding=UTF-8",
                    "root", "1234")) {

                String sql = "INSERT INTO candidates (name, email, department, position, photo) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    stmt.setString(3, department);
                    stmt.setString(4, position);
                    stmt.setString(5, fileName); // Store just the name of the photo file

                    stmt.executeUpdate();
                }
            }

            // After successfully adding the candidate, redirect back to the dashboard or another page
            response.getWriter().println("<script>alert('Candidate added successfully.');window.location='adminDashboard.html';</script>");

        } catch (IOException | ClassNotFoundException | SQLException e) {
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}
