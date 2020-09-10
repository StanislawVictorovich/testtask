package com.tt.generator.di

import com.tt.generator.VisitLogGenerator

class VisitLogGeneratorAware {
  lazy val generator: VisitLogGenerator = DI.visitLogGenerator
}
