# Genera VoiceHands_Tecnologias_y_cambios.docx (OOXML) en el escritorio del usuario.
$ErrorActionPreference = 'Stop'
$desktop = [Environment]::GetFolderPath('Desktop')
$outDocx = Join-Path $desktop 'VoiceHands_Tecnologias_y_cambios.docx'
$work = Join-Path $env:TEMP "vh_docx_$(Get-Random)"
New-Item -ItemType Directory -Path $work -Force | Out-Null
$wordDir = Join-Path $work 'word'
$relsDir = Join-Path $work '_rels'
$wordRels = Join-Path $wordDir '_rels'
New-Item -ItemType Directory -Path $relsDir,$wordDir,$wordRels -Force | Out-Null

$documentXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:wpc="http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas" xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:wp14="http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing" xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing" xmlns:w10="urn:schemas-microsoft-com:office:word" xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" xmlns:w14="http://schemas.microsoft.com/office/word/2010/wordml" xmlns:w15="http://schemas.microsoft.com/office/word/2012/wordml" xmlns:wpg="http://schemas.microsoft.com/office/word/2010/wordprocessingGroup" xmlns:wpi="http://schemas.microsoft.com/office/word/2010/wordprocessingInk" xmlns:wne="http://schemas.microsoft.com/office/word/2006/wordml" xmlns:wps="http://schemas.microsoft.com/office/word/2010/wordprocessingShape" mc:Ignorable="w14 wp14">
<w:body>
<w:p><w:r><w:rPr><w:b/><w:sz w:val="32"/></w:rPr><w:t>VoiceHands — Tecnologías integradas y cambios</w:t></w:r></w:p>
<w:p><w:r><w:rPr><w:b/><w:sz w:val="28"/></w:rPr><w:t>1. Tecnologías integradas</w:t></w:r></w:p>
<w:tbl>
<w:tblPr><w:tblW w:w="9000" w:type="dxa"/><w:tblBorders><w:top w:val="single" w:sz="4"/><w:left w:val="single" w:sz="4"/><w:bottom w:val="single" w:sz="4"/><w:right w:val="single" w:sz="4"/><w:insideH w:val="single" w:sz="4"/><w:insideV w:val="single" w:sz="4"/></w:tblBorders></w:tblPr>
<w:tr><w:tc><w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Área</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Tecnología / librería</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Uso en el proyecto</w:t></w:r></w:p></w:tc></w:tr>
<w:tr><w:tc><w:p><w:r><w:t>UI</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Jetpack Compose + Material 3</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Pantallas, tema, Scaffold, barra de navegación, formularios</w:t></w:r></w:p></w:tc></w:tr>
<w:tr><w:tc><w:p><w:r><w:t>Navegación</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Navigation Compose</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Rutas texto_a_senas, senas_a_texto, config</w:t></w:r></w:p></w:tc></w:tr>
<w:tr><w:tc><w:p><w:r><w:t>Cámara / visión</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>CameraX</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Pantalla Señas a texto (captura)</w:t></w:r></w:p></w:tc></w:tr>
<w:tr><w:tc><w:p><w:r><w:t>ML / visión</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>MediaPipe Tasks Vision</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Análisis de manos / postura (HandAnalyzer, assets .task)</w:t></w:r></w:p></w:tc></w:tr>
<w:tr><w:tc><w:p><w:r><w:t>3D</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>SceneView 4.1.1 (Filament)</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Personaje glTF/GLB en Texto a señas (SignAvatar3D)</w:t></w:r></w:p></w:tc></w:tr>
<w:tr><w:tc><w:p><w:r><w:t>Modelo 3D</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Ejemplo Khronos RiggedFigure (GLB)</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>avatar_rigged.glb en assets/models (demostración)</w:t></w:r></w:p></w:tc></w:tr>
<w:tr><w:tc><w:p><w:r><w:t>Build</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>AGP 8.13.2, Kotlin 2.3.20, Compose Compiler, JDK 17</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Compilación; alineación con SceneView 4.1.x</w:t></w:r></w:p></w:tc></w:tr>
<w:tr><w:tc><w:p><w:r><w:t>NDK</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>ABI filters (armeabi-v7a, arm64-v8a, x86, x86_64)</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Bibliotecas nativas en el APK</w:t></w:r></w:p></w:tc></w:tr>
<w:tr><w:tc><w:p><w:r><w:t>Entorno</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Gradle + settings.gradle.kts</w:t></w:r></w:p></w:tc><w:tc><w:p><w:r><w:t>Resolución automática del Android SDK si local.properties es inválido</w:t></w:r></w:p></w:tc></w:tr>
</w:tbl>
<w:p><w:r><w:rPr><w:b/><w:sz w:val="28"/></w:rPr><w:t>2. Cambios realizados</w:t></w:r></w:p>
<w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Texto a señas (TextoASenas.kt, MainActivity.kt)</w:t></w:r></w:p>
<w:p><w:r><w:t>• Pestañas Palabras y Oraciones (TabRow).</w:t></w:r></w:p>
<w:p><w:r><w:t>• Palabras: rejilla de señas comunes; el buscador de la cabecera solo se muestra en esta pestaña.</w:t></w:r></w:p>
<w:p><w:r><w:t>• Oraciones: campo multilínea, botón Traducir, reproducción secuencial por palabra.</w:t></w:r></w:p>
<w:p><w:r><w:t>• Avatar: visor 3D con SceneView (SignAvatar3D) en AvatarLscPanel; rotación suave según AvatarMotion.</w:t></w:r></w:p>
<w:p><w:r><w:t>• Callback onMostrarBuscadorCabecera para ocultar el buscador en la pestaña Oraciones.</w:t></w:r></w:p>
<w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Componentes</w:t></w:r></w:p>
<w:p><w:r><w:t>• Nuevo SignAvatar3D.kt: SceneView, ModelNode, carga del GLB desde assets.</w:t></w:r></w:p>
<w:p><w:r><w:t>• AvatarLsc.kt: enum AvatarMotion y panel; área visual delegada en SignAvatar3D.</w:t></w:r></w:p>
<w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Recursos</w:t></w:r></w:p>
<w:p><w:r><w:t>• app/src/main/assets/models/avatar_rigged.glb — modelo de ejemplo (no sustituye contenido LSC certificado).</w:t></w:r></w:p>
<w:p><w:r><w:rPr><w:b/></w:rPr><w:t>Gradle y repositorio</w:t></w:r></w:p>
<w:p><w:r><w:t>• app/build.gradle.kts: bloque android unificado, NDK en defaultConfig, dependencia SceneView, kotlin { compilerOptions { jvmTarget = JVM_17 } }.</w:t></w:r></w:p>
<w:p><w:r><w:t>• build.gradle.kts (raíz): Kotlin 2.3.20 y plugin Compose misma versión.</w:t></w:r></w:p>
<w:p><w:r><w:t>• settings.gradle.kts: reescritura de sdk.dir si la carpeta no existe.</w:t></w:r></w:p>
<w:p><w:r><w:t>• .gitignore: local.properties y entradas típicas de Android/IDE.</w:t></w:r></w:p>
<w:p><w:r><w:t>• Eliminado app/MainActivity.kt duplicado fuera de src/main.</w:t></w:r></w:p>
<w:p><w:r><w:rPr><w:i/></w:rPr><w:t>Documento generado para el proyecto VoiceHandsV.0.1</w:t></w:r></w:p>
<w:sectPr><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440"/></w:sectPr>
</w:body>
</w:document>
'@

$rels = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>
'@

$docRels = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
<Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/settings" Target="settings.xml"/>
</Relationships>
'@

$stylesXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
<w:docDefaults><w:rPrDefault><w:rPr><w:rFonts w:ascii="Calibri" w:hAnsi="Calibri"/></w:rPr></w:rPrDefault></w:docDefaults>
</w:styles>
'@

$settingsXml = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:settings xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"/>
'@

$contentTypes = @'
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
<Default Extension="xml" ContentType="application/xml"/>
<Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
<Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
<Override PartName="/word/settings.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml"/>
</Types>
'@

$utf8NoBom = New-Object System.Text.UTF8Encoding $false
[System.IO.File]::WriteAllText((Join-Path $wordDir 'document.xml'), $documentXml, $utf8NoBom)
[System.IO.File]::WriteAllText((Join-Path $wordDir 'styles.xml'), $stylesXml, $utf8NoBom)
[System.IO.File]::WriteAllText((Join-Path $wordDir 'settings.xml'), $settingsXml, $utf8NoBom)
[System.IO.File]::WriteAllText((Join-Path $relsDir '.rels'), $rels, $utf8NoBom)
[System.IO.File]::WriteAllText((Join-Path $wordRels 'document.xml.rels'), $docRels, $utf8NoBom)
[System.IO.File]::WriteAllText((Join-Path $work '[Content_Types].xml'), $contentTypes, $utf8NoBom)

if (Test-Path $outDocx) { Remove-Item $outDocx -Force }
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::CreateFromDirectory($work, $outDocx, [System.IO.Compression.CompressionLevel]::Optimal, $false)
try { Remove-Item $work -Recurse -Force -ErrorAction SilentlyContinue } catch {}
Write-Output "CREATED:$outDocx"
