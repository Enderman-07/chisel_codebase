import chisel3._
import chisel3.util._
import chisel3.experimental.ExtModule

class alt_inbuf extends ExtModule(Map("io_standard" -> "1.0 V",
                                     "location" -> "IOBANK_1",
                                     "enable_bus_hold" -> "on",
                                     "weak_pull_up_resistor" -> "off",
                                     "termination" -> "parallel 50 ohms")) {
    val io = IO(new Bundle {
        val i = Input(Bool())
        val o = Output(Bool())
    })
}

class Top extends Module {
    val io = IO(new Bundle {})
    val inbuf = Module(new alt_inbuf)
}