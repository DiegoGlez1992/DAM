(: Por cada libro, listar agrupado en un elemento <result> su titulo y autores. :)
for $x in doc("../libros.xml")/bib/libro
return
  <result>
    {$x/titulo}
    {$x/autor}
  </result>