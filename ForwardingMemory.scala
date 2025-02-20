class ForwardingMemory() extends Module {
    val io = IO(new Bundle {
        val rdAddr = Input(UInt(10.W))
        val rdData = Output(UInt(8.W))
        val wrEnable = Input(Bool())
        val wrAddr = Input(UInt(10.W))
        val wrData = Input(UInt(8.W))
    })
    
    val mem = SyncReadMem(1024, UInt(8.W))
    
    val wrDataReg = RegNext(io.wrData)
    val doForwardReg = RegNext(io.wrAddr === io.rdAddr && io.wrEnabale)
    
    val memData = mem.read(io.rdAddr)
    
    when(io.wrEnable) {
        mem.write(io.wrAddr, io.wrData)
    }
    
    io.rdData := Mux(doForwardReg, wrDataReg, memData)
}