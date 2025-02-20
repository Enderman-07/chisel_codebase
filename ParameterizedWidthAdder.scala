import chisel3._
import chisel3.util._
import chisel3.tester._
import chisel3.tester.RawTester.test

object MyModule extends App {
  sealed trait Verbosity
  implicit case object Silent extends Verbosity
  case object Verbose extends Verbosity

  class ParameterizedWidthAdder(in0Width: Int, in1Width: Int, sumWidth: Int)(implicit verbosity: Verbosity)
  extends Module {
    def log(msg: => String): Unit = verbosity match {
      case Silent =>
      case Verbose => println(msg)
    }
    require(in0Width >= 0)
    log(s"// in0Width of $in0Width OK")
    require(in1Width >= 0)
    log(s"// in1Width of $in1Width OK")
    require(sumWidth >= 0)
    log(s"// sumWidth of $sumWidth OK")
    val io = IO(new Bundle {
      val in0 = Input(UInt(in0Width.W))
      val in1 = Input(UInt(in1Width.W))
      val sum = Output(UInt(sumWidth.W))
    })
    log("// Made IO")
    io.sum := io.in0 + io.in1
    log("// Assigned output")
  }
  println(getVerilogString(new ParameterizedWidthAdder(1, 4, 5)))
  println(getVerilogString(new ParameterizedWidthAdder(1, 4, 5)(Verbose)))
}