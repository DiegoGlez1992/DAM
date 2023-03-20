(: Listar los libros publicados antes del año 2000. :)
for $x in doc("../libros.xml")/bib/libro
where $x/@año<2000
return 
  $x