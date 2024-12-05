package com.lumina.data;


public class Annonce {
    private int id;
    private String titre;
    private String lien;
    private String image;
    private String site;
    private String dateRecuperation;


    public Annonce(int id, String titre, String lien, String image, String site, String dateRecuperation) {}


    public Annonce(String titre, String lien, String image, String site, String dateRecuperation) {
        this.titre = titre;
        this.lien = lien;
        this.image = image;
        this.site = site;
        this.dateRecuperation = dateRecuperation;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getLien() {
        return lien;
    }

    public void setLien(String lien) {
        this.lien = lien;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getDateRecuperation() {
        return dateRecuperation;
    }

    public void setDateRecuperation(String dateRecuperation) {
        this.dateRecuperation = dateRecuperation;
    }
}

