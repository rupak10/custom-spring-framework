package com.rupak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rupak.annotation.Autowired;
import com.rupak.annotation.Servlet;
import com.rupak.models.dto.SearchResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Servlet(urlMapping = "/api/products/srv/search")
public class SearchServlet extends HttpServlet {

    @Autowired
    private ProductController productController;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String searchQuery = req.getParameter("query");
        System.out.println("searchQuery = " + searchQuery);

        ObjectMapper objectMapper = new ObjectMapper();

        SearchResponse searchResponse = productController.search(searchQuery);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(objectMapper.writeValueAsString(searchResponse));
    }
}
