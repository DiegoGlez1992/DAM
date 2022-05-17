(: El nombre y el recorrido de los cruceros que se organizan cada mes, con un precio base superior seiscientos euros ordenados por su nombre. :)
for $x in doc("cruceros.xml")/cruceros/crucero
where $x/frecuencia = "cada_mes" and number($x/precio/base) > 600
order by $x/nombre
return 
<crucero>
  {$x/nombre}
  {$x/recorrido}
</crucero>