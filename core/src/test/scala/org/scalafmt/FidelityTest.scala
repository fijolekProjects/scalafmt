package org.scalafmt

import org.scalafmt.util.FileOps
import org.scalafmt.util.FormatAssertions
import org.scalatest.FunSuite

/**
  * Asserts formatter does not alter original source file's AST.
  *
  * Will maybe use scalacheck someday.
  */
class FidelityTest extends FunSuite with FormatAssertions {

  case class Test(filename: String, code: String)

  object Test {

    def apply(filename: String): Test =
      Test(filename, FileOps.readFile(filename))
  }

  val files = FileOps
    .listFiles(".")
    .filter(_.endsWith(".scala"))
    .filterNot(_.contains("/target/"))
    .filterNot(_.contains("/resources/"))

  val examples = files.map(Test.apply)

  examples.foreach { example =>
    test(example.filename) {
      val formatted = ScalaFmt.format_!(example.code, ScalaStyle.UnitTest80)(
          scala.meta.parseSource)
      assertFormatPreservesAst(example.code, formatted)(scala.meta.parseSource)
      println(example.filename)
    }
  }
}
