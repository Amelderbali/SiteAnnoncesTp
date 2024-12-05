<html>
<head>
    <title>Recherche d'annonces</title>

    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>

<%@ include file="header.jsp" %>
<h1>Recherche d'annonces</h1>
<form method="GET" action="search">
    <label for="keyword">Mots-clés:</label>
    <input type="text" id="keyword" name="keyword">
    <button type="submit">Rechercher</button>
</form>

<%@ include file="footer.jsp" %>
</body>
</html>
