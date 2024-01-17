organization := "com.github.kmizu"

name := "nuko"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.13.12"

publishMavenStyle := true

val scaladocBranch = settingKey[String]("branch name for scaladoc -doc-source-url")

scaladocBranch := "main"

Compile / doc / scalacOptions  ++= { Seq(
  "-sourcepath", baseDirectory.value.getAbsolutePath,
  "-doc-source-url", s"https://github.com/kmizu/nuko/tree/${scaladocBranch.value}€{FILE_PATH}.scala"
)}

Test / testOptions += Tests.Argument("-oI")

fork := true

scalacOptions ++= {
  Seq("-unchecked", "-deprecation", "-feature", "-language:implicitConversions")
}

resolvers ++= Seq(
  "Sonatype OSS Releases"  at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

javaOptions += "--add-opens=java.base/java.util=ALL-UNNAMED"

libraryDependencies ++= Seq(
  "com.github.kmizu" %% "scomb" % "0.9.0",
  "com.github.scaruby" %% "scaruby" % "0.6",
  "org.ow2.asm" % "asm" % "5.0.4",
  "junit" % "junit" % "4.13" % "test",
  "org.scalatest" %% "scalatest" %  "3.1.1"
)

libraryDependencies ++= Seq(
  "com.pi4j" % "pi4j-core" % "1.2",
  "com.pi4j" % "pi4j-device" % "1.2",
  "com.pi4j" % "pi4j-gpio-extension" % "1.2",
  "com.pi4j" % "pi4j-service" % "1.1",
  "com.pi4j" % "pi4j-native" % "1.2" pomOnly()
)

assembly / assemblyJarName := "nuko.jar"

assembly / mainClass := Some("com.github.nuko.Main")

console / initialCommands += {
  Iterator(
    "com.github.nuko._",
    "com.github.nuko.Ast._",
    "com.github.nuko.Type._"
  ).map("import "+).mkString("\n")
}

pomExtra := (
  <url>https://github.com/kmizu/nuko</url>
  <licenses>
    <license>
      <name>The MIT License</name>
      <url>http://www.opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:kmizu/nuko.git</url>
    <connection>scm:git:git@github.com:kmizu/nuko.git</connection>
  </scm>
  <developers>
    <developer>
      <id>kmizu</id>
      <name>Kota Mizushima</name>
      <url>https://github.com/kmizu</url>
    </developer>
  </developers>
) 

publishTo := {
  val v = version.value
  val nexus = "https://oss.sonatype.org/"
  if (v.endsWith("-SNAPSHOT"))
    Some("snapshots" at nexus+"content/repositories/snapshots")
  else
    Some("releases" at nexus+"service/local/staging/deploy/maven2")
}

credentials ++= {
  val sonatype = ("Sonatype Nexus Repository Manager", "oss.sonatype.org")
  def loadMavenCredentials(file: java.io.File) : Seq[Credentials] = {
    xml.XML.loadFile(file) \ "servers" \ "server" map (s => {
      val host = (s \ "id").text
      val realm = if (host == sonatype._2) sonatype._1 else "Unknown"
      Credentials(realm, host, (s \ "username").text, (s \ "password").text)
    })
  }
  val ivyCredentials   = Path.userHome / ".ivy2" / ".credentials"
  val mavenCredentials = Path.userHome / ".m2"   / "settings.xml"
  (ivyCredentials.asFile, mavenCredentials.asFile) match {
    case (ivy, _) if ivy.canRead => Credentials(ivy) :: Nil
    case (_, mvn) if mvn.canRead => loadMavenCredentials(mvn)
    case _ => Nil
  }
}