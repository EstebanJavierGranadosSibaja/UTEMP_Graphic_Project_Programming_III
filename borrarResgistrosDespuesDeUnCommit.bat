@echo off
REM Mostrar el estado actual
git status

REM Hacer un hard reset al commit especificado
git reset --soft 022f88e24ed5cfd5350fef757a10cd7b101bc915

echo Los registros despues del commit han sido borrados, pero los cambios estan en tu directorio de trabajo.
pause
