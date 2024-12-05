package com.lumina.servlets;

import com.lumina.data.Annonce;
import com.lumina.data.AnnonceDAO;
import com.lumina.data.AnnonceDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/annonce/detail")
public class AnnonceDetailServlet extends HttpServlet {
    private final AnnonceDAO annonceDAO = new AnnonceDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Récupérer l'identifiant de l'annonce depuis les paramètres de la requête
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'identifiant de l'annonce est manquant.");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            // Récupérer l'annonce à partir de la base de données
            Annonce annonce = annonceDAO.getAnnonceById(id);

            if (annonce == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Annonce non trouvée.");
                return;
            }

            // Ajouter l'annonce comme attribut pour la JSP
            req.setAttribute("annonce", annonce);

            // Rediriger vers la page JSP des détails
            req.getRequestDispatcher("/detail.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'identifiant de l'annonce est invalide.");
        }
    }
}
