class PopCount extends Module {
    val io = IO(new Bundle {
        // 输入数据有效
        val dinValid = Input(Bool())
        // 可以接收数据
        val dinReady = Output(Bool())
        // 输入数据
        val din = Input(UInt(8.W))
        // 输出结果有效
        val popCountValid = Output(Bool())
        // 可以输出数据
        val popCountReady = Input(Bool())
        // 输出结果
        val popCount = Output(UInt(4.W))
    })
    
    // fsm部分
    val fsm = Module(new PopCountFSM)
    // 数据通路部分
    val data = Module(new PopCountDataPath)
    // fsm和顶层接口的连接
    fsm.io.dinValid := io.dinValid
    io.dinReady := fsm.io.dinReady
    io.popCountValid := fsm.io.popCountValid
    fsm.io.popCountReady := io.popCountReady
    // 数据通路和顶层接口的连接
    data.io.din := io.din
    io.popCount := data.io.popCount
    // 数据通路和fsm之间的连接
    data.io.load := fsm.io.load
    fsm.io.done := data.io.done
}


class PopCountDataPath extends Module {
    val io = IO(new Bundle {
        val din = Input(UInt(8.W))
        val load = Input(Bool())
        val popCount = Output(UInt(4.W))
        val done = Output(Bool())
    })
    
    val dataReg = RegInit(0.U(8.W))
    val popCountReg = RegInit(0.U(4.W))
    val counterReg = RegInit(0.U(4.W))
    
    dataReg := 0.U ## dataReg(7, 1)
    popCountReg := popCountReg + dataReg(0)
    
    val done = counterReg === 0.U
    when (!done) {
        counterReg := counterReg - 1.U
    }
    
    when (io.load) {
        dataReg := io.din
        popCountReg := 0.U
        counterReg := 8.U
    }
    
    // 调试用
    printf("%b %d\t", dataReg, popCountReg)
    
    io.popCount := popCountReg
    io.done := done
}


class PopCountFSM extends Module {
    val io = IO(new Bundle {
        val dinValid = Input(Bool())
        val dinReady = Output(Bool())
        val popCountValid = Output(Bool())
        val popCountReady = Input(Bool())
        val load = Output(Bool())
        val done = Input(Bool())
    })
    
    val idle :: count :: done :: Nil = Enum(3)
    val stateReg = RegInit(idle)
    
    io.load := false.B
    
    io.dinReady := false.B
    io.popCountValid := false.B
    
    switch(stateReg) {
        is(idle) {
            io.dinReady := true.B
            when(io.dinValid) {
                io.load := true.B
                stateReg := count
            }
        }
        is(count) {
            when(io.done) {
                stateReg := done
            }
        }
        is(done) {
            io.popCountValid := true.B
            when(io.popCountReady) {
                stateReg := idle
            }
        }
    }
    // 调试用
    printf("state: %b\n", stateReg)
}