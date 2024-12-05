package com.lumina.servlets;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumina.data.Annonce;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/fakesite/search")
public class FakeSiteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String keyword = req.getParameter("keyword");
        List<Annonce> annonces = generateFakeAnnonces(keyword);

        resp.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(resp.getWriter(), annonces);
    }

    private List<Annonce> generateFakeAnnonces(String keyword) {
        List<Annonce> annonces = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            annonces.add(new Annonce(
                    "https://via.placeholder.com/150",
                    keyword + " Title " + i,
                    "https://example.com/annonce" + i,
                    LocalDate.now().minusDays(i).toString(),
                    "FakeSite"
            ));
        }
        return annonces;
    }
}
