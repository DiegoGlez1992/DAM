(: Obtener el número de puertos de España. :)
for $x in doc("puertos.xml")/puertos
let $y := $x/puerto[pais = "España"]
return <NumeroDePuertosEnEspaña>{count($y)}</NumeroDePuertosEnEspaña>