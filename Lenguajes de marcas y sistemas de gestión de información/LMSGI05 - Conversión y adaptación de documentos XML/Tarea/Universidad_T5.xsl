<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" version="5.0" encoding="UTF-8" indent="yes"/>
  <xsl:key name="titulacion" match="grado" use="@id_c"/>
  <xsl:template match="/">
    <html lang="es">
      <head>
        <title>Grados Facultad de Ciencias</title>
        <link rel="stylesheet" type="text/css" href="estilo.css"/>
      </head>
      <body>
        <div id="contenedor">
          <br/>
          <h1>Grados de la Facultad de Ciencias</h1>
          <ul>
            <xsl:for-each select="//grado[centro='Facultad de Ciencias']">
              <li>
                  (<xsl:value-of select="@id_c"/>) <strong><xsl:value-of select="nombre"/></strong> - Plan: <xsl:value-of select="plan"/>
                </li>
            </xsl:for-each>
          </ul>
          <br/>
          <br/>
          <h2>Listado de Asignaturas Troncales (2ºT) de la Facultad de Ciencias</h2>
          <table>
            <thead>
              <tr>
                <th>Código</th>
                <th>Nombre</th>
                <th>Titulación</th>
                <th>Créditos teóricos</th>
                <th>Prácticas</th>
                <th>Comentario</th>
              </tr>
            </thead>
            <tbody>
              <xsl:for-each select="//asignaturas/asignatura[(@titulacion=//grado[centro='Facultad de Ciencias']/@id_c) and (trimestre=2) and (caracteristicas/tipo='Troncal')]">
                <xsl:sort select="creditos_teoricos" data-type="number" order="ascending"/>
                <tr>
                  <td>
                    <xsl:value-of select="@id_a"/>
                  </td>
                  <td>
                    <xsl:value-of select="nombre"/>
                  </td>
                  <td>
                    <xsl:value-of select="key('titulacion',@titulacion)/nombre"/>
                  </td>
                  <td>
                    <xsl:value-of select="creditos_teoricos"/>
                  </td>
                  <td>
                    <xsl:value-of select="caracteristicas/practicas"/>
                  </td>
                  <td>
                    <xsl:choose>
                      <xsl:when test="creditos_teoricos &lt; 2">Corta</xsl:when>
                      <xsl:when test="creditos_teoricos &gt;= 2 and creditos_teoricos &lt;= 5">Media</xsl:when>
                      <xsl:when test="creditos_teoricos &gt; 5">Larga</xsl:when>
                      <xsl:otherwise>Sin información</xsl:otherwise>
                    </xsl:choose>
                  </td>
                </tr>
              </xsl:for-each>
            </tbody>
          </table>
        </div>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet>
