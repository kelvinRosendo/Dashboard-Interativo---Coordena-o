@echo off
echo ==========================================
echo TESTE DE CONFIRMAÇÃO DE DADOS REAIS
echo ==========================================
echo.
echo 1. Verificando segmentos...
mvn test -Dtest=SegmentoTest 2>&1 | findstr "Tests run:"
echo.
echo 2. Verificando usuários...
mvn test -Dtest=UsuarioTest 2>&1 | findstr "Tests run:"
echo.
echo 3. Verificando coordenadoras...
mvn test -Dtest=CoordenadoraTest 2>&1 | findstr "Tests run:"
echo.
echo 4. Verificando semanas em foco...
mvn test -Dtest=SemanaEmFocoTest 2>&1 | findstr "Tests run:"
echo.
echo 5. Verificando cards...
mvn test -Dtest=CardTest 2>&1 | findstr "Tests run:"
echo.
echo 6. Verificando comunicados...
mvn test -Dtest=ComunicadoTest 2>&1 | findstr "Tests run:"
echo.
echo ==========================================
echo RODANDO TODOS OS TESTES DE UMA VEZ...
echo.
mvn clean test 2>&1 | findstr "Tests run:|BUILD"
echo.
echo ==========================================
echo CONFIRMAÇÃO CONCLUÍDA.
pause