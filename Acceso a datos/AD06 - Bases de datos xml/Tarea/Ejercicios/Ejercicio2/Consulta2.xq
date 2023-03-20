(: Listar año y título de todos los libros, ordenados por el año. :)
for $x in doc("../libros.xml")/bib/libro
order by $x/@año
return
  <libro>
    {$x/@año} {$x/titulo}
  </libro>