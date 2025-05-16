Evoting System

** Overview  **
The Evoting System is a web application designed for college elections to enable secure and efficient electronic voting. It allows admins to manage voters and candidates, users to cast votes, and displays election results.

** Features  **
- Admin: Manage voters and candidates, view election results. 
- User: Register and login, cast vote securely.  
- Automatic result display: Shows results and resets vote counts afterward.

** Technologies Used  **
- Front End: HTML, CSS, JavaScript  
- Back End: Java Servlets (Jakarta EE)  
- Database: MySQL  
- Server: Apache Tomcat  
- IDE: NetBeans (Ant-based Web Application)

** Project Structure  **
- src/: Java Servlet source code  
- web/: Frontend files - HTML, CSS, JavaScript  
- nbproject/: NetBeans project configuration  
- build.xml: Ant build script  
- .gitignore: Git ignore file

** Database Design  **
The database contains tables such as:  
- admin: Stores admin login details.
- voters: Stores user login details.  
- candidates: Stores candidate details.  
- vote_counts: Stores vote counts per candidate.

** Installation  **
To set up the project locally, follow these steps:  
1. Clone the repository:  
```bash
 git clone <repository-url>
```
   
2. Open the project in NetBeans as an Ant Web Application.
3. Set up the MySQL database and import the provided schema.
4. Update database connection settings in your servlet files.
5. Start Apache Tomcat server via NetBeans and run the project.
6. Access the application through your web browser.

