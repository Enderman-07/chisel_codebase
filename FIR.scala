// FIR滤波器生成器
import chisel3._
import chisel3.util._

class MyModule(length: Int) extends Module {
  val io = IO(new Bundle {
    val in  = Input(UInt(8.W))
    val valid = Input(Bool())
    val consts = Input(Vec(length, UInt(8.W))) // 后面会提到的用法
    val out = Output(UInt(8.W))
  })

  // 后面才会提到的用法
  val taps = Seq(io.in) ++ Seq.fill(io.consts.length - 1)(RegInit(0.U(8.W)))
  taps.zip(taps.tail).foreach { case (a, b) => when (io.valid) { b := a } }

  io.out := taps.zip(io.consts).map { case (a, b) => a * b }.reduce(_ + _)
}

object MyModule extends App {
  println(getVerilogString(new MyModule(4)))
}