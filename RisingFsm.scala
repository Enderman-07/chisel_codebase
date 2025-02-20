import chisel3._
import chisel3.util._

class RisingFsm extends Moudle {
    val io = IO(new Bundle {
        val din = Input(Bool())
        val risingEdge = Output(Bool())
    })
    
    // 两种状态
    val zero :: one :: Nil = Enum(2)
    
    // 状态寄存器
    val stateReg = RegInit(zero)
    
    // 输出的默认值
    io.risingEdge := false.B
    
    // 状态转换逻辑和输出逻辑
    switch (stateReg) {
        is(zero) {
            when(io.din) {
                stateReg := one
                io.risingEdge := true.B
            }
        }
        is(one) {
            when(!io.din) {
                stateReg := zero
            }
        }
    }
}