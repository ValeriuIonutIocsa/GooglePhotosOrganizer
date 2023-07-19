@echo off

pushd %~dp0\..\..\..\..\Projects\Personal\GooglePhotosOrganizer\GooglePhotosOrganizer
call gradlew fatJar sourcesJar --console=plain
popd
