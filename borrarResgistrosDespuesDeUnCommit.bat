@echo off
REM Mostrar el estado actual
git status

REM Hacer un hard reset al commit especificado
git reset --soft ae598be4125832bd854cf221fea464b3d279f73a

echo Los registros despues del commit han sido borrados, pero los cambios estan en tu directorio de trabajo.
pause
