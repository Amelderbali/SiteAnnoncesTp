package com.lumina.data;

import java.util.Optional;

public interface RechercheDAO {
    void saveRecherche(Recherche recherche);
    Optional<Recherche> getRecherche(); // Retrieve the latest saved configuration
}
