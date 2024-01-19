import korlibs.korge.gradle.*
import korlibs.kotlin
import org.jetbrains.kotlin.gradle.kpm.external.project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(libs.plugins.korge)
}

korge {
	id = "com.sample.demo"

// To enable all targets at once

	//targetAll()

// To enable targets based on properties/environment variables
	//targetDefault()

// To selectively enable targets
	
	targetJvm()
	targetJs()
	targetDesktop()
	targetIos()
	targetAndroid()

	serializationJson()

    entrypoint("Scrap", "ScrapMain")
}


dependencies {
    add("commonMainApi", project(":deps"))
    add("jvmMainApi", "com.google.firebase:firebase-admin:9.2.0")
//    add("commonMainApi", "dev.gitlive:firebase-auth:1.8.0")
    //add("commonMainApi", project(":korge-dragonbones"))
}
