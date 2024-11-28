package com.lumina.data;

import java.util.List;

public interface AnnonceDAO {
    void createTable();

    void insertAnnonce(Annonce annonce);

    void insertAnnonces(List<Annonce> annonces);

    List<Annonce> getAllAnnonces();

    void deleteAnnonceById(int id);
}
