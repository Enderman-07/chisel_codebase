import chisel3._

class Multiplier(width: Int) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(width.W))
    val b = Input(UInt(width.W))
    val product = Output(UInt(width.W))
  })

  io.product := io.a * io.b
}
