@echo off
REM Mostrar el estado actual
git status

REM Hacer un hard reset al commit especificado
git reset --soft 9b1267f76dee97f43c801483491ca4fcd6b3b16a

echo Los registros despues del commit han sido borrados, pero los cambios estan en tu directorio de trabajo.
pause
