(: El nombre del crucero, el del puerto de salida y el precio de un camarote exterior (que se calcula añadiendo al precio base, el precio extra por un camarote exterior) de aquellos cruceros que paran o salen de la ciudad de Bari. :)
for $x in doc("cruceros.xml")/cruceros/crucero
where $x/recorrido/puerto_salida = "Bari" or $x/recorrido/etapa/puerto = "Bari"
return
<crucero>
  {$x/nombre}
  {$x/recorrido/puerto_salida}
  <precio_en_camarote_exterior>{$x/precio/base + $x/precio/extra_camarote_exterior}</precio_en_camarote_exterior>
</crucero>