(: Una lista ordenada alfabéticamente de libros comprados. :)
for $x in doc("../libros.xml")/bib/libro
for $y in doc("../librosalmacen.xml")/almacen
where $x/@codigo = $y/comprados/codigo
order by $x/titulo
return
  $x
