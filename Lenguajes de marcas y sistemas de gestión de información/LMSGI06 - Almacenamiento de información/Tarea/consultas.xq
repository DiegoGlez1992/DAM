(: Obtener el nombre de los puertos de Italia. :)
for $x in doc("puertos.xml")/puertos/puerto
where $x/pais = "Italia"
return 
<puerto>
  {$x/nombre}
</puerto>

,
(: Obtener el número de puertos de España. :)
for $x in doc("puertos.xml")/puertos
let $y := $x/puerto[pais = "España"]
return <NumeroDePuertosEnEspaña>{count($y)}</NumeroDePuertosEnEspaña>

,
(: El nombre y el recorrido de los cruceros que se organizan cada mes, con un precio base superior seiscientos euros ordenados por su nombre. :)
for $x in doc("cruceros.xml")/cruceros/crucero
where $x/frecuencia = "cada_mes" and number($x/precio/base) > 600
order by $x/nombre
return 
<crucero>
  {$x/nombre}
  {$x/recorrido}
</crucero>

,
(: El nombre y el número de etapas de los cruceros que se organizan “cada_dos_meses”. :)
for $x in doc("cruceros.xml")/cruceros/crucero
let $y := $x/recorrido/etapa
where $x/frecuencia = "cada_dos_meses"
return 
<crucero>
  {$x/nombre}
  <numero_de_etapas>{count($y)}</numero_de_etapas>
</crucero>

,
(: El nombre del crucero, el del puerto de salida y el precio de un camarote exterior (que se calcula añadiendo al precio base, el precio extra por un camarote exterior) de aquellos cruceros que paran o salen de la ciudad de Bari. :)
for $x in doc("cruceros.xml")/cruceros/crucero
where $x/recorrido/puerto_salida = "Bari" or $x/recorrido/etapa/puerto = "Bari"
return
<crucero>
  {$x/nombre}
  {$x/recorrido/puerto_salida}
  <precio_en_camarote_exterior>{$x/precio/base + $x/precio/extra_camarote_exterior}</precio_en_camarote_exterior>
</crucero>

,
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