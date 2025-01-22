package mycsr

import chisel3._
import chisel3.util._

// CSR模块定义
class CSRModule extends Module {
  val io = IO(new Bundle {
    val csrAddr = Input(UInt(12.W))  // CSR地址输入
    val csrWriteData = Input(UInt(32.W))  // CSR写入数据
    val csrWriteEnable = Input(Bool())  // CSR写使能信号
    val csrReadData = Output(UInt(32.W))  // CSR读取数据
  })

  // 定义CSR地址常量
  val CSR_REG1_ADDR = 0x300.U
  val CSR_REG2_ADDR = 0x301.U

  // 定义多个CSR寄存器
  val csrReg1 = RegInit(0.U(32.W))
  val csrReg2 = RegInit(0.U(32.W))

  // CSR写逻辑
  when(io.csrWriteEnable) {
    switch(io.csrAddr) {
      is(CSR_REG1_ADDR) { csrReg1 := io.csrWriteData }
      is(CSR_REG2_ADDR) { csrReg2 := io.csrWriteData }
      // 可以在这里添加默认情况，处理未定义的CSR地址
      // otherwise { /* 处理未定义的CSR地址 */ }
    }
  }

  // CSR读逻辑
  io.csrReadData := MuxLookup(io.csrAddr, 0.U, Seq(
    CSR_REG1_ADDR -> csrReg1,
    CSR_REG2_ADDR -> csrReg2
    // 可以在这里添加更多的CSR寄存器
  ))
}

// 生成Verilog代码的主对象
object CSRModule extends App {
  emitVerilog(new CSRModule(), Array("--target-dir", "generated"))
}
