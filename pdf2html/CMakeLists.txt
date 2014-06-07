# leave this above project(pdf2htmlEX)
# set default build type to Release
set(CMAKE_BUILD_TYPE Release CACHE STRING "Build configuration (Debug, Release, RelWithDebInfo, MinSizeRel)")

project(pdf2htmlEX)
cmake_minimum_required(VERSION 2.6.0 FATAL_ERROR)

include_directories(${CMAKE_SOURCE_DIR}/src)

set(PDF2HTMLEX_VERSION "0.8")

file (GLOB datafiles 
    share/base.css
    share/jquery.js
    share/pdf2htmlEX.js
    share/manifest
    )
install (FILES ${datafiles} DESTINATION share/pdf2htmlEX)
install (FILES pdf2htmlEX.1 DESTINATION share/man/man1)
