(: Listar los libros cuyo precio sea 65.95. :)
for $x in doc("../libros.xml")/bib/libro
where $x/precio=65.95
return 
  $x