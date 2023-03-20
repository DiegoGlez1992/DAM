;Autor: Diego González García
;Proyecto: DI07_Tarea

;INCLUIR LIBRERIAS
!include "MUI2.nsh" ;Interfaz de usuario moderna.

;CUESTIONES GENERALES
Name "Tarea DI07" ;Nombre de tu mod. Aparecerá en el texto de la instalación.
OutFile "Tarea DI07.exe" ;Nombre del archivo de salida. Es el nombre del instalador.
Unicode True
InstallDir "C:\Program Files (x86)\Tarea DI07" ;Directorio de instalación por defecto. En adelante podremos hacer referencia a él mediante la variable $INSTDIR
InstallDirRegKey HKCU "Software\Tarea DI07" "" ;Definimos la clave en el registro
RequestExecutionLevel admin ;Requerir los privilegios de usuario . Admite los valores user o admin

;OPCIONES DE LA INTERFAZ
!define MUI_HEADERIMAGE ;Habilitar imagen de cabecera.
!define MUI_HEADERIMAGE_BITMAP "icono.bmp" ;Imagen de cabecera del instalador. Rutas relativas.
;!define MUI_HEADERIMAGE_UNBITMAP "imagen_cabecera.bmp" ;Imagen de cabecera del desinstalador.
!define MUI_ICON "imagen_icon.ico" ;Icono de instalación.
!define MUI_UNICON "imagen_unicon.ico" ;Icono de desinstalación.
!define MUI_ABORTWARNING ;Habilita el aviso de confirmación en el caso de querer anular la instalación.
!define MUI_FINISHPAGE_SHOWREADME ;Habilitar opción 'Ver léeme/View readme' al final de la instalación.

;PÁGINAS
!insertmacro MUI_PAGE_COMPONENTS ;Página para seleccionar los componentes a instalar.
!insertmacro MUI_PAGE_DIRECTORY ;Página para seleccionar el directorio de instalación. Por defecto será el indicado en InstallDir.
;!insertmacro MUI_PAGE_STARTMENU pageid variable ;Página para crear acceso directo en el menú inicio.
!insertmacro MUI_PAGE_INSTFILES ;Página de progreso de la instalación.
!insertmacro MUI_PAGE_FINISH ;Página final.

!insertmacro MUI_UNPAGE_CONFIRM ;Confirmar la desinstalación.
;!insertmacro MUI_UNPAGE_COMPONENTS ;Página para seleccionar los componentes a desinstalar.
;!insertmacro MUI_UNPAGE_DIRECTORY ;Página para seleccionar el directorio de desinstalación. Por defecto será el indicado en InstallDir.
!insertmacro MUI_UNPAGE_INSTFILES ;Página de progreso de la desinstalación.
!insertmacro MUI_UNPAGE_FINISH ;Página final.

;IDIOMAS DEL INSTALADOR
!insertmacro MUI_LANGUAGE "Spanish"

;SECCIONES
Section "Fichero jar" sec1
	;SetOverwrite on ;Habilita la sobreescritura.
	SetOutPath $INSTDIR\lib ;Creamos la carpeta lib en el directorio
	File lib\swing-layout-1.0.4.jar ;Copiamos el contenido
	SetOutPath "$INSTDIR"
	File DI07_Tarea.jar
	File README.TXT
	WriteRegStr HKCU "Software\Tarea DI07" "" $INSTDIR
	WriteUninstaller "$INSTDIR\unistall.exe" ;Crea el desinstalador en este directorio.
SectionEnd
Section "Desinstalar"
	Delete "$INSTDIR\Uninstall.exe"
	;RMDir /r /REBOOTOK "$INSTDIR"  ;Directorio a desinstalar. /REBOOTOK elimina tras reiniciar los archivos que no se pudieron desintalar.
	RMDir "$INSTDIR"
	DeleteRegKey /ifempty HKCU "Software\Tarea DI07"
SectionEnd

;CONSTANTES
LangString DESC_sec1 ${LANG_SPANISH} "Instalacion del fichero DI07 Tarea.jar"

;Activar la función que permite mostrar las descripciones.
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${sec1} $(DESC_sec1)
!insertmacro MUI_FUNCTION_DESCRIPTION_END