package com.abiralgautam.onlineshopping.servlets;

import com.abiralgautam.onlineshopping.DatabaseConnection;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");


        Connection connection = null;
        try{
            connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();
            PrintWriter out = response.getWriter();
            if (resultSet.next()) {
                out.println("login_successful" );
            } else {
                out.println("Invalid username or password");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }catch (Exception e){

            e.printStackTrace();
        }finally {
            DatabaseConnection.closeConnection(connection);
        }
    }
}
