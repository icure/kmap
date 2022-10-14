K-Map is a modern albeit currently limited mapper inspired by map struct.

It uses KSP (https://github.com/google/ksp) to generate kotlin mappers annotated with mapstruct Mapper annotation.

A lot of restrictions exist at this stage:

* Types conversions are limited but easy to add
* All properties have to be set through the constructor
* @Mappings and @Mapping annotation are not supported yet

The main motivation for this project is to have a working solution for generation of mappers for kotlin code heavily reliant on data classes that supports Kotlin 1.4 to 1.7

The project is cut in two subprojects:

1. The mapper-processor
2. The workload that uses the mapper-processor to generate mappers

To launch the mapper-processor you can start the kspKotlin gradle task of the workload.

To easily debug this project, launch the ./debug.sh script in the project directory and connect to it on port 5005 with a remote JVM debugger.
