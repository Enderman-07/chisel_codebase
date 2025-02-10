package simple

import chisel3._
import chisel3.util._

/**
 * ALU模块模板
 * 支持加法、减法、按位或、按位与四种操作
 */
class ALU extends Module {
  val io = IO(new Bundle {
    val fn = Input(UInt(2.W))  // 功能选择信号
    val a  = Input(UInt(4.W))  // 输入A
    val b  = Input(UInt(4.W))  // 输入B
    val result = Output(UInt(4.W))  // 输出结果
  })

  // 默认输出值
  io.result := 0.U

  // 根据功能选择信号执行操作
  switch(io.fn) {
    is(0.U) { io.result := io.a + io.b }  // 加法
    is(1.U) { io.result := io.a - io.b }  // 减法
    is(2.U) { io.result := io.a | io.b }  // 按位或
    is(3.U) { io.result := io.a & io.b }  // 按位与
  }
}

/**
 * 顶层模块，用于连接FPGA的开关和LED
 */
class ALUTop extends Module {
  val io = IO(new Bundle {
    val sw  = Input(UInt(10.W))  // 开关输入
    val led = Output(UInt(10.W)) // LED输出
  })

  // 实例化ALU模块
  val alu = Module(new ALU())

  // 将开关输入映射到ALU的输入端口
  alu.io.fn := io.sw(1, 0)  // 使用开关的低两位作为功能选择信号
  alu.io.a  := io.sw(5, 2)  // 使用开关的中间四位作为输入A
  alu.io.b  := io.sw(9, 6)  // 使用开关的高四位作为输入B

  // 将ALU的输出结果映射到LED，只使用低4位
  io.led := Cat(0.U(6.W), alu.io.result)
}

/**
 * 生成Verilog代码的主程序
 */
object ALUMain extends App {
  println("Generating the ALU hardware")
  (new chisel3.stage.ChiselStage).emitVerilog(new ALUTop(), Array("--target-dir", "generated"))
}
