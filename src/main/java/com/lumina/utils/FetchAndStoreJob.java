package com.lumina.utils;

import com.lumina.data.Annonce;
import com.lumina.data.AnnonceDAO;
import com.lumina.wrappers.SiteWrapper;

import java.util.List;

public class FetchAndStoreJob implements Runnable {

    private final SiteWrapper siteWrapper;
    private final AnnonceDAO annonceDAO;
    private final String motsCles;
    private final String location;
    private final JobListener listener;

    public FetchAndStoreJob(SiteWrapper siteWrapper, AnnonceDAO annonceDAO, String motsCles, String location, JobListener listener) {
        this.siteWrapper = siteWrapper;
        this.annonceDAO = annonceDAO;
        this.motsCles = motsCles;
        this.location = location;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            System.out.println("Fetching annonces...");
            // Fetch annonces from the wrapper
            List<Annonce> annonces = siteWrapper.fetchAnnonces(motsCles);

            System.out.println("Storing annonces in the database...");
            // Store annonces in the database
            annonceDAO.insertAnnonces(annonces);

            System.out.println("Batch job completed successfully.");

            // Notify listener with the fetched data
            if (listener != null) {
                listener.onJobCompleted(annonces);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Batch job failed.");
            if (listener != null) {
                listener.onJobFailed(e);
            }
        }
    }

    // Listener interface for UI updates
    public interface JobListener {
        void onJobCompleted(List<Annonce> annonces);
        void onJobFailed(Exception e);
    }
}
