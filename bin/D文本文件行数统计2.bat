���C&cls
@echo off
title ���ı��ļ�����ͳ��-�ι�����

	set /a FileNum=0,passFile=0,TotalLine=0
	set out=%~dp0��ϸ��Ϣ.txt
	del /f /a /q "%out%" >nul 2>nul

if not "%~1"=="" echo.&echo ����ͳ�ƣ����Ժ�...&goto Main

:Setting

	set file=C:\???\
	echo.
	echo   �������ļ���Ŀ¼��·��,����ֱ���Ϸŵ��ˣ�
	echo.
	set /p file=
	Call:GetRealPath %file%
	if "%file:~-1%"=="\" set file=%file:~1,-1%
	if not exist "%file%" echo �Ҳ���ָ����·����&&goto Setting
	cls
	echo.&echo ����ͳ�ƣ����Ժ�...&goto Main

:GetRealPath

	set file=%~1

	:AddPath

		shift /1
		if "%~1"=="" goto:eof
		set file=%file% %~1

	goto AddPath

goto:eof

:Main

	if "%file%"=="" set file=%~1
	if not exist "%file%\" Call:Statistic "%file%"&&goto Loop
	for /r "%file%" %%i in (*.*) do Call:Statistic "%%~i"

:Loop
shift /1
set file=
if "%~1"=="" (goto End) else (goto Main)

:Statistic

	set /a passFile+=1
	set w=echo.^&echo ������%passFile%:��^&echo �ļ�-"%~1" ����
	if not exist %1 %w%�ļ����к����������޷�����������ַ���������&goto:eof

	Call:FileSize %1

	if "%IsTooBig%"=="True" %w%̫��������&goto:eof

	set /a Line=0
	for /f "usebackq" %%i in ("%~1") do set /a Line+=1
	
	if %Line%==0 if not %fileSize%==0 if not "%~x1"==".txt" %w%���Ǵ��ı��ļ����Ѻ���&goto:eof

	set /a passFile-=1,FileNum+=1,TotalLine+=%Line%
	echo �ļ�-"%~1" ���� �� %Line% ��>>"%out%"

goto:eof

:FileSize

	set IsTooBig=False
	set fileSize=%~z1
	set foreSize=%fileSize:~0,8%
	set afterSize=%fileSize:~-8%

	if not "%foreSize%"=="%afterSize%" set IsTooBig=True
	if "%foreSize%"=="%afterSize%" if /i %fileSize% GTR 26214400 set IsTooBig=True

goto:eof

:End
	
	echo.&echo ��%FileNum%�����ı��ļ�����Щ�ļ���������Ϊ%TotalLine%��&echo.
	if /i %passFile% GTR 0 echo ������%passFile%���޷�������ļ�...&echo.
	if not exist "%out%" pause&goto:eof
	echo ���������ʾ��ϸ��Ϣ...
	pause>nul
	write "%out%" 2>nul||start "" "%out%"