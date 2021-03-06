// Using Android Plugin
android.Plugin.androidBuild

// Specifying the Android target Sdk version
platformTarget in Android := "android-23"

// Application Name
name := """manythanks-app"""

// Application Version
version := "1.0.0"

// Scala version
scalaVersion := "2.11.7"

// Repositories for dependencies
resolvers ++= Seq(Resolver.mavenLocal,
  DefaultMavenRepository,
  Resolver.typesafeRepo("releases"),
  Resolver.typesafeRepo("snapshots"),
  Resolver.typesafeIvyRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.defaultLocal
)

val ScalatestVersion = "3.0.0-M7"
val ScalamockVersion = "3.2.2"

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "4.0-RC1" withSources() withJavadoc(),
  "com.google.android.gms" % "play-services" % "8.+",
  "com.android.support" % "appcompat-v7" % "+",
  "com.android.support" % "support-v4" % "23.0.1",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "com.github.kevinsawicki" % "http-request" % "6.0",
  "io.spray" % "spray-json_2.11" % "1.3.2",
//  "com.netflix.rxjava" % "rxjava-core" % "latest.integration",
//  "com.netflix.rxjava" % "rxjava-scala" % "latest.integration" intransitive(),
//  "com.netflix.rxjava" % "rxjava-android" % "latest.integration" intransitive(),
  "io.reactivex" % "rxscala_2.11" % "0.25.0",
  "io.reactivex" % "rxandroid" % "1.0.1",
  "org.scalatest" % "scalatest_2.11"                            % ScalatestVersion % "test",
  "org.scalamock" % "scalamock-scalatest-support_2.11"          % ScalamockVersion % "test",
  "org.robolectric" % "robolectric" % "3.0" % "test",
  "junit" % "junit" % "4.12" % "test"
)

// Override the run task with the android:run
run <<= run in Android

// Activate proguard for Scala
proguardScala in Android := true

// Activate proguard for Android
useProguard in Android := true

// Set proguard options
proguardOptions in Android ++= Seq(
  "-ignorewarnings",
  "-keep class scala.Dynamic")

unmanagedJars in Compile ~= { _ filterNot (_.data.getName startsWith "android-support-v4") }

//proguardCache in Android ++= Seq(
//  ProguardCache("org.scaloid") % "org.scaloid"
//)