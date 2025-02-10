import chisel3._
import chisel3.util._

// 参数化加法器模块
class Adder(val width: Int) extends Module {
  val io = IO(new Bundle {
    val A = Input(UInt(width.W))      // 输入A
    val B = Input(UInt(width.W))      // 输入B
    val Cin = Input(UInt(1.W))        // 进位输入
    val Sum = Output(UInt(width.W))   // 和输出
    val Cout = Output(UInt(1.W))      // 进位输出
  })

  // 使用全加器数组实现加法器
  val FAs = Array.fill(width)(Module(new FullAdder()).io)
  val carry = Wire(Vec(width + 1, UInt(1.W)))
  val sum = Wire(Vec(width, UInt(1.W)))  // 使用 UInt(1.W) 而不是 Bool()

  // 初始化进位
  carry(0) := io.Cin

  // 连接全加器
  for (i <- 0 until width) {
    FAs(i).a := io.A(i)
    FAs(i).b := io.B(i)
    FAs(i).cin := carry(i)
    carry(i + 1) := FAs(i).cout
    sum(i) := FAs(i).sum
  }

  // 输出结果
  io.Sum := sum.asUInt
  io.Cout := carry(width)
}

// 全加器模块
class FullAdder extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(1.W))    // 输入A
    val b = Input(UInt(1.W))    // 输入B
    val cin = Input(UInt(1.W))  // 进位输入
    val sum = Output(UInt(1.W)) // 和输出
    val cout = Output(UInt(1.W)) // 进位输出
  })

  // 全加器逻辑
  io.sum := io.a ^ io.b ^ io.cin
  io.cout := (io.a & io.b) | (io.a & io.cin) | (io.b & io.cin)
}
