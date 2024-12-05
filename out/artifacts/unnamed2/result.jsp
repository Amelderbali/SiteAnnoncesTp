<html>
<head>
    <title>Résultats de recherche</title>
</head>
<body>
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
</body>
</html>
