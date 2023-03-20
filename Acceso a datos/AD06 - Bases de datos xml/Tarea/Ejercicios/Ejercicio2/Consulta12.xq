(: Obtener la suma del importe de todos los libros que están pendientes. :)
sum(
  for $x in doc("../libros.xml")/bib/libro
  for $y in doc("../librosalmacen.xml")/almacen/pendientes
  where $x/@codigo = $y/codigo
  return
    $x/precio)