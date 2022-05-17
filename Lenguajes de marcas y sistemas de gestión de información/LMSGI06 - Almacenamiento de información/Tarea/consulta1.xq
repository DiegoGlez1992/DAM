(: Obtener el nombre de los puertos de Italia. :)
for $x in doc("puertos.xml")/puertos/puerto
where $x/pais = "Italia"
return 
<puerto>
  {$x/nombre}
</puerto>