import org.gradle.api.JavaVersion

object Versions {
    object Kotlin {
        const val lang = "1.7.10"
        const val coroutines = "1.6.4"
        const val serialization = "1.4.0"
    }

    object Java {
        const val jvmTarget = "17"
        val java = JavaVersion.VERSION_17
    }

    const val ksp = "${Kotlin.lang}-1.0.6"
    const val agp = "7.2.2"
    const val spotless = "6.11.0"
    const val ktlint = "0.45.2"
    const val okhttp = "4.10.0"
    const val retrofit2 = "2.9.0"
    const val hson = "0.1.4"
    const val compose = "1.3.0-beta03"
    const val compose_jb = "1.2.0-beta01"
    const val paging = "3.2.0-alpha02"
    const val activity = "1.6.0"
    const val datastore = "1.0.0"
    const val room = "2.5.0-alpha03"
    const val lifecycle = "2.6.0-alpha02"
    const val work = "2.8.0-alpha04"
    const val startup = "1.2.0-alpha01"
    const val coil = "2.2.1"
    const val accompanist = "0.26.4-rc"
    const val accompanist_jb = "0.25.2"
    const val androidx_exifinterface = "1.3.3"
    const val exoplayer = "2.18.1"
    const val browser = "1.4.0"
    const val protobuf = "3.20.0"
    const val androidx_test = "1.5.0-alpha02"
    const val extJUnitVersion = "1.1.4-alpha07"
    const val espressoVersion = "3.5.0-alpha07"
    const val koin = "3.2.2"
    const val moko = "0.20.1"
    const val sqlDelight = "1.5.3"
    const val javafx = "0.0.13"
    const val kFilePicker = "1.0.4"
    const val jodaTime = "2.11.2"
    const val cache4k  = "0.8.0"
    const val precompose = "1.3.8"
    const val precomposeKsp = "1.0.1"
}
