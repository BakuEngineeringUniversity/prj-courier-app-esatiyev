import org.apache.tools.ant.util.JavaEnvUtils.VERSION_1_8

buildscript {
    val agp_version by extra("7.0.0")
    val agp_version1 by extra("8.1.2")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
//    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
//    id("com.google.devtools.ksp") version "1.8.21-1.0.11" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false

}
val buildToolsVersion by extra("34.0.0")
val compileSdkVersion by extra(33)
val compileSdkVersion1 by extra(30)
val targetCompatibility by extra(VERSION_1_8)
val sourceCompatibility by extra(VERSION_1_8)
val sourceCompatibility1 by extra(VERSION_1_8)
val buildToolsVersion1 by extra(buildToolsVersion)
val targetCompatibility1 by extra("1.8")
val sourceCompatibility2 by extra("1.8")
val buildToolsVersion2 by extra(buildToolsVersion1)
