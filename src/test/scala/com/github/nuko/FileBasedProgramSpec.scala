package com.github.nuko

import java.io.{File, FileFilter}

import com.github.scaruby.SFile

class FileBasedProgramSpec extends SpecHelper {
  val directory = new SFile("test-programs")
  describe(s"run Nihongo programs under ${directory}") {
    for(program <- directory.listFiles{file => file.name.endsWith(".nk")}) {
      it(s"program ${program} runs successfully") {
        try {
          E.evaluateFile(program)
          assert(true)
        }catch {
          case e:LanguageException =>
            val line = e.location match {
              case Some(SourceLocation(line, _)) => line
              case None => 0
            }
            Console.err.println(new StackTraceElement("<empty>", "<empty>", program.name, line))
            e.printStackTrace()
            assert(false)
          case e:Throwable =>
            e.printStackTrace()
            assert(false)
        }
      }
    }
  }
}
