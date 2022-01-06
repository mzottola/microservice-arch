# Readme

4 applications are created:
* discovery-service: every application registers to it
* gateway: unique point to access other application
* authentication-service: responsible of generating and validating JWTs + getting user information
* foobar-service: a basic spring boot app with a layer of spring security to inject the `Authentication` to benefit fine grained access
