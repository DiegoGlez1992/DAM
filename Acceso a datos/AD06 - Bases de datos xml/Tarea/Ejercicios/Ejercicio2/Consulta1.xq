(: Listar el título de todos los libros. :)
for $x in doc("../libros.xml")/bib/libro
return 
  $x/titulo