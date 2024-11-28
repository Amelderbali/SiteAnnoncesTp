package com.lumina.wrappers;

import com.lumina.data.Annonce;

import java.util.List;

public interface SiteWrapper {
    /**
     * Récupère une liste d'annonces correspondant aux mots-clés donnés.
     *
     * @param motsCles Les mots-clés de recherche.
     * @return Une liste d'annonces correspondant à la recherche.
     */
    List<Annonce> fetchAnnonces(String motsCles);
}
