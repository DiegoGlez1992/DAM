(: Listar año y título de los libros que tienen más de un autor. :)
for $x in doc("../libros.xml")/bib/libro
where count($x/autor) > 1
return 
  <libro>
    {$x/@año}
    {$x/titulo}
  </libro>