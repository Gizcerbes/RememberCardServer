
rootProject.name = "MultiDictionary"
include("modules:version3")
findProject(":modules:version3")?.name = "version3"
include("modules:testApi")
findProject(":modules:testApi")?.name = "testApi"
include("modules:static")
findProject(":modules:static")?.name = "static"
include("modules:test")
findProject(":modules:test")?.name = "test"
