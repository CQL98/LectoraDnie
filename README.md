# LectoraDnie
Aplicativo Android Lectura Dni electronico peruano
Elementos a usar : Lectora y cable otg
Documentos referencia :

-Documentacion tecnica Reniec: https://pki.reniec.gob.pe/siid/eventos/GuiaReferenciaEspecificacionesTecnicasOperacionesDNIe-v1.0-20150722.pdf

-Especificacion para CCID: https://www.usb.org/sites/default/files/DWG_Smart-Card_CCID_Rev110.pdf

-Hardware usb Android: https://developer.android.com/reference/android/hardware/usb/package-summary

Para poder comunicarse se usa la API de host USB android.hardware.usb
Y los pasos a seguir para la obtención de datos básicos del ciudadano segun reniec:

"El DNIe contiene datos básicos del ciudadano. Estos datos residen en el chip del DNIe, específicamente,
en el registro denominado ABI. Para obtener estos datos, se realiza la siguiente secuencia de pasos.

i. Iniciar contexto PKI

ii. Seleccionar el EF del registro ABI

iii. Lectura e Interpretación del registro ABI"
