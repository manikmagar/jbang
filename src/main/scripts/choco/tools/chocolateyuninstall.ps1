$tools = Split-Path $MyInvocation.MyCommand.Definition
$package = Split-Path $tools
$jbang_home = Join-Path $package 'jbang-0.6.0.5'
$jbang_bat = Join-Path $jbang_home 'bin/jbang.bat'

Uninstall-BinFile -Name 'jbang' -Path $jbang_bat
