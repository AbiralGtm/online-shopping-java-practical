package com.abiralgautam.onlineshopping.servlets;

import com.abiralgautam.onlineshopping.DatabaseConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class OrderUpdateServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderId = request.getParameter("orderId");
        String status = request.getParameter("status");
        try {

            String sql = "UPDATE orders SET order_status = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection(); // Assuming you have a Database class to get connection
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.valueOf(status));
                stmt.setInt(2, Integer.valueOf(orderId));
                int rowsAffected = stmt.executeUpdate();
                if(rowsAffected > 0)
                {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("order_status_update_success");
                }else{
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Error updating order status: ");
                }
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error updating order status: " + e.getMessage());

        }
    }
}