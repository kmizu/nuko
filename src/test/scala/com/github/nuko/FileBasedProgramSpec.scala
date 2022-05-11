package com.github.nuko

import java.io.{File, FileFilter}

import com.github.scaruby.SFile

class FileBasedProgramSpec extends SpecHelper {
  val directory = new SFile("test-programs")
  describe(s"run Nihongo programs under ${directory}") {
    for(program <- directory.listFiles{file => file.name.endsWith(".ni")}) {
      it(s"program ${program} runs successfully") {
        try {
          E.evaluateFile(program)
          assert(true)
        }catch {
          case e:Throwable =>
            e.printStackTrace()
            assert(false)
        }
      }
    }
  }
}
