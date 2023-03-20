(: Por cada libro, obtener su título y el número de autores, agrupados en un elemento <libro>. :)
for $x in doc("../libros.xml")/bib/libro
return
  <libro>
    {$x/titulo}
    <n_autores>
      {count($x/autor)}
    </n_autores>
  </libro>