def delay(x : UInt) = RegNext(x)

class testModule extends Module {
    def delay(x : UInt) = RegNext(x)

    val io = IO(new Bundle {
        val delIn = Input(UInt(32.W))
        val delOut = Output(UInt(32.W))
    })
	// 调用两次delay
    io.delOut := delay(delay(io.delIn))
}