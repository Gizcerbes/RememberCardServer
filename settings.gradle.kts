
rootProject.name = "MultiDictionary"
include("modules:api")
findProject(":modules:api")?.name = "api"
include("modules:version3")
findProject(":modules:version3")?.name = "version3"
include("modules:testApi")
findProject(":modules:testApi")?.name = "testApi"
