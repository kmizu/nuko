package com.github.nuko

import com.github.nuko.Value.*

class BuiltinFunctionSpec extends SpecHelper {
  describe("1を表示") {
    assert(BoxedInt(1) == E("表示(1)"))
  }
}