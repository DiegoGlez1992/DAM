(: El nombre del crucero, el del puerto de salida y el número total de días de duración de los cruceros que tienen su salida en un puerto de España. :)
for $x in doc("cruceros.xml")/cruceros/crucero
for $y in doc("puertos.xml")/puertos/puerto[pais = "España"]
where $x/recorrido/puerto_salida = $y/nombre
return
<crucero>
  {$x/nombre}
  {$x/recorrido/puerto_salida}
  <dias_crucero>{sum($x/recorrido/etapa/dias)}</dias_crucero>
</crucero>