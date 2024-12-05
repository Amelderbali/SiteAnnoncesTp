package com.lumina.servlets;




import com.lumina.data.Annonce;
import com.lumina.wrappers.LeboncoinWrapper;
import com.lumina.wrappers.SiteWrapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String keyword = req.getParameter("keyword");
        SiteWrapper wrapper = new LeboncoinWrapper(); // Exemple avec LeboncoinWrapper
        List<Annonce> annonces = wrapper.fetchAnnonces(keyword);

        req.setAttribute("annonces", annonces);
        req.getRequestDispatcher("/result.jsp").forward(req, resp);
    }
}
