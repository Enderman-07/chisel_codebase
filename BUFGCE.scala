import chisel3._
import chisel3.util._

class BUFGCE extends BlackBox(Map("SIM_DEVICE" -> "7SERIES")) {
    val io = IO(new Bundle {
        val I = Input(Clock())
        val CE = Input(Bool())
        val O = Output(Clock())
    })
}

class Top extends Module {
    val io = IO(new Bundle {})
    val bufgce = Module(new BUFGCE)
    // 连接BUFGCE的时钟输入端口到顶层模块的时钟信号
    bufgce.io.I := clock
}