<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Détails de l'annonce</title>

    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<%@ include file="header.jsp" %>
<h1>Détails de l'annonce</h1>

<c:if test="${annonce != null}">
    <p><strong>Titre :</strong> ${annonce.titre}</p>
    <p><strong>Lien :</strong> <a href="${annonce.lien}" target="_blank">${annonce.lien}</a></p>
    <p><strong>Image :</strong> <img src="${annonce.image}" alt="${annonce.titre}" /></p>
    <p><strong>Site :</strong> ${annonce.site}</p>
    <p><strong>Date de récupération :</strong> ${annonce.dateRecuperation}</p>
</c:if>

<c:if test="${annonce == null}">
    <p>Aucune annonce trouvée pour cet identifiant.</p>
</c:if>

<a href="index.jsp">Retour à l'accueil</a>

<%@ include file="footer.jsp" %>
</body>
</html>
