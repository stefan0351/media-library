REM sets permissions on Windows Vista and Windows 7
icacls "$INSTALL_PATH" /grant *S-1-5-32-545:(CI)(OI)F /T /Q
