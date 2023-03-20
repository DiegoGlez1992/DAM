(: Una lista ordenada de autores que tengan libros pendientes. La última línea contendrá una línea que tenga el total de autores. :)
for $x in doc("../libros.xml")/bib/libro
for $y in doc("../librosalmacen.xml")/almacen
where $x/@codigo = $y/pendientes/codigo and $x/autor
order by $x
return
  <autores_libros_pendientes>
    {$x/autor}
    <total_autores>{count($x/autor)}</total_autores>
  </autores_libros_pendientes>