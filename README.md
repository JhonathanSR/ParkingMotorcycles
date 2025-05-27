# 📌 Descripción General
ParkingMotorcycles es una aplicación móvil diseñada para gestionar el registro de motos en un parqueadero, optimizando el control de entradas, salidas y reportes históricos.

## 🎯 Objetivo
Facilitar el registro de motocicletas en parqueaderos, brindando herramientas para:
✔ Registro rápido de motos (placa, marca, hora de ingreso/salida).
✔ Cálculo automático del tiempo de estancia y valor a pagar.
✔ Modo administrador para consultar historiales y generar reportes en PDF.

## 📱 Funcionalidades Principales
1. Registro de Motos
Captura de datos básicos: placa, marca, fecha/hora de ingreso.

Cálculo automático del tiempo estacionado y valor a pagar (según tarifa configurada).

2. Modo Administrador
Consulta de historial: Búsqueda por placa o filtrado por fechas.

Eliminación de registros: Opción para corregir o eliminar datos incorrectos.

Generación de reportes: Exportación a PDF con detalles de todas las motos registradas.

3. Almacenamiento Local
Uso de SQLite para persistencia de datos.

Backup automático en almacenamiento interno (opcionalmente en la nube).

## ⚙️ Tecnologías Utilizadas
Lenguaje: Kotlin (Android Nativo).

Base de Datos: SQLite (Room o SQLiteHelper).

Generación de PDFs: Biblioteca iTextPDF.

Patrón de Diseño: MVVM (Model-View-ViewModel) o MVC.
