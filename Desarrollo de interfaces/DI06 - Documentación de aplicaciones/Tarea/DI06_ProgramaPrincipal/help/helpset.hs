<?xml version="1.0" encoding='ISO-8859-1'?>
<!DOCTYPE helpset PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN" "http://java.sun.com/products/javahelp/helpset_1_0.dtd">

<helpset version="1.0">

	<!-- Titulo -->
	<title>Creación y uso de un componente</title>

	<maps>
		<!-- Pagina por defecto al mostrar la ayuda -->
		<homeID>Manual</homeID>
		<!-- Que mapa deseamos -->
		<mapref location="map.jhm" />
	</maps>

	<!-- Las Vistas que deseamos mostrar en la ayuda -->
	<view>
		<name>Tabla Contenidos</name>	<!-- Nombre de la vista -->
		<label>index</label>	<!-- Etiqueta asociada a la tabla de contenidos -->
		<type>javax.help.TOCView</type>	<!-- Camino a la clase navegador -->
		<data>toc.xml</data>	<!-- Fichero con la tabla de contenidos -->
	</view>

	<view xml:lang="es" mergetype="javax.help.SortMerge">
		<name>Index</name>
		<label>El indice</label>
		<type>javax.help.IndexView</type>
		<data>index.xml</data>
	</view>

	<view xml:lang="es">
		<name>Buscar</name>
		<label>Buscar</label>
		<type>javax.help.SearchView</type>
		<data engine="com.sun.java.help.search.DefaultSearchEngine">
			JavaHelpSearch
		</data>
	</view>

	<presentation default= true>
		<name>Página principal</name>
		<size width="600" height="600" />
		<location x="250" y="250" />
		<title>Ayuda JavaHelp</title>
		<toolbar>
			<helpaction>javax.help.BackAction</helpaction>
			<helpaction>javax.help.ForwardAction</helpaction>
			<helpaction image="homeicon">javax.help.HomeAction</helpaction>
		</toolbar>
	</presentation>

</helpset>