import chisel3._
import chisel3.util._

class RisingMooreFsm extends Module {
    val io = IO(new Bundle {
        val din = Input(Bool())
        val risingEdge = Output(Bool())
    })
    
    // 三种状态
    val zero :: puls :: one :: Nil = Enum(3)
    
    // 状态寄存器
    val stateReg = RegInit(zero)
    
    // 状态转换逻辑
    switch (stateReg) {
        is(zero) {
            when(io.din) {
                stateReg := puls
            }
        }
        is(puls) {
            when(io.din) {
                stateReg := one
            } .otherwise {
                stateReg := zero
            }
        }
        is(one) {
            when(!io.din) {
                stateReg := zero
            }
        }
    }
    
    // 输出逻辑
    io.risingEdge := stateReg === puls
}