(: El nombre y el número de etapas de los cruceros que se organizan “cada_dos_meses”. :)
for $x in doc("cruceros.xml")/cruceros/crucero
let $y := $x/recorrido/etapa
where $x/frecuencia = "cada_dos_meses"
return 
<crucero>
  {$x/nombre}
  <numero_de_etapas>{count($y)}</numero_de_etapas>
</crucero>