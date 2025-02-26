import chisel3._

class BcdTable extends Module {
    val io = IO(new Bundle {
        val address = Input(UInt(8.W))
        val data = Output(UInt(8.W))
    })
    
    val table = Wire(Vec(100, UInt(8.W)))
    
    // 二进制转换为BCD
    for (i <- 0 until 100) {
        table(i) := (((i/10)<<4) + i%10).U
    }
    
    io.data := table(io.address)
}