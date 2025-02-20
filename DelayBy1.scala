import chisel3._
import chisel3.util._
import chisel3.tester._
import chisel3.tester.RawTester.test


class DelayBy1(resetValue: Option[UInt] = None) extends Module {
  val io = IO(new Bundle {
    val in  = Input( UInt(16.W))
    val out = Output(UInt(16.W))
  })
  val reg = if (resetValue.isDefined) { // resetValue = Some(number)
    RegInit(resetValue.get)
  } else { //resetValue = None
    Reg(UInt())
  }
  reg := io.in
  io.out := reg
}

object MyModule extends App {
  println(getVerilogString(new DelayBy1))
  println(getVerilogString(new DelayBy1(Some(3.U))))
}