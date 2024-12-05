package com.lumina.wrappers;

import com.lumina.data.Annonce;

import java.util.List;



import com.lumina.data.Annonce;
import java.io.IOException;
import java.util.List;

public interface SiteWrapper {
    List<Annonce> fetchAnnonces(String keyword) throws IOException;
}

