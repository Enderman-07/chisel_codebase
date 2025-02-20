// Decoupled Arbiter
import chisel3._
import chisel3.util._
import chisel3.tester._
import chisel3.tester.RawTester.test

object MyModule extends App {
  class MyRoutingArbiter(numChannels: Int) extends Module {
    val io = IO(new Bundle {
      val in = Vec(numChannels, Flipped(Decoupled(UInt(8.W))))
      val out = Decoupled(UInt(8.W))
    } )

    io.out.valid := io.in.map(_.valid).reduce(_ || _)
    val channel = PriorityMux(
        io.in.map(_.valid).zipWithIndex.map { case (valid, index) => (valid, index.U) }
    )
    io.out.bits := io.in(channel).bits
    io.in.map(_.ready).zipWithIndex.foreach { case (ready, index) =>
        ready := io.out.ready && channel === index.U
    }

  }

  test(new MyRoutingArbiter(4)) { c =>
    // 设置初始值
    for(i <- 0 until 4) {
        c.io.in(i).valid.poke(false.B)
        c.io.in(i).bits.poke(i.U)
        c.io.out.ready.poke(true.B)
    }

    c.io.out.valid.expect(false.B)

    // 测试有背压的单输入有效的行为
    for (i <- 0 until 4) {
        c.io.in(i).valid.poke(true.B)
        c.io.out.valid.expect(true.B)
        c.io.out.bits.expect(i.U)

        c.io.out.ready.poke(false.B)
        c.io.in(i).ready.expect(false.B)

        c.io.out.ready.poke(true.B)
        c.io.in(i).valid.poke(false.B)
    }

    // 测试有背压的多输入有效的行为
    c.io.in(1).valid.poke(true.B)
    c.io.in(2).valid.poke(true.B)
    c.io.out.bits.expect(1.U)
    c.io.in(1).ready.expect(true.B)
    c.io.in(0).ready.expect(false.B)

    c.io.out.ready.poke(false.B)
    c.io.in(1).ready.expect(false.B)
  }

  println("SUCCESS!!") // Scala Code: if we get here, our tests passed!
}