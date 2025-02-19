import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.grgit)
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")

group = "org.cru.mobile.fork.co.brainly"
version = "${version}_${grgit.log { includes = listOf("HEAD") }.size}"

gradlePlugin {
    plugins {
        register("onesky-gradle") {
            id = "org.cru.mobile.fork.co.brainly.onesky"
            displayName = "OneSky Gradle Plugin"
            implementationClass = "co.brainly.onesky.OneSkyPlugin"
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        languageVersion = "1.7"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    additionalEditorconfigFile.set(File(projectDir, ".editorconfig"))

    enableExperimentalRules.set(true)

    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
    }

    filter {
        exclude("**/generated/**")
        exclude("src/test/**/*.kt", "src/androidTest/**/*.kt")
        include("src/**/*.kt")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleApi())

    val okHttp = "4.8.1"
    implementation("com.squareup.okhttp3:okhttp:$okHttp")
    val moshi = "1.9.3"
    implementation("com.squareup.moshi:moshi:$moshi")
    implementation("com.squareup.moshi:moshi-kotlin:$moshi")

    implementation("com.jakewharton.picnic:picnic:0.4.0") {
        because("0.5.0 crashes on Table.renderText() call")
    }

    testImplementation(gradleTestKit())
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testImplementation("org.assertj:assertj-core:3.11.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okHttp")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "cruGlobalMavenRepository"
            setUrl("https://cruglobal.jfrog.io/artifactory/maven-cru-mobile-forks-local/")
            credentials(PasswordCredentials::class)
        }
    }
}
