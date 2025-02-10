import chisel3._
import chisel3.util._
import _root_.circt.stage.ChiselStage

class Memory extends Module {
    val io = IO(new Bundle {
        val rdAddr = Input(UInt(10.W))
        val rdData = Output(UInt(8.W))
        val wrEnable = Input(Bool())
        val wrAddr = Input(UInt(10.W))
        val wrData = Input(UInt(8.W))
    })
    
    val mem = SyncReadMem(1024, UInt(8.W))
    
    io.rdData := mem.read(io.rdAddr)
    
    when(io.wrEnable) {
        mem.write(io.wrAddr, io.wrData)
    }
}

object MyModule extends App {
  ChiselStage.emitSystemVerilogFile(
    new Memory,
    firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
  )
}

