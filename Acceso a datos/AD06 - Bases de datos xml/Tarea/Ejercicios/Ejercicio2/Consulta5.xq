(: Listar año y título de los libros publicados por Addison-Wesley después del año 1992. :)
for $x in doc("../libros.xml")/bib/libro
where $x/editorial='Addison-Wesley' and $x/@año>1992
return 
  <libro>
    {$x/@año}
    {$x/titulo}
  </libro>