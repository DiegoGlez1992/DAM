(: Listar año y título de los libros que tienen no tienen autor. :)
for $x in doc("../libros.xml")/bib/libro
where count($x/autor) = 0
return 
  <libro>
    {$x/@año}
    {$x/titulo}
  </libro>