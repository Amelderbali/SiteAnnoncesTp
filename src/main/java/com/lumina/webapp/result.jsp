<html>
<head>
    <title>Résultats de recherche</title>

    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>

<%@ include file="header.jsp" %>
<h1>Résultats de recherche</h1>
<table border="1">
    <tr>
        <th>Image</th>
        <th>Titre</th>
        <th>Lien</th>
        <th>Date</th>
        <th>Site</th>
    </tr>
    <c:forEach var="annonce" items="${annonces}">
        <tr>
            <td><img src="${annonce.image}" alt="Image"></td>
            <td>${annonce.title}</td>
            <td><a href="${annonce.link}" target="_blank">Lien</a></td>
            <td>${annonce.date}</td>
            <td>${annonce.site}</td>
        </tr>
    </c:forEach>
</table>

<%@ include file="footer.jsp" %>
</body>
</html>
